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
import { StrategicDirectionResponse } from '@/dto/strategicDirection/StrategicDirectionResponse.ts'
import { PaginationResponse } from '@/dto/generic/PaginationResponse.ts'
import CreateChallengeSettingList from '@/views/challengeSettingList/creation/CreateChallengeSettingList.vue'
import EditChallengeSettingList from '@/views/challengeSettingList/edit/EditChallengeSettingList.vue'
import { useI18n } from 'vue-i18n'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { toast } from 'vue3-toastify'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'ChallengeSettingScreen',
  components: {
    AppDialogShell,
    EmptyDataTable,
    ConfirmationDialog,
    CreateChallengeSettingList,
    EditChallengeSettingList,
    GenericDashboardHeaderView
  },
  setup() {
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const isLoading = ref(false)
    const idToDelete = ref(0)
    const deleteDialog = ref(false)
    const showEditDialog = ref(false)
    const selectedId = ref(0)
    const dtoResponse = ref<StrategicDirectionResponse[]>([])
    const showCreateNewPage = ref(false)
    const search = ref('')
    const headers = [
      {
        title: t('settings.challengeSettings.challengeSettingsType'),
        align: 'start',
        sortable: true,
        value: 'localType'
      },
      {
        title: t('settings.challengeSettings.name'),
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

    const openEditDialog = (strategicDirectionId: number) => {
      selectedId.value = strategicDirectionId
      showEditDialog.value = true
    }

    const loadData = async () => {
      try {
        isLoading.value = true
        const response = await get<PaginationResponse<StrategicDirectionResponse>>(
          URIS.CHALLENGE_SETTINGS.BASE + URIS.CHALLENGE_SETTINGS.ALL
        )
        if (response?.success) {
          dtoResponse.value = response.data.map((item: any) => ({
            ...item,
            createdAt: formatDate(item.createdAt),
            updatedAt: formatDate(item.updatedAt)
          }))
        } else {
          console.error('API request was not successful:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('Error loading data:', error)
        // Handle error scenario
      } finally {
        isLoading.value = false
      }
    }

    const createNew = () => {
      showCreateNewPage.value = true
    }

    onMounted(async () => {
      sidebarStore.show()
      await loadData()
    })

    const openDeleteDialog = (strategicDirectionId: number) => {
      idToDelete.value = strategicDirectionId
      deleteDialog.value = true
    }

    const cancelDeletion = () => {
      deleteDialog.value = false
    }

    const confirmDeletion = async () => {
      console.log('deletion confirmed')
      console.log(idToDelete.value)

      await deleteRequest(
        URIS.CHALLENGE_SETTINGS.BASE +
        Characters.QUESTION_MARK +
        Fields.CHALLENGE_SETTINGS_ID +
        Characters.EQUAL +
        idToDelete.value
      )

      deleteDialog.value = false
      console.log('deleted successfully.')
      toast.success(t('Messages.success.strategicDirection.deleted'))
      await loadData()
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

    const refreshTemplateList = async () => {
      await loadData()
    }

    return {
      search,
      dtoResponse,
      headers,
      showCreateNewPage,
      createNew,
      openEditDialog,
      showEditDialog,
      selectedId,
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

<template src="./ChallengeSettingList.html" v-if="isAdminOrModerator()"></template>
<style src="./ChallengeSettingList.scss" lang="scss" scoped></style>
