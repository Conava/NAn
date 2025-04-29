<template>
  <div class="p-4">
    <!-- Home / Scan Screen -->
    <div v-if="!showSettings">
      <h1 class="text-2xl font-bold mb-4">Network Analyzer</h1>

      <!-- Scan Controls -->
      <button
          @click="runScan"
          class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
      >
        Run Scan
      </button>

      <!-- Scan Results -->
      <h2 class="mt-6 text-xl font-semibold">Scan Results</h2>
      <table
          v-if="results.length"
          class="w-full mt-2 table-auto border-collapse"
      >
        <thead>
        <tr class="bg-gray-100">
          <th
              v-for="col in columns"
              :key="col"
              class="px-3 py-1 text-left border-b"
          >
            {{ col }}
          </th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="(row, rowIndex) in results"
            :key="rowIndex"
            class="odd:bg-white even:bg-gray-50"
        >
          <td
              v-for="col in columns"
              :key="col"
              class="px-3 py-1 border-b"
          >
            {{ row[col] }}
          </td>
        </tr>
        </tbody>
      </table>
      <p v-else class="mt-2 text-gray-500">No data yet.</p>

      <!-- Go to Settings Screen -->
      <button
          @click="showSettings = true"
          class="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Settings
      </button>
    </div>

    <!-- Settings “Page” -->
    <div v-else>
      <div class="max-w-2xl mx-auto">
        <h1 class="text-2xl font-bold mb-4">Application Settings</h1>
        <SettingsPanel />
        <button
            @click="showSettings = false"
            class="mt-4 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
        >
          Back
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import axios from 'axios'
import SettingsPanel from './components/SettingsPanel.vue'

const showSettings = ref(false)

// **NEW**: results + columns for dynamic JSON table
const results = ref<Record<string, any>[]>([])
const columns = ref<string[]>([])

async function runScan() {
  try {
    // 1) Trigger the one‐off scan
    await axios.get('http://localhost:8080/api/scan', {
      withCredentials: true
    })

    // 2) Fetch the new data from the JSON file via your API
    const res = await axios.get<Record<string, any>[]>(
        'http://localhost:8080/api/monitor/data',
        { withCredentials: true }
    )

    // 3) Store rows and derive column headers
    results.value = res.data
    columns.value = res.data.length
        ? Object.keys(res.data[0])
        : []

  } catch (err) {
    console.error('Scan failed:', err)
  }
}
</script>