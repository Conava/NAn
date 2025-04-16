<template>
  <div id="app">
    <h1>CSV Data Viewer</h1>

    <!-- Show table if we have any data -->
    <div v-if="tableData.length">
      <table>
        <thead>
        <tr>
          <!-- Use the first row of CSV as the headers -->
          <th v-for="(header, index) in tableData[0]" :key="index">
            {{ header }}
          </th>
        </tr>
        </thead>
        <tbody>
        <!-- Loop through each row after the header (starting from index 1) -->
        <tr v-for="(row, rowIndex) in tableData.slice(1)" :key="rowIndex">
          <!-- Loop through each cell in the row -->
          <td v-for="(cell, cellIndex) in row" :key="cellIndex">
            {{ cell }}
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- If there's no data yet, show a loading message -->
    <p v-else>Loading data...</p>
  </div>
</template>

<script setup>
// Vue composition API: we use ref to create a reactive variable
import { ref, onMounted } from 'vue'
// PapaParse is a library to parse CSV into usable data
import Papa from 'papaparse'

// This will hold our parsed CSV data
const tableData = ref([])

// Run this code when the component is mounted (i.e., page loads)
onMounted(async () => {
  try {
    // Fetch the CSV file from the backend
    const response = await fetch('http://localhost:8080/api/data')
    // Convert the response to text (raw CSV)
    const csvText = await response.text()

    // Use PapaParse to convert CSV text to a JavaScript array
    Papa.parse(csvText, {
      complete: (results) => {
        // Save the parsed data so it triggers reactivity in the table
        tableData.value = results.data
      }
    })
  } catch (error) {
    // Log errors if something goes wrong (e.g., network issues)
    console.error('Failed to fetch CSV:', error)
  }
})
</script>

<style>
/* Basic styling for the page and table */
body {
  font-family: Arial, sans-serif;
  margin: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 8px;
  text-align: left;
}
</style>