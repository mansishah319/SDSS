<template>
  <v-progress-circular
    :class="customProgressClass"
    :model-value="percentage"
    :rotate="customRotate"
    :size="customSize"
    :width="customWidth"
    :color="color"
  >
    <template #default> {{ percentage }}%</template>
  </v-progress-circular>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CustomProgressCircular',
  props: {
    percentage: {
      type: Number,
      required: true
    },
    color: {
      type: String,
      default: '#008755'
    },
    customRotate: {
      type: Number,
      default: 360
    },
    customSize: {
      type: Number,
      default: 250
    },
    customWidth: {
      type: Number,
      default: 50
    },
    customProgressClass: {
      type: String,
      default: 'custom-progress-circular'
    }
  }
})
</script>

<style scoped lang="scss">
.custom-progress-circular {
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1), rgba(0, 0, 0, 0.1));
  border: 2px solid #ddd;
  border-radius: 50%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  padding: 4px;
}
</style>
