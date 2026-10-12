import { Buffer as BufferPolyfill } from 'buffer/'
import processPolyfill from 'process/browser'
import { createApp } from 'vue'

// Polyfills for browser compatibility (exceljs, etc.)
globalThis.Buffer = BufferPolyfill
globalThis.process = processPolyfill

// Components
import App from '@/App.vue'

// Plugins & styles
import Notifications from '@kyvg/vue3-notification'
import { registerPlugins } from '@/plugins'
import 'animate.css'
import 'primeflex/primeflex.css'
import VueApexCharts from 'vue3-apexcharts'
import Countdown from 'vue3-flip-countdown'
import '@/assets/css/app.scss'

// Custom components
import DPDateTimePicker from '@/components/date/DPDateTimePicker.vue'
import DPTimePicker from '@/components/date/DPTimePicker.vue'
import DPDatePicker from '@/components/date/DPDatePicker.vue'

import DateCell from '@/views/challenges/list/components/DateCell.vue'
import StatusCell from '@/views/challenges/list/components/StatusCell.vue'
import VisibilityCell from '@/views/challenges/list/components/VisibilityCell.vue'
import VotingTimeCell from '@/views/challenges/list/components/VotingTimeCell.vue'

import StatusColumn from '@/components/common/common/StatusColumn.vue'
import RoleColumn from '@/components/common/common/RoleColumn.vue'
import EmailColumn from '@/components/common/common/EmailColumn.vue'
import DepartmentColumn from '@/components/common/common/DepartmentColumn.vue'
import SectorColumn from '@/components/common/common/SectorColumn.vue'

import DubaiPoliceLoader from '@/components/common/Loader.vue'

// Types for global polyfills
declare global {
  var Buffer: typeof BufferPolyfill
  var process: typeof processPolyfill

  interface Window {
    Buffer: typeof BufferPolyfill
    process: typeof processPolyfill
  }
}

// --- Browser detection ---
const userAgent = navigator.userAgent

const isChromeBrowser = /Chrome|CriOS/.test(userAgent) && !/Edg|OPR|SamsungBrowser/.test(userAgent)

document.documentElement.classList.toggle('browser-chrome', isChromeBrowser)

document.documentElement.classList.toggle('browser-non-chrome', !isChromeBrowser)

document.body.classList.toggle('browser-chrome', isChromeBrowser)

document.body.classList.toggle('browser-non-chrome', !isChromeBrowser)

/**
 * ============================================================
 * SDSS HARDCODED LOGIN TOKEN
 * ============================================================
 *
 * This executes BEFORE Vue application starts.
 */

const SDSS_LOGIN_TOKEN =
  'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZ3JwTnVtYmVyIjoiNjk2Nzk3IiwiZW1wbG95ZWVOYW1lIjoiQWRtaW4iLCJmdWxsTmFtZSI6IkFkbWluIEFkbWluIiwidXNlck5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZHViYWlwb2xpY2UuZ292LmFlIiwicm9sZXMiOlsiUE9SVEFMQURNSU4iXSwicm9sZUxpc3QiOlsiUk9MRV9QT1JUQUxBRE1JTiJdLCJsYW5ndWFnZSI6ImVuIiwiZW1wVHlwZSI6MiwiZ2VuZGVyIjoibWFsZSIsImRlcGFydG1lbnRJZCI6MSwiZGVwYXJ0bWVudE5hbWUiOiJJVCBEZXBhcnRtZW50Iiwic2VjdG9ySWQiOjEsInNlY3Rvck5hbWUiOiJJVCBTZWN0b3IiLCJyYXRpbmdzIjp0cnVlLCJkZWNpc2lvbiI6dHJ1ZSwicXVlc3Rpb25WaWV3Ijp0cnVlLCJjaGF0Q29udGVudHMiOnRydWUsInNjcm9sbGluZ1RleHQiOnRydWUsInNvY2lhbE1lZGlhRmVlZHMiOnRydWUsImludGVybmFsRG9jdW1lbnRzIjp0cnVlLCJpc0RlbHBoaU1vZGVyYXRvciI6ZmFsc2UsImlzcyI6IkRFRkFVTFRfSVNTVUVSIiwiaWF0IjoxNzkwMzU5NTIxLCJleHAiOjE4ODQ5Njc1MjF9.9__CnJO6gZSX29cXt9reYTq2JSiovaLV600uPQvvHG6rCfiUel4GC3M-FkI7QvT3MjFkafRhCVdNP9apn9L2PQ'

localStorage.setItem('sdss_login_token', SDSS_LOGIN_TOKEN)

// Debug
console.log('========================================')

console.log('SDSS LOGIN TOKEN INITIALIZED')

console.log('Token exists:', !!localStorage.getItem('sdss_login_token'))

console.log('Token:', localStorage.getItem('sdss_login_token'))

console.log('========================================')

/**
 * ============================================================
 * APP SETUP
 * ============================================================
 */

const app = createApp(App)

registerPlugins(app)

app.use(Countdown)
app.use(Notifications)
app.use(VueApexCharts)

// Global components
app.component('DpTimePicker', DPTimePicker)

app.component('DpDatePicker', DPDatePicker)

app.component('DpDateTimePicker', DPDateTimePicker)

// Register globally
app.component('DateCell', DateCell)

app.component('StatusCell', StatusCell)

app.component('VisibilityCell', VisibilityCell)

app.component('VotingTimeCell', VotingTimeCell)

// Register custom table column components
app.component('StatusColumn', StatusColumn)

app.component('RoleColumn', RoleColumn)

app.component('EmailColumn', EmailColumn)

app.component('DepartmentColumn', DepartmentColumn)

app.component('SectorColumn', SectorColumn)

// Loading
app.component('DubaiPoliceLoader', DubaiPoliceLoader)

// Configure Vue
// app.config.globalProperties.$config = { silent: true }

// Mount application
app.mount('#app')
