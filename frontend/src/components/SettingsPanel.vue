<template>
  <div class="p-4 max-w-2xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">Application Settings</h1>
    <form @submit.prevent="saveSettings" class="space-y-4">
      <!-- Base Directory -->
      <div>
        <label class="block font-medium">Base Directory</label>
        <input
            v-model="settings.baseDir"
            type="text"
            class="mt-1 w-full border rounded p-2"
        />
      </div>

      <!-- Data Storage -->
      <div>
        <label class="block font-medium">Data Storage</label>
        <input
            v-model="settings.dataStorage"
            type="text"
            class="mt-1 w-full border rounded p-2"
        />
      </div>

      <!-- Log File -->
      <div>
        <label class="block font-medium">Log File</label>
        <input
            v-model="settings.logFile"
            type="text"
            class="mt-1 w-full border rounded p-2"
        />
      </div>

      <!-- Booleans -->
      <div class="flex items-center space-x-4">
        <label class="flex items-center">
          <input v-model="settings.defaultUseGps" type="checkbox" class="mr-2" />
          Default Use GPS
        </label>
        <label class="flex items-center">
          <input v-model="settings.keepHistory" type="checkbox" class="mr-2" />
          Keep History
        </label>
        <label class="flex items-center">
          <input v-model="settings.activateGui" type="checkbox" class="mr-2" />
          Activate GUI
        </label>
      </div>

      <!-- DB Settings -->
      <fieldset class="border rounded p-3">
        <legend class="font-medium">Database</legend>
        <div class="flex items-center mb-2">
          <input v-model="settings.db.remoteEnabled" type="checkbox" id="dbEnabled" class="mr-2" />
          <label for="dbEnabled">Remote Enabled</label>
        </div>
        <div>
          <label class="block font-medium">Remote URI</label>
          <input
              v-model="settings.db.remoteUrl"
              type="text"
              class="mt-1 w-full border rounded p-2"
          />
        </div>
      </fieldset>

      <!-- Monitor Settings -->
      <fieldset class="border rounded p-3">
        <legend class="font-medium">Monitor</legend>
        <div class="mb-2">
          <label class="block font-medium">Scan Interval (seconds)</label>
          <input
              v-model.number="settings.monitor.scanInterval"
              type="number"
              min="1"
              class="mt-1 w-full border rounded p-2"
          />
        </div>
        <div class="flex items-center space-x-4 mb-2">
          <label class="flex items-center">
            <input v-model="settings.monitor.gpsOn" type="checkbox" class="mr-2" />
            GPS On
          </label>
          <label class="flex items-center">
            <input v-model="settings.monitor.kmlOutput" type="checkbox" class="mr-2" />
            KML Output
          </label>
          <label class="flex items-center">
            <input v-model="settings.monitor.csvOutput" type="checkbox" class="mr-2" />
            CSV Output
          </label>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block font-medium">JSON Filename</label>
            <input
                v-model="settings.monitor.jsonFileName"
                type="text"
                class="mt-1 w-full border rounded p-2"
            />
          </div>
          <div>
            <label class="block font-medium">KML Filename</label>
            <input
                v-model="settings.monitor.kmlFileName"
                type="text"
                class="mt-1 w-full border rounded p-2"
            />
          </div>
          <div>
            <label class="block font-medium">CSV Filename</label>
            <input
                v-model="settings.monitor.csvFileName"
                type="text"
                class="mt-1 w-full border rounded p-2"
            />
          </div>
        </div>
      </fieldset>

      <!-- Save Button -->
      <button
          type="submit"
          class="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Save Settings
      </button>

      <p v-if="message" class="mt-2 text-green-600">{{ message }}</p>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// TypeScript interface matching your SettingsDTO
interface SettingsDTO {
  baseDir: string
  dataStorage: string
  defaultUseGps: boolean
  keepHistory: boolean
  activateGui: boolean
  logFile: string
  db: {
    remoteEnabled: boolean
    remoteUrl: string
  }
  monitor: {
    scanInterval: number
    gpsOn: boolean
    kmlOutput: boolean
    csvOutput: boolean
    jsonFileName: string
    kmlFileName: string
    csvFileName: string
  }
}

const settings = ref<SettingsDTO>({
  baseDir: '',
  dataStorage: '',
  defaultUseGps: false,
  keepHistory: false,
  activateGui: false,
  logFile: '',
  db: { remoteEnabled: false, remoteUrl: '' },
  monitor: {
    scanInterval: 60,
    gpsOn: false,
    kmlOutput: false,
    csvOutput: false,
    jsonFileName: '',
    kmlFileName: '',
    csvFileName: '',
  },
})

const message = ref('')

// Fetch current settings when component mounts
onMounted(async () => {
  try {
    const res = await axios.get<SettingsDTO>(
        'http://localhost:8080/api/settings',
        { withCredentials: true }
    )
    settings.value = res.data
  } catch (err) {
    console.error('Failed to load settings:', err)
  }
})

// Save updated settings
async function saveSettings() {
  try {
    const res = await axios.post<string>(
        'http://localhost:8080/api/settings',
        settings.value,
        { withCredentials: true }
    )
    message.value = 'Settings saved: ' + res.data
  } catch (err) {
    console.error('Save failed:', err)
    message.value = 'Error saving settings'
  }
}
</script>