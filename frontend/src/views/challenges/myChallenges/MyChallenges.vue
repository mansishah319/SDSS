<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import EnhancedDataTable from '@/components/common/table/EnhancedDataTable.vue'
import { useRouter } from 'vue-router'
import 'vue3-toastify/dist/index.css'
import { get, searchRequest } from '@/utils/GenericApiHelper'
import { Fields, URIS } from '@/constants/DpConstants'
import { ChallengeSearchRequest } from '@/dto/challenge/ChallengeSearchRequest'
import { ChallengesResponse } from '@/dto/challenge/ChallengesResponse'
import { PaginationResponse } from '@/dto/generic/PaginationResponse'
import { Pageable } from '@/dto/pagination/Pageable'
import { isHasAnyRole } from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import Employee from '@/dto/Employee'
import { type ExcelLegendItem, type ExcelMetricBarItem, exportRowsToExcel } from '@/utils/excel/exportRowsToExcel'
import StatusCell from '@/views/challenges/list/components/StatusCell.vue'
import VisibilityCell from '@/views/challenges/list/components/VisibilityCell.vue'
import DateCell from '@/views/challenges/list/components/DateCell.vue'
import VotingTimeCell from '@/views/challenges/list/components/VotingTimeCell.vue'
import ModeratorCell from '@/views/challenges/list/components/ModeratorCell.vue'
import { resolveLocalizedValue } from '@/utils/localizationUtils'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { ChallengeStatus } from '@/dto/challenge/ChallengeStatus'
import {
  formatChallengeStatusLabel,
  getChallengeStatusAppearance,
  normalizeChallengeStatus
} from '@/utils/challengeStatusUtils'
import { ChallengeVisibility } from '@/dto/enums/ChallengeVisibility'

