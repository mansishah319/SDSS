<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template src="./EnhancedDataTable.html" lang="html"></template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ContentLoader from '@/components/loader/ContentLoader.vue'

export interface SearchField {
  key: string
  label: string
  component: string
  cols?: number
  sm?: number
  md?: number
  items?: any[]
  itemTitle?: string
  itemValue?: string
  variant?: string
  density?: string
  clearable?: boolean
  icon?: string
  loading?: boolean
  disabled?: boolean
}

export interface TableAction {
  key: string
  label: string
  icon: string
  color: string
  tooltip?: string
}

export interface ColumnTemplate {
  component: any
  options?: any
}

export default defineComponent({
  name: 'EnhancedDataTable',
  components: {
    ContentLoader
  },
  props: {
    // Basic props
    title: {
      type: String,
      required: true
    },
    items: {
      type: Array,
      required: true
    },
    headers: {
      type: Array,
      required: true
    },
    totalItems: {
      type: Number,
      default: 0
    },
    loading: {
      type: Boolean,
      default: false
    },
    skeletonLoading: {
      type: Boolean,
      default: false
    },
    loaderVariant: {
      type: String,
      default: 'table'
    },
    loaderMessage: {
      type: String,
      default: ''
    },
    loaderDescription: {
      type: String,
      default: ''
    },

    // Search configuration
    basicSearchFields: {
      type: Array as () => SearchField[],
      default: () => []
    },
    advancedSearchFields: {
      type: Array as () => SearchField[],
      default: () => []
    },
    searchValues: {
      type: Object,
      default: () => ({})
    },
    searchOnInput: {
      type: Boolean,
      default: false
    },

    // Table configuration
    itemsPerPage: {
      type: Number,
      default: 10
    },
    itemsPerPageOptions: {
      type: Array,
      default: () => [
        { value: 10, title: '10' },
        { value: 25, title: '25' },
        { value: 50, title: '50' },
        { value: 100, title: '100' },
        { value: -1, title: 'All' }
      ]
    },
    itemValue: {
      type: String,
      default: 'id'
    },
    itemProps: {
      type: [Object, Function],
      default: undefined
    },
    rowProps: {
      type: [Object, Function],
      default: undefined
    },

    // Actions configuration
    actions: {
      type: [Array, Function],
      default: () => []
    },

    // Column templates
    columnTemplates: {
      type: Object as () => Record<string, ColumnTemplate>,
      default: () => ({})
    },
    columnOptions: {
      type: Object,
      default: () => ({})
    },

    // UI customization
    statsIcon: {
      type: String,
      default: 'mdi-table'
    },
    tableIcon: {
      type: String,
      default: 'mdi-table'
    },
    emptyStateIcon: {
      type: String,
      default: 'mdi-table-large'
    },
    emptyStateMessage: {
      type: String,
      default: ''
    },
    showStatsChip: {
      type: Boolean,
      default: true
    },

    // Features
    showExport: {
      type: Boolean,
      default: true
    },
    exportLoading: {
      type: Boolean,
      default: false
    },

    // New prop
    actionsOrientation: {
      type: String,
      default: 'vertical', // 'vertical' or 'horizontal'
      validator: (v: string) => ['vertical', 'horizontal'].includes(v)
    }
  },

  emits: [
    'search',
    'clear-search',
    'refresh',
    'export',
    'action',
    'column-action',
    'table-update',
    'update:itemsPerPage'
  ],

  setup(props, { emit }) {
    const { t } = useI18n()
    const searchExpanded = ref(false)

    // Computed properties
    const hasAdvancedSearch = computed(() => props.advancedSearchFields.length > 0)

    const itemsPerPageComputed = computed({
      get: () => props.itemsPerPage,
      set: (value: number) => {
        handleItemsPerPageUpdate(value)
      }
    })

    const emptyStateTitle = computed(() => t('common.noDataTitle'))
    const emptyStateDescription = computed(() => props.emptyStateMessage?.trim() || t('common.noDataDescription'))
    const loadingMessage = computed(() => props.loaderMessage?.trim() || `${t('common.loading')} ${props.title}`)
    const loadingDescription = computed(() => props.loaderDescription?.trim() || t('common.loader.tableDescription'))

    // Methods
    const toggleSearchExpanded = () => {
      searchExpanded.value = !searchExpanded.value
    }

    const handleSearchChange = () => {
      if (props.searchOnInput) {
        emit('search', props.searchValues)
      }
    }

    const handleSearch = () => {
      emit('search', props.searchValues)
    }

    const handleClearSearch = () => {
      emit('clear-search')
    }

    const handleRefresh = () => {
      emit('refresh')
    }

    const handleExport = () => {
      emit('export')
    }

    const handleAction = (actionKey: string, item: any) => {
      emit('action', { actionKey, item })
    }

    const handleColumnAction = (data: any) => {
      emit('column-action', data)
    }

    const handleTableUpdate = (options: any) => {
      emit('table-update', options)
    }

    const handleItemsPerPageUpdate = (newValue: number) => {
      emit('update:itemsPerPage', newValue)
    }

    return {
      searchExpanded,
      hasAdvancedSearch,
      itemsPerPageComputed,
      emptyStateTitle,
      emptyStateDescription,
      loadingMessage,
      loadingDescription,
      toggleSearchExpanded,
      handleSearchChange,
      handleSearch,
      handleClearSearch,
      handleRefresh,
      handleExport,
      handleAction,
      handleColumnAction,
      handleTableUpdate,
      handleItemsPerPageUpdate,
      itemProps: props.itemProps,
      rowProps: props.rowProps || props.itemProps
    }
  }
})
</script>

<style src="./EnhancedDataTable.scss" lang="scss" scoped></style>
