<template>
  <div>
    <table>
      <thead>
      <tr>
        <th v-for="column in columns" :key="column">{{ column }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(row, index) in rows" :key="index">
        <td>{{ row.MAC }}</td>
        <td>{{ row.SSID }}</td>
        <td>{{ row.SignalStrength }}</td>
        <td>{{ row.RadioType }}</td>
        <td>{{ row.Band }}</td>
        <td>{{ row.ConnectedStations }}</td>
        <td>{{ row.ChannelUtilization }}</td>
        <td>{{ row.Authentication }}</td>
        <td>{{ row.Encryption }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Define table column headers
const columns = ref([
  "MAC", "SSID", "Signal Strength", "Radio Type", "Band",
  "Connected Stations", "Channel Utilization", "Authentication", "Encryption"
]);

// Placeholder data (Replace with API data later)
const rows = ref([
  { MAC: "00:1A:2B:3C:4D:5E", SSID: "Network1", SignalStrength: "-40dBm", RadioType: "802.11ac", Band: "5GHz", ConnectedStations: 5, ChannelUtilization: "30%", Authentication: "WPA2", Encryption: "AES" },
  { MAC: "00:1A:2B:3C:4D:5F", SSID: "Network2", SignalStrength: "-50dBm", RadioType: "802.11n", Band: "2.4GHz", ConnectedStations: 3, ChannelUtilization: "40%", Authentication: "WPA3", Encryption: "AES" },
  { MAC: "00:1A:2B:3C:4D:60", SSID: "Network3", SignalStrength: "-60dBm", RadioType: "802.11ax", Band: "5GHz", ConnectedStations: 8, ChannelUtilization: "50%", Authentication: "WPA2", Encryption: "TKIP" }
]);
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
div {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* Aligns content to the top */
  justify-content: flex-start;
  width: 100%;
}
</style>