<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<!-- DeleteConfirmation.vue -->
<template>
  <v-card>
    <v-card-title class="headline">
      {{ $t('common.deletionTitle') }}
    </v-card-title>
    <v-card-text>{{ $t('common.deleteConfirmation') }}</v-card-text>
    <v-card-actions>
      <v-btn color="red darken-1" text @click="cancelWithoutDelete">
        {{ $t('common.cancel') }}
      </v-btn>
      <v-btn color="green darken-1" text @click="confirmDeletion">
        {{ $t('common.confirm') }}
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'

export default defineComponent({
  props: {
    dialog: Boolean as PropType<boolean>
  },
  emits: ['cancel', 'delete'], // Declare the 'delete' event
  setup(props, { emit }) {
    const cancelWithoutDelete = () => {
      emit('cancel')
    }

    const confirmDeletion = () => {
      // Emit the 'delete' event when the "Confirm" button is clicked
      emit('delete')
    }

    return { cancelWithoutDelete, confirmDeletion }
  }
})
</script>
