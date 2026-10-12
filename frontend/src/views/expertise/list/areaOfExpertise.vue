<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue'
import { deleteRequest, get } from '@/utils/GenericApiHelper.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { ExpertiseResponse } from '@/dto/expertise/ExpertiseResponse.ts'
import { PaginationResponse } from '@/dto/generic/PaginationResponse.ts'
import CreateNewExpertise from '@/views/expertise/creation/CreateNewExpertise.vue'
import EditExpertise from '@/views/expertise/edit/EditExpertise.vue'
import { useI18n } from 'vue-i18n'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { toast } from 'vue3-toastify'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'AreaOfExpertise',
  components: {
    AppDialogShell,
    EmptyDataTable,
    ConfirmationDialog,
    CreateNewExpertise,
    EditExpertise,
    GenericDashboardHeaderView
  },
  setup() {
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const isLoading = ref(false)
    const idToDelete = ref(0)
    const deleteDialog = ref(false)
    const showEditDialog = ref(false)
    const selectedExpertiseId = ref(0)
    const expertiseResponse = ref<ExpertiseResponse[]>([])
    const showCreateNewPage = ref(false)
    const search = ref('')
    const headers = [
      {
        title: t('settings.expertise.name'),
        align: 'start',
        sortable: true,
        value: 'localName'
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
        sortable: false
      }
    ]

    const openEditDialog = (expertiseId: number) => {
      selectedExpertiseId.value = expertiseId
      showEditDialog.value = true
    }

    const loadExpertiseData = async () => {
      try {
        isLoading.value = true
        const response = await get<PaginationResponse<ExpertiseResponse>>(URIS.EXPERTISE.BASE + URIS.EXPERTISE.SEARCH)
        if (response?.success) {
          expertiseResponse.value = response.data.map((item: any) => ({
            ...item,
            createdAt: formatDate(item.createdAt),
            updatedAt: formatDate(item.updatedAt)
          }))
        } else {
          console.error('API request was not successful:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('Error loading challenge dashboard data:', error)
        // Handle error scenario
      } finally {
        isLoading.value = false
      }
    }

    const createNewExpertise = () => {
      showCreateNewPage.value = true
    }

    onMounted(async () => {
      sidebarStore.show()
      await loadExpertiseData()
    })

    const openDeleteDialog = (expertiseId: number) => {
      idToDelete.value = expertiseId
      deleteDialog.value = true
    }

    const cancelDeletion = () => {
      deleteDialog.value = false
    }

    const confirmDeletion = async () => {
      console.log('expertise deletion confirmed')
      console.log(idToDelete.value)

      await deleteRequest(
        URIS.EXPERTISE.BASE + Characters.QUESTION_MARK + Fields.EXPERTISE_ID + Characters.EQUAL + idToDelete.value
      )

      deleteDialog.value = false
      console.log('expertise deleted successfully.')
      toast.success(t('Messages.success.expertise.deleted'))
      await loadExpertiseData()
    }

    const refreshTemplateList = async () => {
      await loadExpertiseData()
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

    return {
      search,
      expertiseResponse,
      headers,
      showCreateNewPage,
      createNewExpertise,
      openEditDialog,
      showEditDialog,
      selectedExpertiseId,
      deleteDialog,
      openDeleteDialog,
      cancelDeletion,
      confirmDeletion,
      isLoading,
      isAdminOrModerator,
      refreshTemplateList
    }
  }
})
</script>

<template src="./areaOfExpertise.html" v-if="isAdminOrModerator"></template>
<style src="./areaOfExpertise.scss" lang="scss" scoped></style>
