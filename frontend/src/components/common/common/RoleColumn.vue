<template>
  <v-chip color="primary" variant="tonal" size="small" class="role-chip">
    <v-icon start size="16">mdi-shield-account</v-icon>
    {{ item.roles }}
  </v-chip>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'RoleColumn',
  props: {
    item: {
      type: Object,
      required: true
    },
    value: {
      type: [String, Array],
      default: null
    }
  }
})
</script>

<style scoped>
.role-chip {
  font-weight: 500;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>
