<!-- VotingTimeCell.vue -->
<template>
  <div class="voting-time-cell">
    <div class="voting-period">
      <v-icon size="16" color="primary" class="me-1">mdi-calendar-start</v-icon>
      {{ formatDate(item.startTime) }}
    </div>
    <div class="voting-period">
      <v-icon size="16" color="error" class="me-1">mdi-calendar-end</v-icon>
      {{ formatDate(item.endTime) }}
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'VotingTimeCell',
  props: {
    item: {
      type: Object,
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
  },
  setup(props) {
    return {
      ...props.options
    }
  }
})
</script>
