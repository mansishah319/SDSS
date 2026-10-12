<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { onMounted, ref, watch } from 'vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'

export default {
  components: { AppDialogShell, FooterComponent, EmptyDataTable },
  props: {
    dialogValue: {
      type: Boolean,
      default: false
    },
    appearActions: {
      type: Boolean,
      default: false
    },
    appearJustViewActions: {
      type: Boolean,
      default: false
    },
    items: {
      type: Array,
      default: () => []
    },
    itemsEmptyMessage: {
      type: String,
      default: 'common.empty'
    },
    mainTitle: {
      type: String,
      default: ''
    },
    subtitle: {
      type: String,
      default: ''
    },
    headers: { type: Array, default: () => [] }
  },
  emits: ['closeDialog', 'approve', 'reject', 'view'],
  setup(props, { emit }) {
    const dialog = ref(false)

    const emitCloseDialog = () => {
      dialog.value = false
      emit('closeDialog')
    }

    watch(
      () => props.dialogValue,
      (newValue) => {
        dialog.value = newValue
      }
    )

    onMounted(() => {
      dialog.value = props.dialogValue
    })

    const approveItem = (item: Record<string, any>) => {
      emit('approve', item) // Emit 'approve' event with the selected item
    }

    const rejectItem = (item: Record<string, any>) => {
      emit('reject', item) // Emit 'reject' event with the selected item
    }

    const viewItem = (item: Record<string, any>) => {
      emit('view', item) // Emit 'reject' event with the selected item
    }

    return {
      dialog,
      emitCloseDialog,
      approveItem,
      rejectItem,
      viewItem
    }
  }
}
</script>

<template src="./DialogMainDashboard.html"></template>
<style src="../AdminDashboard.scss" lang="scss" scoped></style>
