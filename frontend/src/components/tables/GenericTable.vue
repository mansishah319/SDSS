<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, PropType } from 'vue'

export default defineComponent({
  name: 'GenericDataTable',
  props: {
    headers: {
      type: Array as PropType<any[]>,
      default: () => [] // Provide a default value
    },
    items: {
      type: Array as PropType<any[]>,
      default: () => [] // Provide a default value
    },
    search: {
      type: String,
      default: '' // Provide a default value
    },
    loading: {
      type: Boolean,
      default: false
    },
    showUpdateButton: {
      type: Boolean,
      default: true
    },
    showDeleteButton: {
      type: Boolean,
      default: true
    },
    loadingText: {
      type: String,
      default: '' // Provide a default value
    },
    itemsPerPageText: {
      type: String,
      default: '' // Provide a default value
    },
    itemsPerPageOptions: {
      type: Array as PropType<any[]>,
      default: () => [] // Provide a default value
    },
    onEdit: {
      type: Function as PropType<(_id: number) => void>,
      default: null // Set to null to handle in methods
    },
    onDelete: {
      type: Function as PropType<(_id: number) => void>,
      default: null // Set to null to handle in methods
    }
  },
  methods: {
    handleEdit(id: number) {
      if (this.onEdit) {
        this.onEdit(id)
      } else {
        console.warn('onEdit function not provided')
      }
    },
    handleDelete(id: number) {
      if (this.onDelete) {
        this.onDelete(id)
      } else {
        console.warn('onDelete function not provided')
      }
    }
  }
})
</script>

<template src="./GenericTable.html"></template>
