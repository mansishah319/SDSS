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
import { Characters, URIS } from '@/constants/DpConstants.ts'
import CreateNewEmailTemplate from '@/views/emailTemplate/creation/CreateNewEmailTemplate.vue'
import UpdateEmailTemplate from '@/views/emailTemplate/update/UpdateEmailTemplate.vue'
import { useI18n } from 'vue-i18n'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import { toast } from 'vue3-toastify'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { EmailTemplatesResponse } from '@/dto/template/EmailTemplatesResponse.ts'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'EmailTemplateList',
  components: {
    AppDialogShell,
    EmptyDataTable,
    ConfirmationDialog,
    CreateNewEmailTemplate,
    UpdateEmailTemplate,
    GenericDashboardHeaderView
  },
  setup() {
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const isLoading = ref(false)
    const idToDelete = ref(0)
    const deleteDialog = ref(false)
    const showEditDialog = ref(false)
    const selectedId = ref<number>()
    const dtoResponse = ref<EmailTemplatesResponse[]>([])
    const showCreateNewPage = ref(false)
    const search = ref('')
    const headers = [
      {
        title: t('settings.emailTemplate.localSubject'),
        align: 'start',
        sortable: true,
        value: 'localSubject'
      },
      {
        title: t('settings.emailTemplate.templateTypeTitle'),
        value: 'templateType.localTitle',
        sortable: true
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

    const openEditDialog = (id: number) => {
      selectedId.value = id
      console.log(selectedId.value)
      showEditDialog.value = true
    }

    const loadData = async () => {
      try {
        isLoading.value = true
        const response = await get<SuccessResponse<any>>(URIS.EMAIL_TEMPLATE.BASE + URIS.EMAIL_TEMPLATE.ALL)
        if (response?.success) {
          dtoResponse.value = response.data as EmailTemplatesResponse[]
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

    const openDeleteDialog = (id: number) => {
      idToDelete.value = id
      deleteDialog.value = true
    }

    const cancelDeletion = () => {
      deleteDialog.value = false
    }

    const confirmDeletion = async () => {
      console.log('deletion confirmed')

      await deleteRequest(
        URIS.EMAIL_TEMPLATE.BASE + Characters.QUESTION_MARK + 'templateId' + Characters.EQUAL + idToDelete.value
      ).then(async (response: SuccessResponse) => {
        console.log(response)
        if (response.success) {
          deleteDialog.value = false
          toast.success(t('Messages.success.emailTemplate.deleted'))
          await loadData()
        } else {
          toast.error(response.message)
        }
      })
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

<template src="./EmailTemplateList.html" v-if="isAdminOrModerator"></template>
<style src="./EmailTemplateList.scss" lang="scss" scoped></style>
