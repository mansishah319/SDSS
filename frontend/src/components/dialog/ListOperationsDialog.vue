<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, ref, watch } from 'vue'

export default defineComponent({
  name: 'ListOperationsDialog',
  props: {
    showDialog: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '' // Provide a default value
    }
  },
  emits: ['update:showDialog'],
  setup(props, { emit }) {
    // Use local state to manage dialog visibility
    const internalShowDialog = ref(props.showDialog)

    // Watch for changes in the prop to update local state
    watch(
      () => props.showDialog,
      (newVal) => {
        internalShowDialog.value = newVal
      }
    )

    const closeDialog = () => {
      internalShowDialog.value = false
      emit('update:showDialog', false)
    }

    return { internalShowDialog, closeDialog }
  }
})
</script>

<template src="./ListOperationsDialog.html"></template>
<style src="./ListOperationsDialog.scss" lang="scss" scoped></style>
