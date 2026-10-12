<template>
  <span class="custom-label-font">
    <v-tooltip v-if="tooltip" location="top">
      <template #activator="{ props }">
        <span v-bind="props">
          {{ labelValue }}
          <span v-if="mandatory" style="color: red">*</span>
        </span>
      </template>
      <span>{{ tooltip }}</span>
    </v-tooltip>
    <template v-else>
      {{ labelValue }}
      <span v-if="mandatory" style="color: red">*</span>
    </template>
  </span>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CustomFormLabel',
  props: {
    labelValue: {
      type: String,
      required: true
    },
    mandatory: {
      type: Boolean,
      default: false
    },
    tooltip: {
      type: String,
      default: ''
    }
  }
})
</script>

<style scoped lang="scss">
.custom-label-font {
  font-family: DubaiBold, sans-serif;
  font-size: 0.8rem;
}
</style>
