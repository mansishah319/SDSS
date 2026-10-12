<!--
  - * ***************************************************************
  - * Copyright ©1956-2026 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { computed, defineComponent, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { create, get, update } from '@/utils/GenericApiHelper.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { PaginationResponse } from '@/dto/generic/PaginationResponse.ts'
import { DepartmentResponse } from '@/dto/department/DepartmentResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { SdssSurveyCreationRequest } from '@/dto/surveys/survey/SdssSurveyCreationRequest.ts'
import { SdssSurveyByIdResponse } from '@/dto/surveys/survey/SdssSurveyByIdResponse'
import { useSidebarStore } from '@/store/sidebar.ts'
import ContentLoader from '@/components/loader/ContentLoader.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'

interface EmployeeAudienceRow {
  id: number
  employeeNumber?: string
  fullName?: string
  departmentName?: string
  jobName?: string
  emailAddress?: string
}

export default defineComponent({
  name: 'SurveyFormPage',
  components: {
    ContentLoader,
    GenericDashboardHeaderView
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const surveyId = computed(() => Number(route.params.surveyId || 0))
    const isEditMode = computed(() => surveyId.value > 0)
    const isPageLoading = ref(false)
    const isEmployeesLoading = ref(false)
    const isJobsLoading = ref(false)
    const jobs = ref<Array<{ id: number; name: string }>>([])
    const allDepartmentList = ref<DepartmentResponse[]>([])
    const employeeRows = ref<EmployeeAudienceRow[]>([])
    const selectedEmployeeIds = ref<number[]>([])
    const departmentIds = ref<number[]>([])
    const jobIds = ref<number[]>([])
    const search = ref('')
    const employeeSearch = ref('')
    const employeePage = ref(1)
    const employeeItemsPerPage = ref(10)
    const totalEmployees = ref(0)
    const isAudienceWatchPaused = ref(false)

    const creationRequest = ref<SdssSurveyCreationRequest>({
      titleEn: '',
      titleAr: '',
      allowedGrpList: [],
      descriptionEn: '',
      descriptionAr: '',
      numberOfRounds: 1,
      manualHandle: true,
      roundRequestList: []
    })

    const employeeHeaders = computed(() => [
      { title: t('common.grpNumber'), key: 'id', value: 'id', sortable: true },
      { title: t('common.fullName'), key: 'fullName', value: 'fullName', sortable: true },
      { title: t('common.department'), key: 'departmentName', value: 'departmentName', sortable: true },
      { title: t('sdssSurvey.selectJobs'), key: 'jobName', value: 'jobName', sortable: true },
      { title: 'Email', key: 'emailAddress', value: 'emailAddress', sortable: true }
    ])

    const pageTitle = computed(() => (isEditMode.value ? t('sdssSurvey.edit') : t('sdssSurvey.add')))
    const selectedEmployeesCount = computed(() => selectedEmployeeIds.value.length)

    const loadDepartmentList = async () => {
      const res = await get<SuccessResponse<any>>(URIS.CORE.BASE + URIS.CORE.DEPARTMENT_ALL_WITHOUT_RESTRICTIONS)
      if (res?.success) {
        allDepartmentList.value = res.data as DepartmentResponse[]
      }
    }

    const buildRepeatedParamsEndpoint = (
      endpoint: string,
      params: Record<string, Array<number | string> | number | string | undefined>
    ) => {
      const searchParams = new URLSearchParams()

      Object.entries(params).forEach(([key, value]) => {
        if (Array.isArray(value)) {
          value.forEach((item) => searchParams.append(key, String(item)))
          return
        }

        if (value !== undefined && value !== '') {
          searchParams.append(key, String(value))
        }
      })

      return `${endpoint}?${searchParams.toString()}`
    }

    const loadJobsByDepartments = async () => {
      if (!departmentIds.value.length) {
        jobs.value = []
        jobIds.value = []
        return
      }

      isJobsLoading.value = true
      try {
        const res = await get<Array<{ jobId: number; jobName: string }>>(
          buildRepeatedParamsEndpoint(`${URIS.CORE.BASE}${URIS.CORE.EMPLOYEE_JOBS_BY_DEPARTMENTS}`, {
            departmentIds: departmentIds.value
          })
        )

        jobs.value = (res || []).map((job) => ({
          id: Number(job.jobId),
          name: job.jobName
        }))

        const availableJobIds = new Set(jobs.value.map((job) => job.id))
        jobIds.value = jobIds.value.filter((id) => availableJobIds.has(id))
      } catch (error) {
        console.error('Error loading jobs:', error)
      } finally {
        isJobsLoading.value = false
      }
    }

    const buildAudienceEndpoint = (pageNumber: number, size: number, searchValue = '') => {
      return buildRepeatedParamsEndpoint(`${URIS.CORE.BASE}${URIS.CORE.EMPLOYEE_RETRIEVE_BY_JOBS_AND_DEPARTMENTS}`, {
        departmentIds: departmentIds.value,
        jobIds: jobIds.value,
        search: searchValue.trim(),
        page: pageNumber,
        size
      })
    }

    const loadEmployeesByFilters = async () => {
      if (!departmentIds.value.length) {
        employeeRows.value = []
        totalEmployees.value = 0
        return
      }

      isEmployeesLoading.value = true
      try {
        const res = await get<PaginationResponse<EmployeeAudienceRow>>(
          buildAudienceEndpoint(employeePage.value - 1, employeeItemsPerPage.value, employeeSearch.value || '')
        )
        employeeRows.value = (res?.data || []).map((employee) => ({
          ...employee,
          id: Number(employee.id)
        }))
        totalEmployees.value = res?.totalElements || 0
      } catch (error) {
        console.error('Error loading employee audience:', error)
      } finally {
        isEmployeesLoading.value = false
      }
    }

    const selectAllEmployeesByCurrentFilters = async () => {
      if (!departmentIds.value.length) {
        selectedEmployeeIds.value = []
        return
      }

      isEmployeesLoading.value = true
      try {
        const res = await get<PaginationResponse<EmployeeAudienceRow>>(buildAudienceEndpoint(0, 100000))
        selectedEmployeeIds.value = (res?.data || []).map((employee) => Number(employee.id))
      } catch (error) {
        console.error('Error selecting all employee audience:', error)
      } finally {
        isEmployeesLoading.value = false
      }
    }

    const loadSurveyData = async () => {
      if (!isEditMode.value) {
        return
      }

      const res = await get<SuccessResponse<SdssSurveyByIdResponse>>(
        URIS.SURVEYS.SURVEY.BASE + '?surveyId=' + surveyId.value
      )
      if (res?.success) {
        const responseData = res.data as SdssSurveyByIdResponse
        isAudienceWatchPaused.value = true
        departmentIds.value = responseData.departmentList?.map((department: string) => Number(department)) || []
        jobIds.value = responseData.jobList?.map((job: string) => Number(job)) || []
        selectedEmployeeIds.value = responseData.allowedGrpList || []
        isAudienceWatchPaused.value = false
        creationRequest.value = {
          titleEn: responseData.titleEn || '',
          titleAr: responseData.titleAr || '',
          allowedGrpList: responseData.allowedGrpList || [],
          descriptionEn: responseData.descriptionEn || '',
          descriptionAr: responseData.descriptionAr || '',
          numberOfRounds: responseData.rounds ? responseData.rounds.length : 0,
          manualHandle: responseData.rounds?.some((round) => round.manualHandle) || false,
          roundRequestList:
            responseData.rounds?.map((round) => ({
              titleEn: round.titleEn,
              titleAr: round.titleAr,
              descriptionEn: round.descriptionEn || '',
              descriptionAr: round.descriptionAr || '',
              startTime: round.startTime || '',
              endTime: round.endTime || '',
              roundNumber: round.roundNumber,
              manualHandle: round.manualHandle || false
            })) || []
        }

        await loadJobsByDepartments()
        await loadEmployeesByFilters()
      }
    }

    const onDropdownOpen = async (isOpen: boolean) => {
      if (isOpen && jobs.value.length === 0) {
        await loadJobsByDepartments()
      }
    }

    const generateRounds = () => {
      if (!validateNumberOfRounds()) {
        return
      }

      const currentRounds = creationRequest.value.roundRequestList?.length || 0
      const newRounds = creationRequest.value.numberOfRounds || 0

      if (newRounds > currentRounds) {
        for (let i = currentRounds; i < newRounds; i++) {
          creationRequest.value.roundRequestList?.push({
            titleEn: '',
            titleAr: '',
            descriptionEn: '',
            descriptionAr: '',
            startTime: '',
            endTime: '',
            roundNumber: i + 1
          })
        }
      } else if (newRounds < currentRounds) {
        creationRequest.value.roundRequestList?.splice(newRounds)
      }
    }

    const validateNumberOfRounds = (): boolean => {
      if (!creationRequest.value.numberOfRounds || creationRequest.value.numberOfRounds <= 0) {
        toast.error(t('error.roundsOfSurveyZero'))
        return false
      }
      return true
    }

    const validateAudience = (): boolean => {
      if (!selectedEmployeeIds.value.length) {
        toast.error(t('sdssSurvey.invitationsWarning'))
        return false
      }
      return true
    }

    const save = async () => {
      if (!validateNumberOfRounds() || !validateAudience()) {
        return
      }

      const payload: SdssSurveyCreationRequest = {
        ...creationRequest.value,
        allowedGrpList: selectedEmployeeIds.value
      }

      const endpoint = isEditMode.value
        ? URIS.SURVEYS.SURVEY.BASE + Characters.QUESTION_MARK + Fields.SURVEY_ID + Characters.EQUAL + surveyId.value
        : URIS.SURVEYS.SURVEY.BASE
      const request = isEditMode.value ? update : create
      const res = await request(endpoint, payload)

      if ((res as any)?.success) {
        toast.success(isEditMode.value ? t('Messages.success.sdssSurvey.updated') : t('Messages.success.sdssSurvey.created'))
        await router.push({ name: 'SdssSurvey' })
      } else {
        toast.error((res as any)?.message)
      }
    }

    const cancel = async () => {
      await router.push({ name: 'SdssSurvey' })
    }

    watch(employeeSearch, async () => {
      employeePage.value = 1
      await loadEmployeesByFilters()
    })

    watch(
      () => creationRequest.value.numberOfRounds,
      () => {
        generateRounds()
      }
    )

    watch(
      () => creationRequest.value.manualHandle,
      () => {
        generateRounds()
      }
    )

    watch(departmentIds, async () => {
      if (isAudienceWatchPaused.value) {
        return
      }

      employeePage.value = 1
      await loadJobsByDepartments()
      await selectAllEmployeesByCurrentFilters()
      await loadEmployeesByFilters()
    })

    watch(jobIds, async () => {
      if (isAudienceWatchPaused.value) {
        return
      }

      employeePage.value = 1
      await selectAllEmployeesByCurrentFilters()
      await loadEmployeesByFilters()
    })

    watch([employeePage, employeeItemsPerPage], async () => {
      await loadEmployeesByFilters()
    })

    onMounted(async () => {
      sidebarStore.show()
      isPageLoading.value = true
      try {
        await loadDepartmentList()
        await loadSurveyData()
        if (!isEditMode.value) {
          generateRounds()
        }
      } finally {
        isPageLoading.value = false
      }
    })

    return {
      cancel,
      creationRequest,
      departmentIds,
      employeeHeaders,
      employeeRows,
      employeeSearch,
      employeePage,
      employeeItemsPerPage,
      isEditMode,
      isEmployeesLoading,
      isJobsLoading,
      isPageLoading,
      jobs,
      jobIds,
      allDepartmentList,
      onDropdownOpen,
      pageTitle,
      save,
      search,
      selectedEmployeeIds,
      selectedEmployeesCount,
      totalEmployees,
      generateRounds
    }
  }
})
</script>

<template src="./SurveyFormPage.html"></template>
<style src="./SurveyFormPage.scss" lang="scss" scoped></style>
