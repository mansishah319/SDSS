<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue'
import { deleteRequest, get } from '@/utils/GenericApiHelper'
import { Characters, Fields, URIS } from '@/constants/DpConstants'
import { PaginationResponse } from '@/dto/generic/PaginationResponse'
import { useI18n } from 'vue-i18n'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { toast } from 'vue3-toastify'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { isAdminOrModerator } from '@/utils/roleUtils'
import { SdssSurveyByIdResponse } from '@/dto/surveys/survey/SdssSurveyByIdResponse'
import { useRouter } from 'vue-router'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'SdssSurvey',
  components: {
    EmptyDataTable,
    ConfirmationDialog,
    GenericDashboardHeaderView
  },
  setup() {
    const router = useRouter()
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const isLoading = ref(false)
    const idToDelete = ref(0)
    const deleteDialog = ref(false)
    const dtoResponse = ref<SdssSurveyByIdResponse[]>([] as SdssSurveyByIdResponse[])
    const search = ref('')
    const headers = [
      {
        title: t('common.id'),
        align: 'start',
        sortable: true,
        value: 'id'
      },
      {
        title: t('sdssSurvey.name'),
        align: 'start',
        sortable: true,
        value: 'localTitle'
      },
      {
        title: t('sdssSurvey.description'),
        align: 'start',
        sortable: true,
        value: 'localDescription'
      },
      {
        title: t('common.statusName'),
        align: 'start',
        sortable: true,
        value: 'localStatus'
      },
      {
        title: t('sdssSurvey.mode'),
        align: 'start',
        sortable: true,
        value: 'mode'
      },
      {
        title: t('common.createdBy'),
        value: 'createdBy',
        sortable: true
      },
      {
        title: t('common.createdAt'),
        value: 'createdAt',
        sortable: true
      },
      {
        title: t('common.updatedBy'),
        value: 'updatedBy',
        sortable: true
      },
      {
        title: t('common.updatedAt'),
        value: 'updatedAt',
        sortable: true
      },
      {
        title: t('common.actions'),
        key: 'actions',
        value: 'actions',
        align: 'center',
        sortable: false
      }
    ]

    const openEditDialog = (surveyIdKey: number) => {
      router.push({
        name: 'SdssSurveyUpdate',
        params: { surveyId: surveyIdKey }
      })
    }

    const loadData = async () => {
      try {
        isLoading.value = true
        await get<PaginationResponse<SdssSurveyByIdResponse>>(
          URIS.SURVEYS.SURVEY.BASE + URIS.SURVEYS.SURVEY.SEARCH
        ).then((res: PaginationResponse<SdssSurveyByIdResponse>) => {
          if (res?.success) {
            dtoResponse.value = res.data.map((item: any) => ({
              ...item,
              createdAt: formatDate(item.createdAt),
              updatedAt: formatDate(item.updatedAt)
            })) as SdssSurveyByIdResponse[]
          } else {
            console.error('API request was not successful:', res?.message)
            // Handle error scenario
          }
        })
      } catch (error) {
        console.error('Error loading data:', error)
        // Handle error scenario
      } finally {
        isLoading.value = false
      }
    }

    const createNew = () => {
      router.push({ name: 'SdssSurveyCreate' })
    }

    onMounted(async () => {
      sidebarStore.show()
      await loadData()
    })

    const cancelDeletion = () => {
      deleteDialog.value = false
    }

    const confirmDeletion = async () => {
      await deleteRequest(
        URIS.SURVEYS.SURVEY.BASE + Characters.QUESTION_MARK + Fields.SURVEY_ID + Characters.EQUAL + idToDelete.value
      ).then(async (res: SuccessResponse<string>) => {
        if (res.success) {
          deleteDialog.value = false
          console.log('deleted successfully.')
          toast.success(t('Messages.success.sdssSurvey.deleted'))
          await loadData()
        } else {
          toast.error(res.message)
        }
      })
    }

    const formatDate = (dateString: string): string => {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }

    const handleRowClick = (event: any, row: any) => {
      console.log(event)
      viewSurveyDetails(row.item.id)
    }

    const viewSurveyDetails = (surveyId: number) => {
      router.push({
        name: 'SdssSurveyView',
        params: { surveyId }
      })
    }

    const openDeleteDialog = (id: number) => {
      idToDelete.value = id
      deleteDialog.value = true
    }

    const isModeratorAndAllowedToUpdateOrDeleteThisSurvey = (item: SdssSurveyByIdResponse) => {
      return item.currentUserIsTheSurveyModerator && item.isAllowedToUpdateOrDelete
    }
    const checkAvailabilityToEditOrDelete = async (surveyId: number): Promise<boolean> => {
      if (idToDelete.value === surveyId) return true
      const res = (await get<SuccessResponse<boolean>>(
        URIS.SURVEYS.SURVEY.BASE + URIS.SURVEYS.SURVEY.IS_USER_ALLOWED + '?surveyId=' + surveyId
      )) as SuccessResponse<boolean>

      // Return true if the request is successful and contains data
      return res.success && res.data
    }

    return {
      search,
      dtoResponse,
      headers,
      createNew,
      openEditDialog,
      deleteDialog,
      openDeleteDialog,
      cancelDeletion,
      confirmDeletion,
      isLoading,
      isAdminOrModerator,
      handleRowClick,
      viewSurveyDetails,
      checkAvailabilityToEditOrDelete,
      isModeratorAndAllowedToUpdateOrDeleteThisSurvey
    }
  }
})
</script>

<template src="./SdssSurvey.html" v-if="isAdminOrModerator"></template>
<style src="./SdssSurvey.scss" lang="scss"></style>
