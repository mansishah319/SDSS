<template>
  <v-select
    v-model="statusValue"
    :items="statusOptions"
    item-title="name"
    item-value="id"
    variant="outlined"
    density="compact"
    class="status-select"
    hide-details
    :color="statusColor"
    :bg-color="statusBgColor"
    :prepend-inner-icon="statusIcon"
    :loading="item.statusUpdating"
    @update:model-value="handleStatusChange"
  >
    <template #selection="{ item: selectedItem }">
      <div class="status-selection">
        <v-icon :color="statusColor" size="16" class="me-2">{{ statusIcon }}</v-icon>
        <span class="status-text">{{ selectedItem.name }}</span>
      </div>
    </template>
    <template #item="{ item: optionItem, props }">
      <v-list-item v-bind="props" class="status-option">
        <template #prepend>
          <v-icon :color="getStatusColor(optionItem.id)" size="16">{{ getStatusIcon(optionItem.id) }}</v-icon>
        </template>
        <v-list-item-title>{{ optionItem.name }}</v-list-item-title>
      </v-list-item>
    </template>
  </v-select>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'StatusColumn',
  props: {
    item: {
      type: Object,
      required: true
    },
    value: {
      type: [Number, String],
      default: null
    },
    options: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:value', 'action'],
  setup(props, { emit }) {
    const { t } = useI18n()

    const statusOptions = [
      { name: t('common.status.inactive'), id: 0 },
      { name: t('common.status.active'), id: 1 },
      { name: t('common.status.deleted'), id: 2 }
    ]

    // Function to map status string to numeric ID
    const getStatusId = (status: any): number => {
      if (typeof status === 'number') {
        return status
      }

      if (typeof status === 'string') {
        const statusLower = status.toLowerCase()
        switch (statusLower) {
          case 'inactive':
          case 'غير نشط':
            return 0
          case 'active':
          case 'نشط':
            return 1
          case 'deleted':
          case 'محذوف':
            return 2
          default:
            return 0 // Default to inactive
        }
      }

      return 0 // Default to inactive
    }

    const statusValue = computed({
      get: () => getStatusId(props.value),
      set: (val) => emit('update:value', val)
    })

    const statusColor = computed(() => getStatusColor(props.value))
    const statusBgColor = computed(() => getStatusBgColor(props.value))
    const statusIcon = computed(() => getStatusIcon(props.value))

    const handleStatusChange = (newValue: number) => {
      // Use the new value directly instead of the computed property
      console.log('StatusColumn - handleStatusChange called with newValue:', newValue)
      console.log('StatusColumn - props.value:', props.value)
      console.log('StatusColumn - statusValue.value:', statusValue.value)

      const numericStatus = newValue
      const updatedItem = { ...props.item, status: numericStatus }

      console.log('StatusColumn - emitting action with:', {
        type: 'status-change',
        item: updatedItem,
        status: numericStatus
      })

      emit('action', {
        type: 'status-change',
        item: updatedItem,
        status: numericStatus
      })
    }

    const getStatusColor = (status: any) => {
      if (typeof status === 'string') {
        status = status.toLowerCase()
      }

      switch (status) {
        case 'inactive':
        case 'غير نشط':
        case 0:
          return 'orange'
        case 'active':
        case 'نشط':
        case 1:
          return 'green'
        case 'deleted':
        case 'محذوف':
        case 2:
          return 'red'
        default:
          return ''
      }
    }

    const getStatusBgColor = (status: any) => {
      if (typeof status === 'string') {
        status = status.toLowerCase()
      }

      switch (status) {
        case 'inactive':
        case 'غير نشط':
        case 0:
          return 'orange-lighten-5'
        case 'active':
        case 'نشط':
        case 1:
          return 'green-lighten-5'
        case 'deleted':
        case 'محذوف':
        case 2:
          return 'red-lighten-5'
        default:
          return ''
      }
    }

    const getStatusIcon = (status: any) => {
      if (typeof status === 'string') {
        status = status.toLowerCase()
      }

      switch (status) {
        case 'inactive':
        case 'غير نشط':
        case 0:
          return 'mdi-account-off'
        case 'active':
        case 'نشط':
        case 1:
          return 'mdi-account-check'
        case 'deleted':
        case 'محذوف':
        case 2:
          return 'mdi-account-remove'
        default:
          return 'mdi-help-circle'
      }
    }

    return {
      statusOptions,
      statusValue,
      statusColor,
      statusBgColor,
      statusIcon,
      handleStatusChange,
      getStatusColor,
      getStatusBgColor,
      getStatusIcon
    }
  }
})
</script>

<style lang="scss" scoped>
.status-select {
  .v-field {
    border-radius: 8px;
    font-weight: 500;
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .v-field__input {
    min-height: 32px;
    padding: 4px 8px;
  }

  &[data-status='0'] .v-field__outline {
    border-color: rgb(var(--v-theme-orange)) !important;
  }

  &[data-status='1'] .v-field__outline {
    border-color: rgb(var(--v-theme-green)) !important;
  }

  &[data-status='2'] .v-field__outline {
    border-color: rgb(var(--v-theme-red)) !important;
  }
}

.status-selection {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-text {
  font-weight: 500;
  font-size: 0.875rem;
}

.status-option {
  padding: 8px 12px;
  border-radius: 4px;
  margin: 2px 0;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: rgba(var(--v-theme-primary), 0.1);
  }

  .v-list-item-title {
    font-weight: 500;
    font-size: 0.875rem;
  }
}

// Loading state
.status-updating {
  position: relative;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1;
  }
}
</style>
