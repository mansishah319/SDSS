<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <v-card
    class="stat-card-overview"
    :style="{ backgroundColor: backgroundColor }"
    elevation="2"
    @click="$emit('click')"
  >
    <v-card-text class="d-flex flex-column align-center justify-space-between">
      <v-icon :color="iconColor" size="48">{{ icon }}</v-icon>
      <div class="text-h5 py-2" :style="{ color: fontColor }">
        {{ value }}
      </div>
      <div class="text-caption grey--text font-bukra-bold" :style="{ color: fontColor }">
        {{ title }}
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
defineEmits(['click'])
defineProps({
  title: {
    type: String,
    default: ''
  },
  value: {
    type: [String, Number],
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  iconColor: {
    type: String,
    default: 'primary'
  },
  backgroundColor: {
    type: String,
    default: 'white'
  },
  fontColor: {
    type: String,
    default: 'black'
  }
})
</script>

<style scoped lang="scss">
.stat-card-overview {
  padding: 1rem;
  border-radius: 0.5rem;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
