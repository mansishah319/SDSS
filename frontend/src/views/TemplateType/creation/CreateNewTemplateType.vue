<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted, reactive, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { create, get } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { URIS } from '@/constants/DpConstants.ts'
import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import { TemplateKeysResponse } from '@/dto/template/TemplateKeysResponse.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  emits: ['close', 'templateUpdated'],
  setup(_, { emit }) {
    const sidebarStore = useSidebarStore()
    const { t } = useI18n()
    const templateTypeKeyList = ref<TemplateKeysResponse[]>()
    const formData = reactive({
      titleEn: '',
      titleAr: '',
      templateTypeKey: '',
      mandatoryFieldsEn: [],
      mandatoryFieldsAr: []
    })
    const emailSnippetsEn = ref<string[]>([])
    const emailSnippetsAr = ref<string[]>([])
    const valid = ref(false)
    const rules = {
      required: (value: string) => !!value || 'Required.'
    }

    onMounted(() => {
      sidebarStore.show()
      loadTemplateTypeKeys()
    })

    const loadTemplateTypeKeys = async () => {
      const response = await get<SuccessResponse<any>>(URIS.TEMPLATE_TYPE.BASE + URIS.TEMPLATE_TYPE.KEYS)
      templateTypeKeyList.value = response?.data as TemplateKeysResponse[]
    }

    const onTemplateTypeChange = (templateType: string) => {
      console.log('templateType:' + templateType)
    }

    const saveTemplate = async () => {
      if (!valid.value) {
        toast.error(t('error.mandatory'))
        return
      }

      console.log(formData)

      try {
        const creationResponse = (await create<SuccessResponse<any>>(
          URIS.TEMPLATE_TYPE.BASE,
          formData
        )) as SuccessResponse<TemplateTypeResponse>
        if (creationResponse.success) {
          emit('templateUpdated')
          toast.success(t('Messages.success.emailTemplate.created'))
          cancel()
        } else {
          console.log('There is an issue with Email Template Creation')
        }
      } catch (error: any) {
        // Assuming the error response has a message property
        console.log(error)
      }
    }

    const cancel = () => {
      formData.titleEn = ''
      formData.titleAr = ''
      formData.templateTypeKey = ''
      formData.mandatoryFieldsAr = []
      formData.mandatoryFieldsEn = []
      emit('close')
    }

    return {
      formData,
      templateTypeKeyList,
      emailSnippetsEn,
      emailSnippetsAr,
      valid,
      onTemplateTypeChange,
      rules,
      saveTemplate,
      cancel,
      isAdminOrModerator
    }
  }
})
</script>

<template src="./CreateNewTemplateType.html" v-if="isAdminOrModerator"></template>
<style src="./CreateNewTemplateType.scss" lang="scss" scoped></style>
