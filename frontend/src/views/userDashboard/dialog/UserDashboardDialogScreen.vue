<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, PropType, ref, watch } from 'vue'

export default defineComponent({
  name: 'MyUserDialog',
  props: {
    dialogValue: {
      type: Boolean as PropType<boolean>,
      default: false
    },
    items: {
      type: Array as PropType<Array<Record<string, any>>>,
      default: () => []
    },
    headers: {
      type: Array as PropType<Array<string>>,
      default: () => []
    },
    title: {
      type: String as PropType<string>,
      default: 'Dialog Title'
    },
    isActionsNeeded: {
      type: Boolean as PropType<boolean>,
      default: false
    }
  },
  emits: ['closeDialog', 'approve', 'reject'], // Emit custom events for approve and reject actions
  setup(props, { emit }) {
    const dialog = ref(false)
    const isActionsNeededValue = ref(false)

    const emitCloseDialog = () => {
      dialog.value = false
      emit('closeDialog')
    }

    // Watch for changes in props.dialogValue
    watch(
      () => props.dialogValue,
      (newValue) => {
        dialog.value = newValue
      }
    )

    // Set the initial value when the component is mounted
    onMounted(() => {
      dialog.value = props.dialogValue
      isActionsNeededValue.value = props.isActionsNeeded
    })

    const approveItem = (item: Record<string, any>) => {
      emit('approve', item) // Emit 'approve' event with the selected item
    }

    const rejectItem = (item: Record<string, any>) => {
      emit('reject', item) // Emit 'reject' event with the selected item
    }

    return {
      dialog,
      emitCloseDialog,
      approveItem,
      rejectItem
    }
  }
})
</script>

<template src="./UserDashboardDialogScreen.html"></template>
<style src="../UserDashboard.scss" lang="scss" scoped></style>
