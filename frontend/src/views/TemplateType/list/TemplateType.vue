<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'
import { templateTypeHeaders } from '@/views/TemplateType/list/constants/TemplateTypeHeaders.ts'
import { loadTemplatesTypes } from '@/services/template/TemplateTypeService.ts'
import GenericTable from '@/components/tables/GenericTable.vue'
import GenericDashboardHeaderView from '@/components/generic/header/GenericDashboardHeaderView.vue'
import CreateNewTemplateType from '@/views/TemplateType/creation/CreateNewTemplateType.vue'
import { useI18n } from 'vue-i18n'
import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'
import UpdateTemplateType from '@/views/TemplateType/update/UpdateTemplateType.vue'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  name: 'TemplateTypeList',
  components: {
    AppDialogShell,
    UpdateTemplateType,
    GenericTable,
    GenericDashboardHeaderView,
    EmptyDataTable,
    CreateNewTemplateType
  },
  setup() {
    const { t } = useI18n()
    const isLoading = ref(false)
    const sidebarStore = useSidebarStore()
    const dtoListResponse = ref<TemplateTypeResponse[]>([])

    const search = ref('')

    // creation
    const showCreateNewPage = ref(false)
    const createNew = () => {
      showCreateNewPage.value = true
    }

    // update
    const showUpdateDialog = ref(false)

    const selectedId = ref<number>(0)

    const refreshTemplateList = async () => {
      await loadData()
    }

    const openEditItemDialog = (selectId: number) => {
      selectedId.value = selectId
      showUpdateDialog.value = true
    }

    const handleOutsideClick = () => {
      showCreateNewPage.value = false
      showUpdateDialog.value = false
    }

    const loadData = async () => {
      const { dtoResponse: response, isLoading: loading } = await loadTemplatesTypes()
      dtoListResponse.value = response.value as TemplateTypeResponse[]
      isLoading.value = loading.value
    }

    // Use imported headers
    const headers = templateTypeHeaders.map((header: any) => ({
      ...header,
      title: t(header.title) // Apply i18n translation
    }))

    onMounted(async () => {
      sidebarStore.show()
      await loadData()
    })

    return {
      search,
      headers,
      isLoading,
      createNew,
      selectedId,
      dtoListResponse,
      showUpdateDialog,
      showCreateNewPage,
      handleOutsideClick,
      openEditItemDialog,
      refreshTemplateList
    }
  }
})
</script>

<template src="./TemplateType.html"></template>
<style src="./TemplateType.scss" lang="scss" scoped></style>
