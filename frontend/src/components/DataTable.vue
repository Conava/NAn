<template>
  <div>
    <table>
      <thead>
      <tr>
        <th v-for="column in columns" :key="column">{{ column }}</th>
      </tr>
      </thead>
    </table>
  </div>
</template>

<script setup lang="ts">
const columns = ["MAC", "SSID", "Signal Strength", "Radio Type", "Band", "Connected Stations", "Channel Utilization", "Authentication", "Encryption"]
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ddd;
}
th {
  padding: 10px;
  background-color: #f4f4f4;
  border: 1px solid #ddd;
  text-align: left;
}
</style>