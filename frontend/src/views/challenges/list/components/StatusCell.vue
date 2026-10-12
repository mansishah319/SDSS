<template>
  <v-tooltip :text="statusDescription" location="top">
    <template #activator="{ props: tooltipProps }">
      <div v-bind="tooltipProps" class="status-cell">
        <v-chip :color="statusColor" variant="tonal" size="small" class="status-chip" :class="statusClass">
          <v-icon start :icon="statusIcon" size="16" :class="statusIconClass"></v-icon>
          <span class="status-text">{{ statusText }}</span>
          <v-icon v-if="isUrgent" end icon="mdi-exclamation" size="12" class="priority-indicator urgent-pulse"></v-icon>
          <v-icon v-else-if="isNew" end icon="mdi-star" size="12" class="priority-indicator new-glow"></v-icon>
        </v-chip>
      </div>
    </template>
  </v-tooltip>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'

export default defineComponent({
  name: 'StatusCell',
  props: {
    item: {
      type: Object,
      required: true
    },
    options: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    // Destructure all necessary values from props and options
    const {
      getStatusColor,
      getStatusIcon,
      getStatusDescription,
      getStatusClass,
      getStatusIconClass,
      isUrgentStatus,
      isNewStatus
    } = props.options

    const statusText = computed(() => props.item.status)
    const statusColor = computed(() => getStatusColor(props.item.status))
    const statusIcon = computed(() => getStatusIcon(props.item.status))
    const statusDescription = computed(() => getStatusDescription(props.item.status))
    const statusClass = computed(() => getStatusClass(props.item.status))
    const statusIconClass = computed(() => getStatusIconClass(props.item.status))
    const isUrgent = computed(() => isUrgentStatus(props.item.status))
    const isNew = computed(() => isNewStatus(props.item.status))

    return {
      statusText,
      statusColor,
      statusIcon,
      statusDescription,
      statusClass,
      statusIconClass,
      isUrgent,
      isNew
    }
  }
})
</script>
