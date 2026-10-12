<template>
  <div class="department-cell">
    <v-icon size="16" color="primary" class="me-2">mdi-domain</v-icon>
    {{ item.departmentName }}
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DepartmentColumn',
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
.department-cell {
  display: flex;
  align-items: center;
  font-weight: 500;
  color: var(--v-theme-text-primary);
  font-size: 0.875rem;
}
</style>
