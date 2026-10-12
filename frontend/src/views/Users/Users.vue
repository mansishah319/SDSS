<template src="./Users.html" lang="html"></template>

<script lang="ts">
import { computed, defineComponent, nextTick, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'

// Services
import LookupClientService from '@/services/lookup/LookupClientService'
import UserClientService from '@/services/user/UserClientService'

// Components
import UsersView from '@/views/Users/view/UsersView.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import EnhancedDataTable from '@/components/common/table/EnhancedDataTable.vue'
import StatusColumn from '@/components/common/common/StatusColumn.vue'
import RoleColumn from '@/components/common/common/RoleColumn.vue'
import EmailColumn from '@/components/common/common/EmailColumn.vue'
import DepartmentColumn from '@/components/common/common/DepartmentColumn.vue'
import SectorColumn from '@/components/common/common/SectorColumn.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'

// Utils & Constants
import { update } from '@/utils/GenericApiHelper'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { UserStatus } from '@/dto/enums/UserStatus'
import { isAdminOrModerator } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { useEnhancedDataTable } from '@/composables/useEnhancedDataTable'
import { type ExcelLegendItem, type ExcelMetricBarItem, exportRowsToExcel } from '@/utils/excel/exportRowsToExcel'
import { resolveLocalizedFieldValue } from '@/utils/localizationUtils'

// Models
import UserPageable from '@/model/request/UserPageable'
import type { UserSearchData } from '@/dto/users/UserSearchResponse'

// Helper functions
import userSearch from '@/views/Users/search/js/UserSearch'

interface DropDownListRequest {
  id: number
  name: string
}

export default defineComponent({
  name: 'UsersList',
  components: {
    UsersView,
    ConfirmationDialog,
    EnhancedDataTable,
    GenericDashboardHeaderView
  },
  setup() {
    const { t, locale } = useI18n()
    const router = useRouter()
    const sidebarStore = useSidebarStore()

    // State
    const deleteDialog = ref(false)
    const userToDelete = ref(0)
    const displayUserDetail = ref(false)
    const userToDisplay = ref(null)
    const departmentResponseList = ref([])
    const rolesResponseList = ref([])

    const selectUserGrpNumber = ref<number>(0)

    const statusOptions = ref<DropDownListRequest[]>([
      { name: t('common.status.inactive'), id: 0 },
      { name: t('common.status.active'), id: 1 },
      { name: t('common.status.deleted'), id: 2 }
    ])

    // Enhanced Data Table Setup
    const {
      loading,
      skeletonLoading,
      searchExpanded,
      exportLoading,
      itemsPerPage,
      totalItems,
      items: records,
      searchValues,
      setLoading,
      setSkeletonLoading
    } = useEnhancedDataTable({
      title: t('user.search.main-title'),
      statsIcon: 'mdi-account-group',
      tableIcon: 'mdi-table',
      emptyStateIcon: 'mdi-account-group-outline',
      emptyStateMessage: t('user.noUsersFound'),
      showExport: true,
      itemValue: 'grpNumber'
    })

    const dataTableRequest = ref(new UserPageable(0, 10))

    // Table Configuration
    const headers = computed(() => [
      { title: t('user.search.list.grpNumber'), align: 'start', sortable: true, key: 'grpNumber', width: '9%' },
      { title: t('user.search.list.full_name'), align: 'start', sortable: true, key: 'fullName', width: '15%' },
      { title: t('user.search.list.role'), align: 'start', sortable: true, key: 'roles', width: '15%' },
      { title: t('user.search.list.email'), align: 'start', sortable: true, key: 'email', width: '15%' },
      { title: t('user.search.list.status'), align: 'start', sortable: true, key: 'status', width: '15%' },
      {
        title: t('user.search.list.departmentName'),
        align: 'start',
        sortable: false,
        key: 'departmentName',
        width: '50%'
      },
      { title: t('user.search.list.sector'), align: 'start', sortable: true, key: 'sectorName', width: '50%' },
      { title: t('common.actions'), align: 'center', sortable: false, key: 'actions', width: '80px' }
    ])

    // Search Fields Configuration
    const basicSearchFields = computed(() => [
      {
        key: 'grpNumber',
        label: t('user.search.list.grpNumber'),
        component: 'v-text-field',
        cols: 12,
        sm: 6,
        md: 3,
        icon: 'mdi-identifier',
        clearable: true
      },
      {
        key: 'role',
        label: t('user.search.list.role'),
        component: 'v-select',
        cols: 12,
        sm: 6,
        md: 3,
        items: customDropDownRolesList.value,
        itemTitle: 'name',
        icon: 'mdi-shield-account',
        clearable: true
      },
      {
        key: 'fullName',
        label: t('user.search.list.full_name'),
        component: 'v-text-field',
        cols: 12,
        sm: 6,
        md: 3,
        icon: 'mdi-account',
        clearable: true
      },
      {
        key: 'status',
        label: t('user.search.list.status'),
        component: 'v-select',
        cols: 12,
        sm: 6,
        md: 3,
        items: statusOptions.value,
        itemValue: 'id',
        itemTitle: 'name',
        icon: 'mdi-flag',
        clearable: true
      }
    ])

    const advancedSearchFields = computed(() => [
      {
        key: 'expertLevel',
        label: t('user.search.list.expert_level'),
        component: 'v-text-field',
        cols: 12,
        sm: 6,
        md: 3,
        icon: 'mdi-star',
        clearable: true
      },
      {
        key: 'department',
        label: t('user.search.list.departmentName'),
        component: 'v-select',
        cols: 12,
        sm: 6,
        md: 3,
        items: customDropDownDepartmentList.value,
        itemTitle: 'name',
        icon: 'mdi-domain',
        clearable: true
      }
    ])

    // Actions Configuration
    const actions = [
      {
        key: 'view',
        label: t('common.view'),
        icon: 'mdi-account-eye',
        color: 'primary',
        tooltip: t('common.view')
      },
      {
        key: 'edit',
        label: t('common.edit'),
        icon: 'mdi-pencil',
        color: 'warning',
        tooltip: t('common.edit')
      },
      {
        key: 'delete',
        label: t('common.delete'),
        icon: 'mdi-delete',
        color: 'error',
        tooltip: t('common.delete')
      }
    ]

    // Column Templates
    const columnTemplates = {
      status: { component: StatusColumn },
      roles: { component: RoleColumn },
      email: { component: EmailColumn },
      departmentName: { component: DepartmentColumn },
      sectorName: { component: SectorColumn }
    }

    // Computed properties for dropdown lists
    const customDropDownDepartmentList = computed(() => {
      const regularArray = departmentResponseList.value.map((proxyObject: any) => ({ ...proxyObject }))
      return regularArray.map((department: any) => ({
        name: department.shortName
      }))
    })

    const customDropDownRolesList = computed(() => {
      const regularArray = rolesResponseList.value.map((proxyObject: any) => ({ ...proxyObject }))
      return regularArray.map((role: any) => ({
        name: role.role
      }))
    })

    const safeRecords = computed(() => records.value || [])

    const normalizeUserRecord = (user: UserSearchData) => {
      const normalizedUser = { ...user } as UserSearchData & { rolesText?: string }
      const rolesList = Array.isArray(user.roles) ? user.roles : []

      normalizedUser.rolesText = rolesList
        .map((role) =>
          resolveLocalizedFieldValue({
            locale: locale.value,
            item: role as unknown as Record<string, any>,
            fallback: (role as any)?.role || (role as any)?.nameEn || (role as any)?.nameAr || ''
          })
        )
        .filter(Boolean)
        .join(', ')

      return normalizedUser
    }

    const getLocalizedUserStatusLabel = (status?: string | null) => {
      const normalized = String(status || '').trim().toUpperCase()

      switch (normalized) {
        case UserStatus.ACTIVE:
          return t('common.status.active')
        case UserStatus.INACTIVE:
          return t('common.status.inactive')
        case UserStatus.DELETED:
          return t('common.status.deleted')
        default:
          return status || '-'
      }
    }

    const getUserStatusAppearance = (status?: string | null) => {
      const normalized = String(status || '').trim().toUpperCase()

      switch (normalized) {
        case UserStatus.ACTIVE:
          return {
            fillColor: '#ECFDF3',
            borderColor: '#B7EBC8',
            textColor: '#166534',
            metricColor: '#1E7A46'
          }
        case UserStatus.INACTIVE:
          return {
            fillColor: '#F2F4F7',
            borderColor: '#D0D5DD',
            textColor: '#475467',
            metricColor: '#667085'
          }
        case UserStatus.DELETED:
          return {
            fillColor: '#FEF3F2',
            borderColor: '#F3C0BE',
            textColor: '#912018',
            metricColor: '#B42318'
          }
        default:
          return {
            fillColor: '#F2F4F7',
            borderColor: '#D0D5DD',
            textColor: '#344054',
            metricColor: '#667085'
          }
      }
    }

    const buildUserExportColumns = () => [
      {
        key: 'rowNumber',
        header: t('common.srNo2'),
        width: 10,
        value: (_user: UserSearchData, index: number) => index + 1
      },
      {
        key: 'grpNumber',
        header: t('user.search.list.grpNumber'),
        width: 14,
        value: (user: UserSearchData) => user.grpNumber
      },
      {
        key: 'fullName',
        header: t('user.search.list.full_name'),
        width: 24,
        value: (user: UserSearchData) => user.fullName
      },
      { key: 'email', header: t('user.search.list.email'), width: 28, value: (user: UserSearchData) => user.email },
      {
        key: 'status',
        header: t('user.search.list.status'),
        width: 16,
        value: (user: UserSearchData) => getLocalizedUserStatusLabel(user.status)
      },
      {
        key: 'rolesText',
        header: t('user.search.list.role'),
        width: 28,
        value: (user: UserSearchData) => normalizeUserRecord(user).rolesText || '-'
      },
      {
        key: 'departmentName',
        header: t('user.search.list.departmentName'),
        width: 24,
        value: (user: UserSearchData) => user.departmentName || user.shortDepartmentName
      },
      {
        key: 'sectorName',
        header: t('user.search.list.sector'),
        width: 22,
        value: (user: UserSearchData) => user.sectorName
      },
      {
        key: 'expertLevel',
        header: t('user.search.list.expert_level'),
        width: 18,
        value: (user: UserSearchData) => user.expertLevel || user.mainExpertLevel
      },
      { key: 'language', header: t('common.language'), width: 14, value: (user: UserSearchData) => user.language },
      {
        key: 'createdAt',
        header: t('common.createdAt'),
        width: 20,
        value: (user: UserSearchData) => formatDateTime(user.createdAt)
      },
      {
        key: 'updatedAt',
        header: t('common.updatedAt'),
        width: 20,
        value: (user: UserSearchData) => formatDateTime(user.updatedAt)
      }
    ]

    const formatDateTime = (dateString: string) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString(locale.value, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const buildUserFilterSummary = () => {
      const filters = dataTableRequest.value.data || {}
      const items = [
        filters.grpNumber ? `${t('user.search.list.grpNumber')}: ${filters.grpNumber}` : null,
        filters.fullName ? `${t('user.search.list.full_name')}: ${filters.fullName}` : null,
        filters.role ? `${t('user.search.list.role')}: ${filters.role}` : null,
        filters.expertLevel ? `${t('user.search.list.expert_level')}: ${filters.expertLevel}` : null,
        filters.department ? `${t('user.search.list.departmentName')}: ${filters.department}` : null,
        filters.status !== undefined && filters.status !== null
          ? `${t('user.search.list.status')}: ${
            Number(filters.status) === 1
              ? t('common.status.active')
              : Number(filters.status) === 0
                ? t('common.status.inactive')
                : t('common.status.deleted')
          }`
          : null
      ].filter(Boolean)

      return items.length ? items.join(' | ') : locale.value === 'ar' ? 'لا توجد فلاتر مطبقة' : 'No filters applied'
    }

    const buildUserExportMetadata = (rows: UserSearchData[]) => {
      const sortValue = `${dataTableRequest.value.sortBy || 'updatedAt'} (${dataTableRequest.value.sortDesc ? 'desc' : 'asc'})`
      const departments = new Set(rows.map((row) => row.departmentName || row.shortDepartmentName).filter(Boolean))
      const sectors = new Set(rows.map((row) => row.sectorName).filter(Boolean))

      return [
        { label: locale.value === 'ar' ? 'إجمالي النتائج' : 'Total results', value: String(rows.length) },
        { label: locale.value === 'ar' ? 'الإدارات المشمولة' : 'Departments covered', value: String(departments.size) },
        { label: locale.value === 'ar' ? 'القطاعات المشمولة' : 'Sectors covered', value: String(sectors.size) },
        { label: locale.value === 'ar' ? 'ترتيب التصدير' : 'Export sorting', value: sortValue },
        { label: locale.value === 'ar' ? 'الفلاتر المطبقة' : 'Applied filters', value: buildUserFilterSummary() }
      ]
    }

    const buildUserExportLegendItems = (rows: UserSearchData[]): ExcelLegendItem[] =>
      Array.from(new Set(rows.map((row) => row.status).filter(Boolean))).map((status) => {
        const appearance = getUserStatusAppearance(status)
        const count = rows.filter((row) => row.status === status).length

        return {
          label: getLocalizedUserStatusLabel(status),
          description:
            locale.value === 'ar'
              ? `${count} مستخدمين مطابقين للفلاتر الحالية`
              : `${count} users matching the current filters`,
          fillColor: appearance.fillColor,
          borderColor: appearance.borderColor,
          textColor: appearance.textColor
        }
      })

    const buildUserExportMetricBars = (rows: UserSearchData[]): ExcelMetricBarItem[] => {
      if (!rows.length) return []

      const bars: ExcelMetricBarItem[] = []
      const pushMetric = (label: string, count: number, color: string, helper: string) => {
        const percentage = (count / rows.length) * 100
        bars.push({
          label,
          value: Number(percentage.toFixed(1)),
          valueLabel: `${count} / ${rows.length} (${percentage.toFixed(1)}%)`,
          color,
          helper,
          trackColor: '#E8F2EE'
        })
      }

      Array.from(new Set(rows.map((row) => row.status).filter(Boolean))).forEach((status) => {
        const count = rows.filter((row) => row.status === status).length
        const appearance = getUserStatusAppearance(status)
        pushMetric(
          getLocalizedUserStatusLabel(status),
          count,
          appearance.metricColor,
          locale.value === 'ar'
            ? 'قياس توزيع حالات المستخدمين ضمن نتائج التصفية الحالية.'
            : 'Status distribution within the current filtered user result.'
        )
      })

      const topDepartments = Array.from(
        rows.reduce((map, row) => {
          const key = row.departmentName || row.shortDepartmentName || '-'
          map.set(key, (map.get(key) ?? 0) + 1)
          return map
        }, new Map<string, number>())
      )
        .sort((a, b) => b[1] - a[1])
        .slice(0, 3)

      topDepartments.forEach(([department, count]) => {
        pushMetric(
          department,
          count,
          '#0F6CBD',
          locale.value === 'ar'
            ? 'إحدى أكثر الإدارات حضوراً ضمن نتائج التصفية الحالية.'
            : 'One of the most represented departments in the current filtered result.'
        )
      })

      return bars
    }

    const fetchAllUsersForExport = async (): Promise<UserSearchData[]> => {
      const total = totalItems.value
      if (!total) return []

      const sortBy = dataTableRequest.value.sortBy || 'updatedAt'
      const sortDirection = dataTableRequest.value.sortDesc ? 'desc' : 'asc'
      const batchSize = Math.min(Math.max(itemsPerPage.value, 100), 250)
      const totalPages = Math.max(Math.ceil(total / batchSize), 1)
      const collectedRows: UserSearchData[] = []
      const seenIds = new Set<number | string>()

      for (let page = 0; page < totalPages; page += 1) {
        const response = await UserClientService.search(
          dataTableRequest.value.data,
          page,
          batchSize,
          sortBy,
          sortDirection
        )

        for (const user of response?.data ?? []) {
          const normalized = normalizeUserRecord(user)
          const identity = normalized.id ?? normalized.grpNumber

          if (!seenIds.has(identity)) {
            seenIds.add(identity)
            collectedRows.push(normalized)
          }
        }

        if ((response?.data?.length ?? 0) < batchSize) {
          break
        }
      }

      return collectedRows
    }

    // Event Handlers
    const onSearch = (values: any) => {
      dataTableRequest.value.data = values
      dataTableRequest.value.currentPage = 0
      loadItems({
        page: 0,
        itemsPerPage: itemsPerPage.value,
        sortBy: null,
        filter: values
      })
    }

    const onClearSearch = () => {
      dataTableRequest.value.data = {
        grpNumber: undefined,
        role: undefined,
        fullName: undefined,
        expertLevel: undefined,
        status: undefined,
        department: undefined
      }
      fetchUserRecords()
    }

    const onRefresh = async () => {
      await fetchUserRecords()
    }

    const onExport = async () => {
      await exportUserExcelFile()
    }

    const onAction = (actionData: { actionKey: string; item: any }) => {
      const { actionKey, item } = actionData
      switch (actionKey) {
        case 'view':
          viewUserDetails(item.grpNumber)
          break
        case 'edit':
          updateUserScreen(item.grpNumber)
          break
        case 'delete':
          openDeleteDialog(item.grpNumber)
          break
      }
    }

    const onColumnAction = (data: any) => {
      if (data.type === 'status-change') {
        console.log('Status change action received:', data)
        updateStatus(data.item, data.status)
      }
    }

    const onTableUpdate = (options: any) => {
      loadItems(options)
    }

    const onItemsPerPageUpdate = (newItemsPerPage: number) => {
      itemsPerPage.value = newItemsPerPage
      dataTableRequest.value.pageSize = newItemsPerPage
      loadItems({
        page: dataTableRequest.value.currentPage,
        itemsPerPage: newItemsPerPage,
        sortBy: null,
        filter: dataTableRequest.value.data
      })
    }

    // Business Logic Methods
    const exportUserExcelFile = async () => {
      try {
        exportLoading.value = true
        const exportRows = await fetchAllUsersForExport()

        if (!exportRows.length) {
          return
        }

        await exportRowsToExcel<UserSearchData>({
          fileName: locale.value === 'ar' ? 'تقرير-المستخدمين' : 'users-report',
          sheetName: locale.value === 'ar' ? 'المستخدمون' : 'Users',
          title: t('user.search.main-title'),
          headerTitle: locale.value === 'ar' ? 'تقرير المستخدمين' : 'Users Report',
          headerDescription:
            locale.value === 'ar'
              ? 'تقرير تحليلي مبني على نتائج المستخدمين المطابقة للفلاتر الحالية'
              : 'Analytics report generated from the current filtered users result',
          subtitle:
            locale.value === 'ar'
              ? 'تقرير تحليلي مبني على نتائج التصفية الحالية'
              : 'Analytics report based on the current filtered results',
          footerNote:
            locale.value === 'ar'
              ? 'تم إنشاء هذا الملف من شاشة إدارة المستخدمين مع تضمين الفلاتر والتحليلات الحالية.'
              : 'This file was generated from the users management screen with the current filters and analytics.',
          locale: locale.value,
          rtl: locale.value === 'ar',
          columns: buildUserExportColumns(),
          rows: exportRows,
          metadata: buildUserExportMetadata(exportRows),
          legendTitle: locale.value === 'ar' ? 'دلالات حالات المستخدمين' : 'User status legend',
          legendItems: buildUserExportLegendItems(exportRows),
          metricBarsTitle: locale.value === 'ar' ? 'تحليلات المستخدمين' : 'User analytics',
          metricBars: buildUserExportMetricBars(exportRows),
          autoFitColumns: true,
          minAutoFitWidth: 12,
          maxAutoFitWidth: 42,
          freezeHeaderRow: true,
          getCellStyle: ({ row, column }) => {
            if (column.key !== 'status') {
              return null
            }

            const appearance = getUserStatusAppearance(row.status)
            return {
              fillColor: appearance.fillColor,
              borderColor: appearance.borderColor,
              textColor: appearance.textColor,
              bold: true,
              horizontal: 'center'
            }
          }
        })

        toast.success(t('Messages.success.export.completed'))
      } catch (error) {
        console.error('Export error:', error)
        toast.error(t('Messages.error.export.failed'))
      } finally {
        exportLoading.value = false
      }
    }

    const openDeleteDialog = (grpNumber: number) => {
      userToDelete.value = grpNumber
      deleteDialog.value = true
    }

    const cancelUserDeletion = () => {
      deleteDialog.value = false
    }

    const confirmUserDeletion = async () => {
      try {
        await UserClientService.removeUser(userToDelete.value)
        deleteDialog.value = false
        toast.success(t('Messages.success.user.deleted'))
        await fetchUserRecords()
      } catch (error) {
        console.error('Error deleting user:', error)
        toast.error(t('Messages.error.user.deletionFailed'))
      }
    }

    const loadItems = ({ page, itemsPerPage, sortBy, filter }) => {
      if (sortBy) {
        const sortingKey = sortBy[0]?.key
        let sortingOrder = sortBy[0]?.order
        if (sortingOrder === undefined) sortingOrder = 'desc'
        dataTableRequest.value.sortBy = sortingKey
        dataTableRequest.value.sortDesc = sortingOrder === 'desc'
      }
      dataTableRequest.value.currentPage = page
      dataTableRequest.value.pageSize = itemsPerPage
      if (filter) {
        dataTableRequest.value.data = filter
      }
      userSearch(
        dataTableRequest,
        totalItems,
        (loadingVar: any) => {
          setLoading(loadingVar)
        },
        records,
        locale.value
      )
    }

    const viewUserDetails = async (grpNumber: number) => {
      selectUserGrpNumber.value = Number(grpNumber)
      displayUserDetail.value = true
    }

    const updateUserScreen = (grpNumber: number) => {
      router.push({ name: 'UpdateUser', params: { grpNumber } })
    }

    const closeUserDetail = () => {
      selectUserGrpNumber.value = 0
      displayUserDetail.value = false
    }

    const fetchUserRecords = async () => {
      try {
        dataTableRequest.value.sortDesc = true
        dataTableRequest.value.currentPage = 0
        userSearch(
          dataTableRequest,
          totalItems,
          (loadingVar: any) => {
            setLoading(loadingVar)
          },
          records,
          locale.value
        )
      } catch (error) {
        console.error('Error fetching user records:', error)
        toast.error(t('Messages.common.loadFailed'))
      } finally {
        setSkeletonLoading(false)
      }
    }

    const fetchUserDepartments = async () => {
      try {
        const response: any = await LookupClientService.getAllUserListLookup()
        if (response && response.departmentCoreResponseList) {
          departmentResponseList.value = response.departmentCoreResponseList
        }
        if (response && response.roles) {
          rolesResponseList.value = response.roles
        }
        await nextTick()
      } catch (error: any) {
        console.error('Error fetching departments and roles:', error)
        if (error.response?.status === 403) {
          toast.error(t('Messages.error.permission.denied'))
        } else {
          toast.error(t('Messages.common.loadFailed'))
        }
      }
    }

    const updateStatus = async (item: any, statusValue: number) => {
      try {
        console.log('Updating status for user:', item.grpNumber, 'to status:', statusValue, 'type:', typeof statusValue)
        item.statusUpdating = true

        if (statusValue === 0) {
          console.log('Setting status to INACTIVE')
          await updateUserStatus(item.grpNumber, UserStatus.INACTIVE)
        }
        if (statusValue === 1) {
          console.log('Setting status to ACTIVE')
          await updateUserStatus(item.grpNumber, UserStatus.ACTIVE)
        }
        if (statusValue === 2) {
          console.log('Setting status to DELETED')
          await updateUserStatus(item.grpNumber, UserStatus.DELETED)
        }
        await fetchUserRecords()
      } catch (error) {
        console.error('Error updating user status:', error)
        toast.error(t('Messages.error.user.statusUpdateFailed'))
      } finally {
        item.statusUpdating = false
      }
    }

    const updateUserStatus = async (userGrp: number, userStatus: UserStatus) => {
      try {
        await update(
          URIS.USER.BASE +
          URIS.USER.UPDATE_USER_STATUS +
          userGrp +
          Characters.QUESTION_MARK +
          Fields.USER_STATUS +
          Characters.EQUAL +
          userStatus,
          null
        ).then((response: any) => {
          if (response.success) {
            toast.success(t('Messages.success.user.statusUpdated'))
          }
        })
      } catch (error) {
        console.error('Error updating challenge status:', error)
        // Handle error scenarios as needed
      }
    }

    onMounted(async () => {
      sidebarStore.show()
      await fetchUserDepartments()
      await fetchUserRecords()
    })

    return {
      // Enhanced Data Table
      loading,
      skeletonLoading,
      searchExpanded,
      exportLoading,
      itemsPerPage,
      totalItems,
      safeRecords,
      searchValues,
      basicSearchFields,
      advancedSearchFields,
      actions,
      columnTemplates,
      headers,

      // UI State
      deleteDialog,
      displayUserDetail,
      userToDisplay,

      // Methods
      openDeleteDialog,
      cancelUserDeletion,
      confirmUserDeletion,
      closeUserDetail,
      viewUserDetails,
      updateUserScreen,
      updateStatus,
      exportUserExcelFile,
      isAdminOrModerator,

      // Event Handlers
      onSearch,
      onClearSearch,
      onRefresh,
      onExport,
      onAction,
      onColumnAction,
      onTableUpdate,
      onItemsPerPageUpdate,
      selectUserGrpNumber
    }
  }
})
</script>

<style src="./Users.scss" lang="scss" scoped></style>
