<!-- VisibilityCell.vue -->
<template>
  <v-chip
    :color="item.visibility === 'PUBLIC' ? 'success' : 'warning'"
    variant="tonal"
    size="small"
    class="visibility-chip"
  >
    <v-icon start :icon="item.visibility === 'PUBLIC' ? 'mdi-earth' : 'mdi-lock'" size="16"></v-icon>
    {{ item.visibility }}
  </v-chip>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'VisibilityCell',
  props: {
    item: {
      type: Object,
      required: true
    },
    options: {
      type: Object,
      default: () => ({})
    }
  }
})
</script>
