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
import { EmailTemplatesResponse } from '@/dto/template/EmailTemplatesResponse.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import AppRichTextEditor from '@/components/common/richText/AppRichTextEditor.vue'

export default defineComponent({
  components: {
    AppRichTextEditor
  },
  emits: ['close', 'templateUpdated'],
  setup(_, { emit }) {
    const { t } = useI18n()
    const emailTypes = ref<TemplateTypeResponse[]>()
    const formData = reactive({
      subjectAr: '',
      contentAr: '',
      subjectEn: '',
      contentEn: '',
      templateTypeId: null as number | null,
      isDefault: false
    })
    const templateType = ref<TemplateTypeResponse>()
    const emailSnippetsEn = ref<string[]>([])
    const emailSnippetsAr = ref<string[]>([])
    const valid = ref(false)
    const rules = {
      required: (value: string) => !!value || t('error.mandatory')
    }

    onMounted(() => {
      loadEmailTypes()
    })

    const loadEmailTypes = async () => {
      const response = await get<SuccessResponse<TemplateTypeResponse[]>>(
        URIS.TEMPLATE_TYPE.BASE + URIS.TEMPLATE_TYPE.ALL
      )
      emailTypes.value = (response?.data as TemplateTypeResponse[]) || []
    }

    const onTemplateTypeChange = (templateType: TemplateTypeResponse) => {
      emailSnippetsEn.value = templateType.mandatoryFieldsListEn as string[]
      emailSnippetsAr.value = templateType.mandatoryFieldsListAr as string[]
      console.log('templateType:' + emailSnippetsEn.value)
    }

    const saveTemplate = async () => {
      if (!valid.value) {
        toast.error(t('error.mandatory'))
        return
      }

      formData.templateTypeId = templateType.value?.id ?? null

      try {
        const creationResponse = (await create<SuccessResponse<EmailTemplatesResponse>>(
          URIS.EMAIL_TEMPLATE.BASE,
          formData
        )) as SuccessResponse<EmailTemplatesResponse>
        if (creationResponse.success) {
          toast.success(t('Messages.success.emailTemplate.created'))
          emit('templateUpdated')
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
      formData.subjectAr = ''
      formData.contentAr = ''
      formData.subjectEn = ''
      formData.contentEn = ''
      formData.templateTypeId = null
      formData.isDefault = false
      emit('close')
    }

    return {
      formData,
      emailTypes,
      emailSnippetsEn,
      emailSnippetsAr,
      templateType,
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

<template src="./CreateNewEmailTemplate.html" v-if="isAdminOrModerator"></template>
<style src="./CreateNewEmailTemplate.scss" lang="scss" scoped></style>
