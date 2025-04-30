<template>
  <div class="p-4 min-h-screen">
    <!-- Search Bar (fixed top-right, only after scan) -->
    <input
        v-if="hasScanned && !showSettings"
        v-model="searchTerm"
        type="text"
        placeholder="Filter results…"
        class="fixed top-4 right-4 border rounded p-2 w-64 z-50"
    />

    <!-- Home / Scan Screen -->
    <div v-if="!showSettings">
      <h1 class="text-2xl font-bold mb-4">Network Analyzer</h1>

      <!-- Buttons Row: Run Scan, Settings -->
      <div class="flex items-center mb-4">
        <button
            @click="runScan"
            :disabled="isLoading"
            class="mr-4 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
        >
          Run Scan
        </button>
        <button
            @click="showSettings = true"
            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Settings
        </button>
      </div>

      <!-- DB Search Row -->
      <div class="flex items-center mb-4">
        <input
            v-model="dbQuery"
            type="text"
            placeholder="Search database…"
            class="mr-2 border rounded p-2 w-64"
        />
        <button
            @click="searchDb"
            :disabled="isLoading"
            class="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:opacity-50"
        >
          Search DB
        </button>
      </div>

      <!-- Loading Spinner -->
      <div v-if="isLoading" class="flex justify-center mt-6 mb-4">
        <svg
            class="animate-spin text-green-600"
            style="width:1.6px; height:1.6px;"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
        >
          <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
          />
          <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8v8z"
          />
        </svg>
      </div>

      <!-- Scan / Search Results -->
      <div v-if="hasScanned">
        <h2 class="text-xl font-semibold mb-2">Scan Results</h2>

        <table
            v-if="filteredResults.length"
            class="w-full table-auto border-collapse"
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
              v-for="(row, rowIndex) in filteredResults"
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
        <p v-else class="text-gray-500">No matching networks.</p>
      </div>
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
import { ref, computed } from 'vue'
import axios from 'axios'
import SettingsPanel from './components/SettingsPanel.vue'

const showSettings = ref(false)
const results = ref<Record<string, any>[]>([])
const columns = ref<string[]>([])

// Flags
const hasScanned = ref(false)
const isLoading = ref(false)

// Local client filter
const searchTerm = ref('')
const filteredResults = computed(() => {
  if (!searchTerm.value) return results.value
  const term = searchTerm.value.toLowerCase()
  return results.value.filter(row =>
      Object.values(row).some(
          val => typeof val === 'string' && val.toLowerCase().includes(term)
      )
  )
})

// DB query term
const dbQuery = ref('')

async function runScan() {
  try {
    isLoading.value = true
    await axios.get('http://localhost:8080/api/scan', { withCredentials: true })
    const res = await axios.get<Record<string, any>[]>(
        'http://localhost:8080/api/monitor/data',
        { withCredentials: true }
    )
    results.value = res.data
    columns.value = res.data.length ? Object.keys(res.data[0]) : []
    hasScanned.value = true
  } catch (err) {
    console.error('Scan failed:', err)
  } finally {
    isLoading.value = false
  }
}

async function searchDb() {
  try {
    isLoading.value = true
    const res = await axios.get<Record<string, any>[]>(
        'http://localhost:8080/api/mongo/search',
        {
          params: { q: dbQuery.value },
          withCredentials: true
        }
    )
    results.value = res.data
    columns.value = res.data.length ? Object.keys(res.data[0]) : []
    hasScanned.value = true
  } catch (err) {
    console.error('DB search failed:', err)
  } finally {
    isLoading.value = false
  }
}
</script>