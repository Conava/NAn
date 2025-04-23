<template>
  <div class="p-4">
    <h1>Network Analyzer GUI</h1>
    <button @click="runScan">Run Scan</button>

    <h2>Scan Results</h2>
    <table v-if="results.length">
      <thead>
      <tr>
        <th v-for="(val, key) in results[0]" :key="key">{{ key }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in results" :key="index">
        <td v-for="(val, key) in item" :key="key">{{ val }}</td>
      </tr>
      </tbody>
    </table>
    <p v-else>No data yet.</p>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import axios from 'axios'

const results = ref<any[]>([])

async function runScan() {
  try {
    await axios.get('http://localhost:8080/api/scan') // Start scan
    const res = await axios.get('http://localhost:8080/api/monitor/data') // Get results
    results.value = res.data
  } catch (err) {
    console.error('Scan failed:', err)
  }
}
</script>