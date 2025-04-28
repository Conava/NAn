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
    console.log("Starting scan...");

    // Call /api/scan to trigger a scan
    await axios.get('http://localhost:8080/api/scan', {
      withCredentials: true
    });

    // Then fetch the monitoring data
    const res = await axios.get('http://localhost:8080/api/monitor/data', {
      withCredentials: true
    });

    results.value = res.data;
    console.log("Scan complete:", res.data);

  } catch (err) {
    console.error('Scan failed:', err);
  }
}
</script>