export default defineComponent({
  name: 'MyChallenges',
  components: {
    EnhancedDataTable,
    StatusCell,
    VisibilityCell,
    DateCell,
    VotingTimeCell,
    ModeratorCell,
    GenericDashboardHeaderView
  },
  setup() {
    const router = useRouter()
    const { t, locale } = useI18n()
    const loading = ref(false)
    const exportLoading = ref(false)
    const sidebarStore = useSidebarStore()
    const skeletonLoading = ref(true)
    const isLanguageEn = ref(false)
    const itemsPerPage = ref(10)
    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || locale.value || 'ar'

    const isRtl = ref(locale.value === 'ar')

    const totalElements = ref<number>(0)

    const challengeResponse = ref<PaginationResponse<ChallengesResponse>>()
    const currentPageable = ref<Pageable>()
    const listOfChallengeStatus = ref<string[]>([])
    const employeeJson: string | null = localStorage.getItem(Fields.EMPLOYEE)
    const currentEmployee: Employee = employeeJson ? JSON.parse(employeeJson) : new Employee()

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
      { title: t('common.id'), align: 'start', sortable: true, key: 'id', width: '5%' },
      { title: t('challenge.name'), align: 'start', sortable: true, key: 'localTitle', width: '15%' },
      { title: t('challenge.description'), align: 'start', sortable: true, key: 'localDescription', width: '15%' },
      { title: t('challenge.moderator'), align: 'start', sortable: true, key: 'moderatorGrpNumber', width: '10%' },
      { title: t('challenge.department'), align: 'start', sortable: true, key: 'departmentName', width: '10%' },
      { title: t('challenge.votingTime'), align: 'start', sortable: true, key: 'startTime', width: '15%' },
      { title: t('challenge.visibility'), align: 'start', sortable: false, key: 'visibility', width: '5%' },
      { title: t('challenge.createdAt'), align: 'start', sortable: true, key: 'createdAt', width: '15%' },
      { title: t('common.statusName'), align: 'start', sortable: true, key: 'status', width: '5%' },
      { title: t('common.actions'), align: 'end', key: 'actions' }
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

    const tableActions = computed(() => [
      {
        key: 'dashboard',
        label: t('common.dashboard'),
        icon: 'mdi-monitor-dashboard',
        color: 'primary',
        tooltip: t('common.dashboard')
      },
      {
        key: 'view',
        label: t('common.view'),
        icon: 'mdi-eye',
        color: 'success',
        tooltip: t('common.view'),
        show: currentEmployee.questionView
      }
    ])

    const columnOptions = {}

    const moderatorRowProps = (data: { item?: ChallengesResponse; internalItem?: { raw?: ChallengesResponse } }) => {
      const challenge: ChallengesResponse = data?.item ?? data?.internalItem?.raw ?? ({} as ChallengesResponse)
      const isCurrentEmployeeModeratorByGroup =
        challenge?.moderatorGrpNumber != null &&
        (challenge.moderatorGrpNumber === currentEmployee?.grpNumber ||
          challenge.moderatorGrpNumber === currentEmployee?.empGrpNumber)

      const isModeratorOrAssistant =
        challenge?.currentUserIsTheChallengeModeratorOrAssistantModerator || isCurrentEmployeeModeratorByGroup

      if (isModeratorOrAssistant) {
        return {
          class: 'moderator-highlight-row'
        }
      }

      return {}
    }

    // Methods
    const searchButton = async (pageable: Pageable) => {
      loading.value = true
      currentPageable.value = {
        page: pageable?.page ?? 0,
        itemsPerPage: pageable?.itemsPerPage ?? itemsPerPage.value,
        sortBy: pageable?.sortBy?.length ? [...pageable.sortBy] : [...defaultPageable.sortBy]
      }
      const pageRequest = {
        page: pageable?.page ?? 0,
        size: pageable?.itemsPerPage ?? itemsPerPage.value,
        sort: pageable?.sortBy.length > 0 ? `${getSortKey(pageable)},${pageable.sortBy[0].order}` : 'updatedAt,desc'
      }

      const response = (await searchRequest(
        'get',
        URIS.CHALLENGES.BASE + URIS.CHALLENGES.MY_CHALLENGES,
        null,
        { ...challengeSearchRequest.value, ...pageRequest },
        false
      )) as PaginationResponse<ChallengesResponse>

      challengeResponse.value = response
      totalElements.value = response.totalElements
      loading.value = false
      skeletonLoading.value = false
    }

    const getSortKey = (pageable: Pageable) => {
      if (pageable.sortBy.length > 0) {
        if (pageable.sortBy[0].key === 'localDescription') {
          return isRtl.value ? 'descriptionAr' : 'descriptionEn'
        }
        if (pageable.sortBy[0].key === 'localTitle') {
          return isRtl.value ? 'titleAr' : 'titleEn'
        } else {
          return pageable.sortBy[0].key
        }
      } else {
        return 'updatedAt'
      }
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

    const viewChallengeDashboard = (challengeId: number) => {
      router.push({
        name: 'ChallengeDashboard',
        params: { challengeId }
      })
    }

    // Initialize a default Pageable object
    const defaultPageable: Pageable = {
      page: 0,
      itemsPerPage: itemsPerPage.value,
      sortBy: [{ key: 'updatedAt', order: 'desc' }]
    }

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
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
      { key: 'id', header: t('common.id'), width: 10, value: (challenge: ChallengesResponse) => challenge.id },
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
        width: 20,
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
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.createdAt)
      },
      {
        key: 'lastModifiedAt',
        header: t('common.updatedAt'),
        width: 22,
        value: (challenge: ChallengesResponse) => formatDateTime(challenge.lastModifiedAt)
      }
    ]

    const buildAppliedFilterSummary = () => {
      const filters = [
        challengeSearchRequest.value.title ? `${t('challenge.name')}: ${challengeSearchRequest.value.title}` : null,
        challengeSearchRequest.value.moderator ? `${t('challenge.moderator')}: ${challengeSearchRequest.value.moderator}` : null,
        challengeSearchRequest.value.status ? `${t('common.statusName')}: ${getLocalizedStatusLabel(challengeSearchRequest.value.status)}` : null,
        challengeSearchRequest.value.createdAt ? `${t('common.createdAt')}: ${formatDate(challengeSearchRequest.value.createdAt)}` : null,
        challengeSearchRequest.value.lastModifiedAt ? `${t('common.updatedAt')}: ${formatDate(challengeSearchRequest.value.lastModifiedAt)}` : null,
        challengeSearchRequest.value.startVotingDate ? `${t('common.startTime')}: ${formatDate(challengeSearchRequest.value.startVotingDate)}` : null,
        challengeSearchRequest.value.endVotingDate ? `${t('common.endTime')}: ${formatDate(challengeSearchRequest.value.endVotingDate)}` : null
      ].filter(Boolean)

      return filters.length ? filters.join(' | ') : locale.value === 'ar' ? 'لا توجد فلاتر مطبقة' : 'No filters applied'
    }

    const buildExportMetadata = (rows: ChallengesResponse[]) => {
      const currentSort = currentPageable.value?.sortBy?.[0]
      const sortValue = currentSort ? `${getSortKey(currentPageable.value as Pageable)} (${currentSort.order})` : 'updatedAt (desc)'

      return [
        { label: locale.value === 'ar' ? 'إجمالي النتائج' : 'Total results', value: String(rows.length) },
        {
          label: locale.value === 'ar' ? 'التحديات العامة' : 'Public challenges',
          value: String(rows.filter((row) => row.visibility === ChallengeVisibility.PUBLIC).length)
        },
        {
          label: locale.value === 'ar' ? 'التحديات الخاصة' : 'Private challenges',
          value: String(rows.filter((row) => row.visibility === ChallengeVisibility.PRIVATE).length)
        },
        { label: locale.value === 'ar' ? 'ترتيب التصدير' : 'Export sorting', value: sortValue },
        { label: locale.value === 'ar' ? 'الفلاتر المطبقة' : 'Applied filters', value: buildAppliedFilterSummary() }
      ]
    }

    const buildExportLegendItems = (rows: ChallengesResponse[]): ExcelLegendItem[] =>
      Array.from(new Set(rows.map((row) => row.status).filter(Boolean))).map((status) => {
        const appearance = getChallengeStatusAppearance(status)
        const count = rows.filter((row) => row.status === status).length

        return {
          label: getLocalizedStatusLabel(status),
          description:
            locale.value === 'ar'
              ? `${count} من تحدياتي المطابقة للفلاتر الحالية`
              : `${count} matching challenges in the current filtered result`,
          fillColor: appearance.backgroundColor,
          borderColor: appearance.borderColor,
          textColor: appearance.textColor
        }
      })

    const buildExportMetricBars = (rows: ChallengesResponse[]): ExcelMetricBarItem[] => {
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

      pushMetric(
        locale.value === 'ar' ? 'مرئية للعامة' : 'Public visibility',
        rows.filter((row) => row.visibility === ChallengeVisibility.PUBLIC).length,
        '#0F6CBD',
        locale.value === 'ar'
          ? 'عدد تحدياتي المتاحة بنمط عام ضمن نتائج التصفية الحالية.'
          : 'My challenges available with public visibility in the current filtered result.'
      )

      pushMetric(
        locale.value === 'ar' ? 'مرئية بشكل خاص' : 'Private visibility',
        rows.filter((row) => row.visibility === ChallengeVisibility.PRIVATE).length,
        '#7A3EF0',
        locale.value === 'ar'
          ? 'عدد تحدياتي المصنفة كنمط خاص ضمن نتائج التصفية الحالية.'
          : 'My challenges classified with private visibility in the current filtered result.'
      )

      Array.from(new Set(rows.map((row) => row.status).filter(Boolean)))
        .slice(0, 4)
        .forEach((status) => {
          const count = rows.filter((row) => row.status === status).length
          const appearance = getChallengeStatusAppearance(status)
          pushMetric(
            getLocalizedStatusLabel(status),
            count,
            appearance.color,
            locale.value === 'ar'
              ? 'قياس توزيع الحالات ضمن التحديات المطابقة للفلاتر الحالية.'
              : 'Status distribution within the current filtered result.'
          )
        })

      return bars
    }

    const fetchAllChallengesForExport = async () => {
      const total = challengeResponse.value?.totalElements ?? totalElements.value
      if (!total) return []

      const exportPageable = currentPageable.value ?? defaultPageable
      const sort =
        exportPageable.sortBy.length > 0
          ? `${getSortKey(exportPageable)},${exportPageable.sortBy[0].order}`
          : 'updatedAt,desc'
      const batchSize = Math.min(Math.max(exportPageable.itemsPerPage, 100), 250)
      const totalPages = Math.max(Math.ceil(total / batchSize), 1)
      const collectedRows: ChallengesResponse[] = []
      const seenIds = new Set<number>()

      for (let page = 0; page < totalPages; page += 1) {
        const response = (await searchRequest(
          'get',
          URIS.CHALLENGES.BASE + URIS.CHALLENGES.MY_CHALLENGES,
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

    // Event Handlers for EnhancedDataTable
    const handleSearch = (searchValues: any) => {
      challengeSearchRequest.value = searchValues
      searchButton(defaultPageable)
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
      searchButton(defaultPageable)
    }

    const refreshData = () => {
      searchButton(defaultPageable)
    }

    const exportData = async () => {
      try {
        exportLoading.value = true
        const exportRows = await fetchAllChallengesForExport()

        if (!exportRows.length) {
          return
        }

        await exportRowsToExcel<ChallengesResponse>({
          fileName: locale.value === 'ar' ? 'تقرير-تحدياتي' : 'my-challenges-report',
          sheetName: locale.value === 'ar' ? 'تحدياتي' : 'My Challenges',
          title: locale.value === 'ar' ? 'تحدياتي' : 'My Challenges',
          headerTitle: locale.value === 'ar' ? 'تقرير تحدياتي' : 'My Challenges Report',
          headerDescription:
            locale.value === 'ar'
              ? 'تقرير تحليلي مبني على التحديات المطابقة للفلاتر الحالية الخاصة بالمستخدم'
              : 'Analytics report generated from the current filtered my challenges result',
          subtitle:
            locale.value === 'ar'
              ? 'تقرير تحليلي مبني على نتائج التصفية الحالية'
              : 'Analytics report based on the current filtered results',
          footerNote:
            locale.value === 'ar'
              ? 'تم إنشاء هذا الملف من شاشة تحدياتي مع تضمين الفلاتر والتحليلات الحالية.'
              : 'This file was generated from the My Challenges screen with the current filters and analytics.',
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
      } catch (error) {
        console.error('Export error:', error)
      } finally {
        exportLoading.value = false
      }
    }

    const handleTableAction = ({ actionKey, item }: { actionKey: string; item: any }) => {
      switch (actionKey) {
        case 'dashboard':
          viewChallengeDashboard(item.id)
          break
        case 'view':
          viewChallengeDetails(item.id)
          break
      }
    }

    const handleTableUpdate = (options: any) => {
      searchButton(options)
    }

    const onItemsPerPageUpdate = (newItemsPerPage: number) => {
      itemsPerPage.value = newItemsPerPage
      const updatedPageable = {
        ...defaultPageable,
        itemsPerPage: newItemsPerPage,
        page: 0
      }
      searchButton(updatedPageable)
    }

    // Status formatting methods (same as main challenges)
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
        component: StatusCell,
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
        component: VisibilityCell,
        options: {}
      },
      createdAt: {
        component: DateCell,
        options: {
          formatDate,
          formatTime
        }
      },
      startTime: {
        component: VotingTimeCell,
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
      sidebarStore.disable()
      loadStatusList()
      searchButton(defaultPageable)
      isLanguageEn.value = localStorage.getItem('selectedLanguage') === 'en'
    })

    return {
      headers,
      isLanguageAr,
      challengeSearchRequest,
      searchButton,
      challengeResponse,
      loading,
      exportLoading,
      itemsPerPage,
      viewChallengeDetails,
      viewChallengeDashboard,
      isHasAnyRole,
      totalElements,
      skeletonLoading,
      defaultPageable,
      listOfChallengeStatus,
      currentEmployee,
      basicSearchFields,
      advancedSearchFields,
      tableActions,
      columnTemplates,
      columnOptions,
      handleSearch,
      clearSearch,
      exportData,
      refreshData,
      handleTableAction,
      handleTableUpdate,
      onItemsPerPageUpdate,
      getStatusColor,
      getStatusIcon,
      getStatusDescription,
      formatDate,
      formatTime,
      getStatusClass,
      getStatusIconClass,
      isUrgentStatus,
      isNewStatus,
      safeChallengeItems,
      moderatorRowProps
    }
  }
})
</script>

<template src="./MyChallenges.html" v-if="isHasAnyRole"></template>
<style src="./MyChallenges.scss" lang="scss"></style>
