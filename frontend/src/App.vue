<template>
  <div class="p-4">
    <!-- Home / Scan Screen -->
    <div v-if="!showSettings">
      <h1 class="text-2xl font-bold mb-4">Network Analyzer GUI</h1>

      <!-- Scan Controls -->
      <button
          @click="runScan"
          class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
      >
        Run Scan
      </button>

      <!-- Scan Results -->
      <h2 class="mt-6 text-xl font-semibold">Scan Results</h2>
      <table v-if="results.length" class="w-full mt-2 border">
        <thead class="bg-gray-100">
        <tr>
          <th v-for="(val, key) in results[0]" :key="key" class="px-3 py-1 text-left">
            {{ key }}
          </th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="(item, idx) in results"
            :key="idx"
            class="odd:bg-white even:bg-gray-50"
        >
          <td v-for="(val, key) in item" :key="key" class="px-3 py-1">
            {{ val }}
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

        <!-- Reuse your SettingsPanel component -->
        <SettingsPanel />

        <!-- Back to Scan Screen -->
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

const results = ref<any[]>([])
const showSettings = ref(false)

async function runScan() {
  try {
    await axios.get('http://localhost:8080/api/scan', { withCredentials: true })
    const res = await axios.get('http://localhost:8080/api/monitor/data', { withCredentials: true })
    results.value = res.data
  } catch (err) {
    console.error('Scan failed:', err)
  }
}
</script>