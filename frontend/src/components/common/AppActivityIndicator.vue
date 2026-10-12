<template>
  <div class="app-activity-indicator" :aria-label="ariaLabel" role="status">
    <v-progress-circular
      class="app-activity-indicator__spinner"
      :color="color"
      :size="size"
      :width="width"
      indeterminate
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'

export default defineComponent({
  name: 'AppActivityIndicator',
  props: {
    color: {
      type: String,
      default: 'primary'
    },
    size: {
      type: [Number, String] as PropType<number | string>,
      default: 28
    },
    width: {
      type: [Number, String] as PropType<number | string>,
      default: 3
    },
    ariaLabel: {
      type: String,
      default: 'Loading'
    }
  }
})
</script>

<style scoped lang="scss">
.app-activity-indicator {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.75rem;
  min-height: 1.75rem;
}

.app-activity-indicator__spinner {
  filter: drop-shadow(0 0 8px rgba(21, 112, 239, 0.18));
}
</style>
