<!-- ChallengeList.vue -->
<template src="./challenges.html" lang="html"></template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import EnhancedDataTable from '@/components/common/table/EnhancedDataTable.vue'
import { useRouter } from 'vue-router'
import 'vue3-toastify/dist/index.css'
import { deleteRequest, get, searchRequest } from '@/utils/GenericApiHelper'
import { Characters, URIS } from '@/constants/DpConstants'
import { ChallengeSearchRequest } from '@/dto/challenge/ChallengeSearchRequest'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse'
import { PaginationResponse } from '@/dto/generic/PaginationResponse'
import { Pageable } from '@/dto/pagination/Pageable'
import { toast } from 'vue3-toastify'
import { isAdminOrModerator } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse'
import { type ExcelLegendItem, type ExcelMetricBarItem, exportRowsToExcel } from '@/utils/excel/exportRowsToExcel'
import { resolveLocalizedValue } from '@/utils/localizationUtils'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { ChallengeStatus } from '@/dto/challenge/ChallengeStatus'
import {
  formatChallengeStatusLabel,
  getChallengeStatusAppearance,
  normalizeChallengeStatus
} from '@/utils/challengeStatusUtils'
import ModeratorCell from '@/views/challenges/list/components/ModeratorCell.vue'
import { ChallengeVisibility } from '@/dto/enums/ChallengeVisibility'

