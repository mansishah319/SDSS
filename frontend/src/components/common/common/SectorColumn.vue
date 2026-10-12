<template>
  <div class="sector-cell">
    <v-icon size="16" color="secondary" class="me-2">mdi-office-building</v-icon>
    {{ item.sectorName }}
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SectorColumn',
  props: {
    item: {
      type: Object,
      required: true
    },
    value: {
      type: String,
      default: null
    }
  }
})
</script>

<style scoped>
.sector-cell {
  display: flex;
  align-items: center;
  font-weight: 500;
  color: var(--v-theme-text-primary);
  font-size: 0.875rem;
}
</style>
