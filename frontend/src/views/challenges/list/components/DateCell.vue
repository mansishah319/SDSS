<template>
  <div class="date-cell">
    <div class="date-value">{{ options.formatDate(value) }}</div>
    <div class="date-time">{{ options.formatTime?.(value) }}</div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DateCell',
  props: {
    item: {
      type: Object,
      required: true
    },
    value: {
      type: [String, Date],
      required: true
    },
    column: {
      type: String,
      required: true
    },
    options: {
      type: Object,
      required: true
    }
  }
})
</script>
