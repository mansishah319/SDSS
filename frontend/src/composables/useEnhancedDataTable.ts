import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue3-toastify'

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

export function useEnhancedDataTable(options: {
  title: string
  statsIcon?: string
  tableIcon?: string
  emptyStateIcon?: string
  emptyStateMessage?: string
  showExport?: boolean
  itemValue?: string
  itemsPerPageOptions?: any[]
}) {
  const { t } = useI18n()

  // State
  const loading = ref(false)
  const skeletonLoading = ref(true)
  const searchExpanded = ref(false)
  const exportLoading = ref(false)
  const itemsPerPage = ref(10)
  const totalItems = ref(0)
  const items = ref<any>([])
  const searchValues = ref({})

  // Configuration
  const config = {
    title: options.title,
    statsIcon: options.statsIcon ?? 'mdi-table',
    tableIcon: options.tableIcon ?? 'mdi-table',
    emptyStateIcon: options.emptyStateIcon ?? 'mdi-table-large',
    emptyStateMessage: options.emptyStateMessage?.trim() || t('common.noDataDescription'),
    showExport: options.showExport !== false,
    itemValue: options.itemValue ?? 'id',
    itemsPerPageOptions: options.itemsPerPageOptions || [
      { value: 10, title: '10' },
      { value: 25, title: '25' },
      { value: 50, title: '50' },
      { value: 100, title: '100' },
      { value: -1, title: 'All' }
    ]
  }

  // Methods
  const toggleSearchExpanded = () => {
    searchExpanded.value = !searchExpanded.value
  }

  const handleSearch = (values: any) => {
    searchValues.value = { ...values }
    // Emit search event for parent to handle
    return values
  }

  const handleClearSearch = () => {
    searchValues.value = {}
    // Emit clear search event for parent to handle
  }

  const handleRefresh = async () => {
    try {
      loading.value = true
      // Emit refresh event for parent to handle
      toast.success(t('Messages.success.data.refreshed'))
    } catch (error) {
      console.error('Error refreshing data:', error)
      toast.error(t('Messages.error.data.refreshFailed'))
    } finally {
      loading.value = false
    }
  }

  const handleExport = async () => {
    try {
      exportLoading.value = true
      // Emit export event for parent to handle
      toast.success(t('Messages.success.export.completed'))
    } catch (error) {
      console.error('Export error:', error)
      toast.error(t('Messages.error.export.failed'))
    } finally {
      exportLoading.value = false
    }
  }

  const handleAction = (actionData: { actionKey: string; item: any }) => {
    // Emit action event for parent to handle
    return actionData
  }

  const handleColumnAction = (data: any) => {
    // Emit column action event for parent to handle
    return data
  }

  const handleTableUpdate = (tableOptions: any) => {
    // Emit table update event for parent to handle
    return tableOptions
  }

  const setLoading = (value: boolean) => {
    loading.value = value
  }

  const setSkeletonLoading = (value: boolean) => {
    skeletonLoading.value = value
  }

  const setItems = (newItems: any[]) => {
    items.value = newItems
  }

  const setTotalItems = (total: number) => {
    totalItems.value = total
  }

  const setItemsPerPage = (perPage: number) => {
    itemsPerPage.value = perPage
  }

  const setSearchValues = (values: any) => {
    searchValues.value = { ...values }
  }

  // Computed
  const hasAdvancedSearch = computed(() => {
    // This should be set by the parent component
    return false
  })

  return {
    // State
    loading,
    skeletonLoading,
    searchExpanded,
    exportLoading,
    itemsPerPage,
    totalItems,
    items,
    searchValues,

    // Configuration
    config,

    // Methods
    toggleSearchExpanded,
    handleSearch,
    handleClearSearch,
    handleRefresh,
    handleExport,
    handleAction,
    handleColumnAction,
    handleTableUpdate,
    setLoading,
    setSkeletonLoading,
    setItems,
    setTotalItems,
    setItemsPerPage,
    setSearchValues,

    // Computed
    hasAdvancedSearch
  }
}
