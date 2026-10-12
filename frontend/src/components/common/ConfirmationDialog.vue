<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <AppDialogShell
    v-model="dialog"
    class="confirmation-dialog"
    :title="title"
    :subtitle="message"
    :icon="icon || 'mdi-help-circle-outline'"
    :icon-color="iconColor"
    :tone="dialogTone"
    :max-width="maxWidth"
    :persistent="persistent"
    :show-close="showClose"
    :content-class="contentClass"
    @close="handleOutsideClick"
  >
    <slot />

    <template #actions>
      <v-btn variant="text" class="cancel-btn" :prepend-icon="cancelIcon" @click="cancel">
        {{ cancelText || $t('common.cancel') }}
      </v-btn>
      <v-btn
        :color="confirmButtonColor || 'primary'"
        class="confirm-btn"
        :loading="loading"
        :prepend-icon="confirmIcon"
        @click="confirm"
      >
        {{ confirmText || $t('common.confirm') }}
      </v-btn>
    </template>
  </AppDialogShell>
</template>

<script>
import { defineComponent } from 'vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'

export default defineComponent({
  components: {
    AppDialogShell
  },
  props: {
    title: {
      type: String,
      required: true
    },
    message: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      default: null
    },
    iconColor: {
      type: String,
      default: 'primary'
    },
    confirmButtonColor: {
      type: String,
      default: null
    },
    confirmText: {
      type: String,
      default: null
    },
    loading: {
      type: Boolean,
      default: false
    },
    maxWidth: {
      type: [String, Number],
      default: 500
    },
    persistent: {
      type: Boolean,
      default: false
    },
    showClose: {
      type: Boolean,
      default: true
    },
    contentClass: {
      type: String,
      default: ''
    },
    confirmIcon: {
      type: String,
      default: 'mdi-check'
    },
    cancelIcon: {
      type: String,
      default: 'mdi-close'
    },
    cancelText: {
      type: String,
      default: null
    }
  },
  emits: ['confirmed', 'cancel'],
  data() {
    return {
      dialog: true
    }
  },
  computed: {
    dialogTone() {
      const toneSource = (this.confirmButtonColor || this.iconColor || '').toLowerCase()

      if (toneSource.includes('red') || toneSource.includes('error') || toneSource.includes('danger')) {
        return 'error'
      }

      if (toneSource.includes('warn') || toneSource.includes('orange') || toneSource.includes('amber')) {
        return 'warning'
      }

      if (toneSource.includes('success') || toneSource.includes('green')) {
        return 'success'
      }

      return 'primary'
    }
  },
  methods: {
    cancel() {
      this.dialog = false
      this.$emit('cancel', false)
    },
    confirm() {
      this.dialog = false
      this.$emit('confirmed', true)
    },
    handleOutsideClick() {
      this.$emit('cancel')
    }
  }
})
</script>

<style lang="scss" scoped>
.confirmation-dialog {
  .cancel-btn {
    opacity: 0.88;
  }

  .confirm-btn,
  .cancel-btn {
    min-width: 120px;
  }
}
</style>
