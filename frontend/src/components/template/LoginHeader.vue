<template>
  <header class="header-block">
    <div class="header-start-block">
      <img class="dp-logo-header" alt="DP logo" src="@/assets/images/headers/dp-logo-header.svg" />
    </div>
    <div class="header-end-block">
      <div class="app-lang custom-login-page-language">
        <LocaleSwitcher></LocaleSwitcher>
      </div>
      <div class="date-block">
        <div class="date-block-date-time">
          <div class="date-block-date">{{ currentDate }}</div>
          <div class="date-block-time">{{ currentTime }}</div>
        </div>

        <div class="date-block-icon">
          <img src="@/assets/images/calendar/calendar-icon.svg" alt="Calendar" />
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { onMounted, onUnmounted, ref } from 'vue'
import LocaleSwitcher from '@/components/locals/LocaleSwitcher.vue'

export default {
  components: {
    LocaleSwitcher
  },
  setup() {
    const currentDate = ref('')
    const currentTime = ref('')

    const updateDateTime = () => {
      const now = new Date()
      // Format date and time for Dubai timezone
      const dateFormatter = new Intl.DateTimeFormat('en-GB', {
        timeZone: 'Asia/Dubai',
        dateStyle: 'short'
      })
      const timeFormatter = new Intl.DateTimeFormat('en-GB', {
        timeZone: 'Asia/Dubai',
        timeStyle: 'short'
      })

      currentDate.value = dateFormatter.format(now) // e.g., 7/2/2024
      currentTime.value = timeFormatter.format(now) // e.g., 9:05 AM
    }

    // Update date and time every second
    let intervalId
    onMounted(() => {
      updateDateTime() // Initial update
      intervalId = setInterval(updateDateTime, 1000)
    })

    // Cleanup interval on component unmount
    onUnmounted(() => {
      clearInterval(intervalId)
    })

    return {
      currentDate,
      currentTime
    }
  }
}
</script>

<style scoped>
.header-block {
  display: flex;
  justify-content: space-between;
  padding: 10px;
}

.dp-logo-header {
  height: 40px;
}

.date-block {
  display: flex;
  align-items: center;
}

.date-block-date-time {
  display: flex;
  flex-direction: column;
  margin-right: 10px;
}

.date-block-date,
.date-block-time {
  font-size: 14px;
  line-height: 1.5;
}

.date-block-icon img {
  height: 20px;
}
</style>