export default defineComponent({
  name: 'ChallengeList',
  components: {
    ConfirmationDialog,
    EnhancedDataTable,
    GenericDashboardHeaderView,
    ModeratorCell
  },
  setup() {
    const { t, locale } = useI18n()
    const sidebarStore = useSidebarStore()
    const router = useRouter()

    // State
    const loading = ref(false)
    const itemsPerPage = ref(10)
    const skeletonLoading = ref(true)
    const deleteDialog = ref(false)
    const idToDelete = ref(0)
    const challengeResponse = ref<PaginationResponse<ChallengesResponse>>()
    const currentPageable = ref<Pageable>(defaultPageable())
    const theTotalElements = ref<number>(0)
    const listOfChallengeStatus = ref<string[]>([])
    const allDepartmentList = ref<DepartmentResponse[]>([])

    const challengeSearchRequest = ref<ChallengeSearchRequest>({
      title: null,
      status: null,
      moderator: null,
      visibility: null,
      department: null,
      description: null,
      createdAt: null,
      endTime: null,
      endVotingDate: null,
      startVotingDate: null,
      lastModifiedAt: null,
      startTime: null
    })

    // Table configuration
    const headers = computed(() => [
      { title: t('common.id'), align: 'start', sortable: true, key: 'id', width: '4%' },
      { title: t('challenge.name'), align: 'start', sortable: true, key: 'localTitle', width: '12%' },
      { title: t('challenge.description'), align: 'start', sortable: true, key: 'localDescription', width: '18%' },
      { title: t('challenge.moderator'), align: 'start', sortable: true, key: 'moderatorGrpNumber', width: '13%' },
      { title: t('challenge.department'), align: 'start', sortable: true, key: 'departmentName', width: '10%' },
      { title: t('challenge.votingTime'), align: 'start', sortable: true, key: 'startTime', width: '13%' },
      { title: t('challenge.visibility'), align: 'start', sortable: false, key: 'visibility', width: '7%' },
      { title: t('challenge.createdAt'), align: 'start', sortable: true, key: 'createdAt', width: '10%' },
      { title: t('common.statusName'), align: 'start', sortable: true, key: 'status', width: '7%' },
      { title: t('common.actions'), align: 'end', sortable: false, key: 'actions', width: '6%' }
    ])

    const basicSearchFields = computed(() => [
      {
        key: 'title',
        label: t('challenge.name'),
        component: 'v-text-field',
        icon: 'mdi-format-title',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'moderator',
        label: t('challenge.moderator'),
        component: 'v-text-field',
        icon: 'mdi-account',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'department',
        label: t('user.search.list.departmentName'),
        component: 'v-select',
        items: allDepartmentList.value,
        itemTitle: 'shortName',
        itemValue: 'shortName',
        icon: 'mdi-domain',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'status',
        label: t('common.statusName'),
        component: 'v-select',
        items: listOfChallengeStatus.value,
        icon: 'mdi-flag',
        cols: 12,
        sm: 6,
        md: 3
      }
    ])

    const advancedSearchFields = computed(() => [
      {
        key: 'createdAt',
        label: t('common.createdAt'),
        component: 'dp-date-picker',
        icon: 'mdi-calendar',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'lastModifiedAt',
        label: t('common.updatedAt'),
        component: 'dp-date-picker',
        icon: 'mdi-calendar-edit',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'startVotingDate',
        label: t('common.startTime'),
        component: 'dp-date-picker',
        icon: 'mdi-calendar-start',
        cols: 12,
        sm: 6,
        md: 3
      },
      {
        key: 'endVotingDate',
        label: t('common.endTime'),
        component: 'dp-date-picker',
        icon: 'mdi-calendar-end',
        cols: 12,
        sm: 6,
        md: 3
      }
    ])

    // Table actions configuration
    const baseActions = [
      {
        key: 'view',
        label: t('common.view'),
        icon: 'mdi-eye',
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

    // Function to get actions for a specific row
    function getRowActions(item) {
      if (item && item.isAllowedToUpdateOrDelete === true) {
        // Show all actions if allowed
        return baseActions
      }
      // Otherwise, show only view
      return baseActions.filter((a) => a.key === 'view')
    }

    const columnOptions = {}

    function defaultPageable(): Pageable {
      return {
        page: 0,
        itemsPerPage: itemsPerPage.value,
        sortBy: [{ key: 'updatedAt', order: 'desc' }]
      }
    }

    // Methods
    const searchButton = async (pageable: Pageable) => {
      loading.value = true
      currentPageable.value = {
        page: pageable?.page ?? 0,
        itemsPerPage: pageable?.itemsPerPage ?? itemsPerPage.value,
        sortBy: pageable?.sortBy?.length ? [...pageable.sortBy] : [...defaultPageable().sortBy]
      }

      const pageRequest = {
        page: pageable?.page ?? 0,
        size: pageable?.itemsPerPage ?? itemsPerPage.value,
        sort: pageable?.sortBy.length > 0 ? `${getSortKey(pageable)},${pageable.sortBy[0].order}` : 'updatedAt,desc'
      }

      const response = (await searchRequest(
        'get',
        URIS.CHALLENGES.BASE + URIS.CHALLENGES.SEARCH,
        null,
        { ...challengeSearchRequest.value, ...pageRequest },
        false
      )) as PaginationResponse<ChallengesResponse>

      theTotalElements.value = response.totalElements
      challengeResponse.value = response as PaginationResponse<ChallengesResponse>
      loading.value = false
      skeletonLoading.value = false
    }

    const getSortKey = (pageable: Pageable) => {
      if (pageable?.sortBy.length > 0) {
        if (pageable.sortBy[0].key === 'localDescription') {
          return locale.value === 'ar' ? 'descriptionAr' : 'descriptionEn'
        }
        if (pageable.sortBy[0].key === 'localTitle') {
          return locale.value === 'ar' ? 'titleAr' : 'titleEn'
        } else {
          return pageable.sortBy[0].key
        }
      } else {
        return 'updatedAt'
      }
    }

    const loadDepartmentList = async () => {
      const response = get<SuccessResponse<DepartmentResponse[]>>(URIS.CORE.BASE + URIS.CORE.DEPARTMENT_ALL)
      response.then((res: SuccessResponse<DepartmentResponse[]>) => {
        if (res.success) {
          allDepartmentList.value = res.data as DepartmentResponse[]
        }
      })
    }

    const loadStatusList = async () => {
      const response = get<SuccessResponse<string[]>>(URIS.CHALLENGES.RETRIEVE_STATUS)
      response.then((res: SuccessResponse<string[]>) => {
        if (res.success) {
          listOfChallengeStatus.value = res.data as string[]
        }
      })
    }

    const viewChallengeDetails = (challengeId: number) => {
      router.push({
        name: 'ChallengeView',
        params: { challengeId }
      })
    }

    const updateScreen = (id: number) => {
      router.push({ name: 'ChallengeUpdate', params: { id } })
    }

    const confirmDeletion = async () => {
      await deleteRequest(URIS.CHALLENGES.BASE + Characters.SLASH + idToDelete.value)
      toast.success(t('Messages.success.challenge.deleted'))
      deleteDialog.value = false
      await searchButton(defaultPageable())
    }

    const cancelDeletion = () => {
      deleteDialog.value = false
    }

    const clearSearch = () => {
      challengeSearchRequest.value = {
        title: null,
        status: null,
        moderator: null,
        visibility: null,
        department: null,
        description: null,
        createdAt: null,
        endTime: null,
        endVotingDate: null,
        startVotingDate: null,
        lastModifiedAt: null,
        startTime: null
      }
      searchButton(defaultPageable())
    }

    const getLocalizedVisibilityLabel = (visibility?: string | null) => {
      switch (visibility) {
        case ChallengeVisibility.PUBLIC:
          return t('common.visibility.public')
        case ChallengeVisibility.PRIVATE:
          return t('common.visibility.private')
        default:
          return visibility || '-'
      }
    }

    const getLocalizedStatusLabel = (status?: string | null) => {
      const normalizedStatus = normalizeChallengeStatus(status)

      if (locale.value === 'ar') {
        switch (normalizedStatus) {
          case ChallengeStatus.COMPLETED:
            return 'مكتمل'
          case ChallengeStatus.OPEN:
            return 'مفتوح'
          case ChallengeStatus.CLOSED:
            return 'مغلق'
          case ChallengeStatus.VOTING:
            return 'التصويت'
          case ChallengeStatus.REVISED:
            return 'تم مراجعته'
          case ChallengeStatus.PAUSED:
            return 'الإيقاف المؤقت'
          case ChallengeStatus.UNDER_VALIDATOR_REVIEW:
            return 'تحت مراجعة المدقق'
          case ChallengeStatus.UNDER_ADMIN_REVIEW:
            return 'تحت مراجعة الأدمن'
          case ChallengeStatus.NEW:
            return 'جديد'
          case ChallengeStatus.URGENT:
            return 'ضروري'
          default:
            return status || '-'
        }
      }

      return formatChallengeStatusLabel(status) || '-'
    }

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

    const buildExportColumns = () => [
      {
        key: 'rowNumber',
        header: t('common.srNo2'),
        width: 10,
        value: (_challenge: ChallengesResponse, index: number) => index + 1
      },
      {
        key: 'id',
        header: t('common.id'),
        width: 10,
        value: (challenge: ChallengesResponse) => challenge.id
      },
      {
        key: 'titleEn',
        header: `${t('challenge.name')} (${t('english')})`,
        width: 26,
        value: (challenge: ChallengesResponse) => challenge.titleEn
      },
      {
        key: 'titleAr',
        header: `${t('challenge.name')} (${t('arabic')})`,
        width: 26,
        value: (challenge: ChallengesResponse) => challenge.titleAr
      },
      {
        key: 'descriptionEn',
        header: `${t('challenge.description')} (${t('english')})`,
        width: 34,
        value: (challenge: ChallengesResponse) => challenge.descriptionEn
      },
      {
        key: 'descriptionAr',
        header: `${t('challenge.description')} (${t('arabic')})`,
        width: 34,
        value: (challenge: ChallengesResponse) => challenge.descriptionAr
      },
      {
        key: 'moderatorGrpNumber',
        header: t('challenge.moderator'),
        width: 16,
        value: (challenge: ChallengesResponse) => challenge.moderatorGrpNumber
      },
      {
        key: 'departmentName',
        header: t('challenge.department'),
        width: 22,
        value: (challenge: ChallengesResponse) => challenge.departmentName
      },
      {
        key: 'visibility',
        header: t('challenge.visibility'),
        width: 16,
        value: (challenge: ChallengesResponse) => getLocalizedVisibilityLabel(challenge.visibility)
      },
      {
        key: 'status',
        header: t('common.statusName'),
        width: 22,
        value: (challenge: ChallengesResponse) => getLocalizedStatusLabel(challenge.status)
      },
      {
        key: 'startTime',
        header: t('common.startTime'),
        width: 22,
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.startTime)
      },
      {
        key: 'endTime',
        header: t('common.endTime'),
        width: 22,
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.endTime)
      },
      {
        key: 'createdAt',
        header: t('common.createdAt'),
        width: 22,
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.createdAt ?? '-')
      },
      {
        key: 'updatedAt',
        header: t('common.updatedAt'),
        width: 22,
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.updatedAt ?? '-')
      }
    ]

    const buildAppliedFilterSummary = () => {
      const filters = [
        challengeSearchRequest.value.title
          ? `${t('challenge.name')}: ${challengeSearchRequest.value.title}`
          : null,
        challengeSearchRequest.value.moderator
          ? `${t('challenge.moderator')}: ${challengeSearchRequest.value.moderator}`
          : null,
        challengeSearchRequest.value.department
          ? `${t('challenge.department')}: ${challengeSearchRequest.value.department}`
          : null,
        challengeSearchRequest.value.status
          ? `${t('common.statusName')}: ${getLocalizedStatusLabel(challengeSearchRequest.value.status)}`
          : null,
        challengeSearchRequest.value.visibility
          ? `${t('challenge.visibility')}: ${getLocalizedVisibilityLabel(challengeSearchRequest.value.visibility)}`
          : null,
        challengeSearchRequest.value.createdAt
          ? `${t('common.createdAt')}: ${formatDate(challengeSearchRequest.value.createdAt)}`
          : null,
        challengeSearchRequest.value.lastModifiedAt
          ? `${t('common.updatedAt')}: ${formatDate(challengeSearchRequest.value.lastModifiedAt)}`
          : null,
        challengeSearchRequest.value.startVotingDate
          ? `${t('common.startTime')}: ${formatDate(challengeSearchRequest.value.startVotingDate)}`
          : null,
        challengeSearchRequest.value.endVotingDate
          ? `${t('common.endTime')}: ${formatDate(challengeSearchRequest.value.endVotingDate)}`
          : null
      ].filter(Boolean)

      if (filters.length > 0) {
        return filters.join(' | ')
      }
      return locale.value === 'ar' ? 'لا توجد فلاتر مطبقة' : 'No filters applied'
    }

    const buildExportMetadata = (rows: ChallengesResponse[]) => {
      const uniqueDepartments = new Set(rows.map((row) => row.departmentName).filter(Boolean))
      const uniqueModerators = new Set(rows.map((row) => row.moderatorGrpNumber).filter(Boolean))
      const publicCount = rows.filter((row) => row.visibility === ChallengeVisibility.PUBLIC).length
      const privateCount = rows.filter((row) => row.visibility === ChallengeVisibility.PRIVATE).length
      const currentSort = currentPageable.value?.sortBy?.[0]
      const sortValue = currentSort ? `${getSortKey(currentPageable.value as Pageable)} (${currentSort.order})` : 'updatedAt (desc)'

      return [
        {
          label: locale.value === 'ar' ? 'إجمالي النتائج' : 'Total results',
          value: String(rows.length)
        },
        {
          label: locale.value === 'ar' ? 'الإدارات المشمولة' : 'Departments covered',
          value: String(uniqueDepartments.size)
        },
        {
          label: locale.value === 'ar' ? 'المشرفون المشمولون' : 'Moderators covered',
          value: String(uniqueModerators.size)
        },
        {
          label: locale.value === 'ar' ? 'التحديات العامة' : 'Public challenges',
          value: String(publicCount)
        },
        {
          label: locale.value === 'ar' ? 'التحديات الخاصة' : 'Private challenges',
          value: String(privateCount)
        },
        {
          label: locale.value === 'ar' ? 'ترتيب التصدير' : 'Export sorting',
          value: sortValue
        },
        {
          label: locale.value === 'ar' ? 'الفلاتر المطبقة' : 'Applied filters',
          value: buildAppliedFilterSummary()
        }
      ]
    }

    const buildExportLegendItems = (rows: ChallengesResponse[]): ExcelLegendItem[] => {
      const uniqueStatuses = Array.from(new Set(rows.map((row) => row.status).filter(Boolean)))

      return uniqueStatuses.map((status) => {
        const appearance = getChallengeStatusAppearance(status)
        const count = rows.filter((row) => row.status === status).length

        return {
          label: getLocalizedStatusLabel(status),
          description:
            locale.value === 'ar'
              ? `${count} من التحديات المطابقة للفلاتر الحالية`
              : `${count} matching challenges in the current filtered export`,
          fillColor: appearance.backgroundColor,
          borderColor: appearance.borderColor,
          textColor: appearance.textColor
        }
      })
    }

    const buildExportMetricBars = (rows: ChallengesResponse[]): ExcelMetricBarItem[] => {
      if (rows.length === 0) {
        return []
      }

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

      pushMetric(
        locale.value === 'ar' ? 'مرئية للعامة' : 'Public visibility',
        rows.filter((row) => row.visibility === ChallengeVisibility.PUBLIC).length,
        '#0F6CBD',
        locale.value === 'ar'
          ? 'عدد التحديات المتاحة بنمط عام ضمن نتائج التصفية الحالية.'
          : 'Challenges available with public visibility inside the current filtered result.'
      )

      pushMetric(
        locale.value === 'ar' ? 'مرئية بشكل خاص' : 'Private visibility',
        rows.filter((row) => row.visibility === ChallengeVisibility.PRIVATE).length,
        '#7A3EF0',
        locale.value === 'ar'
          ? 'عدد التحديات المصنفة كنمط خاص ضمن نتائج التصفية الحالية.'
          : 'Challenges classified with private visibility inside the current filtered result.'
      )

      const statusCounts = new Map<string, number>()
      rows.forEach((row) => {
        const key = row.status || '-'
        statusCounts.set(key, (statusCounts.get(key) ?? 0) + 1)
      })

      Array.from(statusCounts.entries())
        .sort((a, b) => b[1] - a[1])
        .slice(0, 4)
        .forEach(([status, count]) => {
          const appearance = getChallengeStatusAppearance(status)
          pushMetric(
            getLocalizedStatusLabel(status),
            count,
            appearance.color,
            locale.value === 'ar'
              ? `الحالة الأكثر حضوراً ضمن نتائج التصفية الحالية.`
              : 'One of the most represented statuses in the current filtered result.'
          )
        })

      return bars
    }

    const fetchAllChallengesForExport = async () => {
      const totalElements = challengeResponse.value?.totalElements ?? theTotalElements.value

      if (!totalElements) {
        return []
      }

      const exportPageable = currentPageable.value ?? defaultPageable()
      const sort =
        exportPageable.sortBy.length > 0
          ? `${getSortKey(exportPageable)},${exportPageable.sortBy[0].order}`
          : 'updatedAt,desc'
      const batchSize = Math.min(Math.max(exportPageable.itemsPerPage, 100), 250)
      const collectedRows: ChallengesResponse[] = []
      const seenIds = new Set<number>()
      const totalPages = Math.max(Math.ceil(totalElements / batchSize), 1)

      for (let page = 0; page < totalPages; page += 1) {
        const response = (await searchRequest(
          'get',
          URIS.CHALLENGES.BASE + URIS.CHALLENGES.SEARCH,
          null,
          {
            ...challengeSearchRequest.value,
            page,
            size: batchSize,
            sort
          },
          false
        )) as PaginationResponse<ChallengesResponse>

        for (const row of response?.data ?? []) {
          if (!seenIds.has(row.id)) {
            seenIds.add(row.id)
            collectedRows.push(row)
          }
        }

        if ((response?.data?.length ?? 0) < batchSize) {
          break
        }
      }

      return collectedRows
    }

    const exportData = async () => {
      try {
        if (!challengeResponse.value?.data || challengeResponse.value.data.length === 0) {
          toast.warning(t('common.noDataToExport'))
          return
        }

        loading.value = true
        const exportRows = await fetchAllChallengesForExport()

        if (exportRows.length === 0) {
          toast.warning(t('common.noDataToExport'))
          return
        }

        await exportRowsToExcel<ChallengesResponse>({
          fileName: locale.value === 'ar' ? 'تقرير-التحديات' : 'challenges-report',
          sheetName: locale.value === 'ar' ? 'التحديات' : 'Challenges',
          title: t('common.challenges'),
          headerTitle: locale.value === 'ar' ? 'تقرير التحديات' : 'Challenges Report',
          headerDescription:
            locale.value === 'ar'
              ? 'تقرير تحليلي معتمد على نتائج التصفية الحالية من منصة شرطة دبي'
              : 'Analytics report generated from the current filtered challenge results in Dubai Police platform',
          subtitle:
            locale.value === 'ar'
              ? `تقرير تحليلي مبني على نتائج التصفية الحالية`
              : 'Analytics report based on the current filtered results',
          footerNote:
            locale.value === 'ar'
              ? 'تم إنشاء هذا الملف من شاشة إدارة التحديات مع تضمين الفلاتر والتحليلات الحالية.'
              : 'This file was generated from the challenge management screen with the current filters and analytics.',
          locale: locale.value,
          rtl: locale.value === 'ar',
          columns: buildExportColumns(),
          rows: exportRows,
          metadata: buildExportMetadata(exportRows),
          legendTitle: locale.value === 'ar' ? 'دلالات الحالات' : 'Status legend',
          legendItems: buildExportLegendItems(exportRows),
          metricBarsTitle: locale.value === 'ar' ? 'تحليلات التحديات' : 'Challenge analytics',
          metricBars: buildExportMetricBars(exportRows),
          autoFitColumns: true,
          minAutoFitWidth: 12,
          maxAutoFitWidth: 42,
          freezeHeaderRow: true,
          getCellStyle: ({ row, column }) => {
            if (column.key === 'status') {
              const appearance = getChallengeStatusAppearance(row.status)

              return {
                fillColor: appearance.backgroundColor,
                borderColor: appearance.borderColor,
                textColor: appearance.textColor,
                bold: true,
                horizontal: 'center'
              }
            }

            if (column.key === 'visibility') {
              const isPublic = row.visibility === ChallengeVisibility.PUBLIC

              return {
                fillColor: isPublic ? '#EAF4FF' : '#F4EFFF',
                borderColor: isPublic ? '#B7D4F0' : '#D9C9FB',
                textColor: isPublic ? '#0B4F8C' : '#5B21B6',
                bold: true,
                horizontal: 'center'
              }
            }

            return null
          }
        })

        toast.success(t('Messages.success.export.completed'))
      } catch (error) {
        console.error('Export error:', error)
        toast.error(t('Messages.error.export.failed'))
      } finally {
        loading.value = false
      }
    }

    const refreshData = () => {
      searchButton(defaultPageable())
    }

    const handleSearch = (searchValues: any) => {
      challengeSearchRequest.value = searchValues
      searchButton(defaultPageable())
    }

    const handleTableAction = ({ actionKey, item }: { actionKey: string; item: any }) => {
      switch (actionKey) {
        case 'view':
          viewChallengeDetails(item.id)
          break
        case 'edit':
          updateScreen(item.id)
          break
        case 'delete':
          openDeleteDialog(item.id)
          break
      }
    }

    const handleTableUpdate = (options: any) => {
      searchButton(options)
    }

    const onItemsPerPageUpdate = (newItemsPerPage: number) => {
      itemsPerPage.value = newItemsPerPage
      const updatedPageable = {
        ...defaultPageable(),
        itemsPerPage: newItemsPerPage,
        page: 0 // Reset to first page when changing items per page
      }
      searchButton(updatedPageable)
    }

    const openDeleteDialog = (id: number) => {
      idToDelete.value = id
      deleteDialog.value = true
    }

    // Status related methods
    const getStatusColor = (status: string) => getChallengeStatusAppearance(status).color

    const getStatusIcon = (status: string) => getChallengeStatusAppearance(status).icon

    const getStatusDescription = (status: string) => {
      const normalizedStatus = normalizeChallengeStatus(status)

      switch (normalizedStatus) {
        case ChallengeStatus.COMPLETED:
          return t('challenge.status.completed.description')
        case ChallengeStatus.OPEN:
          return t('challenge.status.open.description')
        case ChallengeStatus.CLOSED:
          return t('challenge.status.closed.description')
        case ChallengeStatus.VOTING:
          return t('challenge.status.voting.description')
        case ChallengeStatus.REVISED:
          return t('challenge.status.revised.description')
        case ChallengeStatus.PAUSED:
          return t('challenge.status.paused.description')
        case ChallengeStatus.UNDER_VALIDATOR_REVIEW:
          return t('challenge.status.validator_review.description')
        case ChallengeStatus.UNDER_ADMIN_REVIEW:
          return t('challenge.status.admin_review.description')
        case ChallengeStatus.NEW:
          return t('challenge.status.new.description')
        case ChallengeStatus.URGENT:
          return t('challenge.status.urgent.description')
        default:
          return status
      }
    }

    const formatDate = (dateString: string) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleDateString(locale.value, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }

    const formatTime = (dateString: string) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleTimeString(locale.value, {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const getStatusClass = (status: string) => {
      const normalizedStatus = normalizeChallengeStatus(status)

      if (normalizedStatus === ChallengeStatus.URGENT) return 'urgent-status'
      if (normalizedStatus === ChallengeStatus.NEW) return 'new-status'
      if (normalizedStatus === ChallengeStatus.VOTING) return 'voting-status'
      if (
        normalizedStatus === ChallengeStatus.UNDER_VALIDATOR_REVIEW ||
        normalizedStatus === ChallengeStatus.UNDER_ADMIN_REVIEW
      ) {
        return 'review-status'
      }

      return ''
    }

    const getStatusIconClass = (status: string) => {
      const normalizedStatus = normalizeChallengeStatus(status)

      if (normalizedStatus === ChallengeStatus.URGENT) return 'urgent-icon'
      if (normalizedStatus === ChallengeStatus.NEW) return 'new-icon'
      if (normalizedStatus === ChallengeStatus.VOTING) return 'voting-icon'
      if (
        normalizedStatus === ChallengeStatus.UNDER_VALIDATOR_REVIEW ||
        normalizedStatus === ChallengeStatus.UNDER_ADMIN_REVIEW
      ) {
        return 'review-icon'
      }

      return ''
    }

    const isUrgentStatus = (status: string) => normalizeChallengeStatus(status) === ChallengeStatus.URGENT

    const isNewStatus = (status: string) => normalizeChallengeStatus(status) === ChallengeStatus.NEW

    const columnTemplates = {
      moderatorGrpNumber: {
        component: ModeratorCell,
        options: {}
      },
      status: {
        component: 'StatusCell',
        options: {
          getStatusColor,
          getStatusIcon,
          getStatusDescription,
          getStatusClass,
          getStatusIconClass,
          isUrgentStatus,
          isNewStatus
        }
      },
      visibility: {
        component: 'VisibilityCell',
        options: {}
      },
      createdAt: {
        component: 'DateCell',
        options: {
          formatDate,
          formatTime
        }
      },
      startTime: {
        component: 'VotingTimeCell',
        options: {
          formatDate
        }
      }
    }

    const safeChallengeItems = computed(() =>
      (challengeResponse.value?.data || []).map((challenge) => ({
        ...challenge,
        localTitle: resolveLocalizedValue({
          locale: locale.value,
          ar: challenge.titleAr,
          en: challenge.titleEn,
          fallback: challenge.localTitle
        }),
        localDescription: resolveLocalizedValue({
          locale: locale.value,
          ar: challenge.descriptionAr,
          en: challenge.descriptionEn,
          fallback: challenge.localDescription
        })
      }))
    )

    onMounted(() => {
      sidebarStore.show()
      loadStatusList()
      loadDepartmentList()
      searchButton(defaultPageable())
    })

    return {
      headers,
      challengeSearchRequest,
      challengeResponse,
      loading,
      itemsPerPage,
      theTotalElements,
      skeletonLoading,
      deleteDialog,
      cancelDeletion,
      confirmDeletion,
      isAdminOrModerator,
      basicSearchFields,
      advancedSearchFields,
      tableActions: getRowActions,
      columnTemplates,
      columnOptions,
      handleSearch,
      clearSearch,
      exportData,
      refreshData,
      handleTableAction,
      handleTableUpdate,
      onItemsPerPageUpdate,
      openDeleteDialog,
      getStatusColor,
      getStatusIcon,
      getStatusDescription,
      formatDate,
      formatTime,
      getStatusClass,
      getStatusIconClass,
      isUrgentStatus,
      isNewStatus,
      safeChallengeItems
    }
  }
})
</script>

<style src="./challenges.scss" lang="scss" scoped></style>
