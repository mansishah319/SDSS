<template lang="html">
  <v-container class="custom-time-counter-up-container text-center pa-4">
    <!-- Counter Area with Animations -->
    <v-row class="justify-center">
      <v-col
        v-for="(value, label) in timeElapsed"
        :key="label"
        cols="6"
        sm="4"
        md="3"
        lg="2"
        xl="2"
        xxl="1"
        class="d-flex justify-center"
      >
        <v-card class="pa-4 text-center elevation-2 hoverable custom-time-counter-up-card">
          <v-icon v-if="icons[label]" class="mb-2" size="36" color="primary">
            {{ icons[label] }}
          </v-icon>
          <div class="font-dubai-bold">{{ value }}</div>
          <div class="subtitle-1">{{ $t(`common.${label}`) }}</div>
        </v-card>
      </v-col>
    </v-row>
    <!-- Progress Bar for Journey -->
    <v-progress-linear
      v-if="showProgress"
      v-show="showProgress"
      :value="calculateJourneyProgress()"
      height="10"
      class="mt-6 custom-time-counter-up-card-progress-linear"
      color="secondary"
    />
  </v-container>
</template>

<script lang="ts">
import { computed, onMounted, ref } from 'vue'

export default {
  name: 'VuetifyDynamicTimeCounter',
  props: {
    createdDate: {
      type: String,
      required: true
    },
    showProgress: {
      type: Boolean,
      default: false
    },
    endDate: {
      type: [String, Date, null],
      default: null
    }
  },
  setup(props) {
    const foundingDate = new Date(props.createdDate)
    const timeElapsed = ref({})
    const icons = ref({
      years: 'mdi-calendar',
      months: 'mdi-calendar-month',
      days: 'mdi-calendar-today',
      hours: 'mdi-clock',
      minutes: 'mdi-clock-time-eight',
      seconds: 'mdi-timer'
    })

    const calculateTimeElapsed = () => {
      const now = new Date()
      let delta = Math.abs(now.getTime() - foundingDate.getTime()) / 1000

      const years = Math.floor(delta / (60 * 60 * 24 * 365))
      delta -= years * (60 * 60 * 24 * 365)

      const months = Math.floor(delta / (60 * 60 * 24 * 30))
      delta -= months * (60 * 60 * 24 * 30)

      const days = Math.floor(delta / (60 * 60 * 24))
      delta -= days * (60 * 60 * 24)

      const hours = Math.floor(delta / (60 * 60))
      delta -= hours * (60 * 60)

      const minutes = Math.floor(delta / 60)
      const seconds = Math.floor(delta % 60)

      timeElapsed.value = {
        years: years,
        months: months,
        days: days,
        hours: hours,
        minutes: minutes,
        seconds: seconds
      }
    }

    const calculateJourneyProgress = computed(() => {
      const now = new Date()
      let endDateObj: Date | null = null
      if (props.endDate) {
        endDateObj = typeof props.endDate === 'string' ? new Date(props.endDate) : props.endDate
      }
      if (!endDateObj) return 0
      const totalTime = Math.abs(now.getTime() - foundingDate.getTime())
      const totalJourneyTime = Math.abs(endDateObj.getTime() - foundingDate.getTime())
      return (totalTime / totalJourneyTime) * 100 || 0
    })

    onMounted(() => {
      calculateTimeElapsed()
      setInterval(calculateTimeElapsed, 1000)
    })

    return { timeElapsed, calculateJourneyProgress, icons }
  }
}
</script>

<style scoped lang="scss">
.custom-time-counter-up-container {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.custom-time-counter-up-card {
  transition: transform 0.3s ease-in-out,
  box-shadow 0.3s ease-in-out;
  width: 70px;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-shadow: none !important;
}

.custom-time-counter-up-card:hover {
}

.subtitle-1 {
  font-size: 0.6rem;
  color: #666;
}

.custom-time-counter-up-card-progress-linear {
  max-width: 80%;
  margin: 0 auto;
}
</style>
