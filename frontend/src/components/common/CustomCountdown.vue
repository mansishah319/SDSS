<template>
  <Countdown
    :deadline="deadlineEndTime"
    :labels="{
      days: $t('common.days'),
      hours: $t('common.hours'),
      minutes: $t('common.minutes'),
      seconds: $t('common.seconds'),
    }"
    :countdown-size="customCountdownSize"
    :label-size="customLabelSize"
    :label-color="customLabelColor"
    :main-color="customMainColor"
    :second-flip-color="customSecondFlipColor"
    :main-flip-background-color="customMainFlipBackgroundColor"
    :second-flip-background-color="customSecondFlipBackgroundColor"
  ></Countdown>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { Countdown } from 'vue3-flip-countdown'

export default defineComponent({
  name: 'CustomCountdown',
  components: { Countdown },
  props: {
    deadlineEndTime: {
      type: String,
      default: ''
    },
    customCountdownSize: {
      type: String,
      default: '2.5rem'
    },
    customLabelSize: {
      type: String,
      default: '.8rem'
    },
    customLabelColor: {
      type: String,
      default: 'green'
    },
    customMainColor: {
      type: String,
      default: '#bbff62'
    },
    customSecondFlipColor: {
      type: String,
      default: '#ffffff'
    },
    customMainFlipBackgroundColor: {
      type: String,
      default: '#a70000'
    },
    customSecondFlipBackgroundColor: {
      type: String,
      default: '#000'
    }
  }
})
</script>
