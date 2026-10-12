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
import { get, update } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import AppRichTextEditor from '@/components/common/richText/AppRichTextEditor.vue'

export default defineComponent({
  components: {
    AppRichTextEditor
  },
  props: {
    gId: {
      type: String,
      required: true
    }
  },
  emits: ['close', 'templateUpdated'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const emailTypes = ref<TemplateTypeResponse[]>()
    const formData = reactive({
      subjectAr: '',
      contentAr: '',
      subjectEn: '',
      contentEn: '',
      templateTypeId: 0,
      isDefault: false
    })
    const templateType = ref<TemplateTypeResponse>()
    const emailSnippetsEn = ref<string[]>([])
    const emailSnippetsAr = ref<string[]>([])
    const valid = ref(false)
    const selectedId = ref<number>(Number(props.gId))
    const rules = {
      required: (value: string) => !!value || 'Required.'
    }

    onMounted(async () => {
      await loadRequiredData()
    })

    const loadRequiredData = async () => {
      const templateTypeResponse = await get<SuccessResponse<any>>(URIS.TEMPLATE_TYPE.BASE + URIS.TEMPLATE_TYPE.ALL)
      emailTypes.value = templateTypeResponse?.data as TemplateTypeResponse[]

      const templateResponse = await get<SuccessResponse<any>>(
        URIS.EMAIL_TEMPLATE.BASE + URIS.EMAIL_TEMPLATE.FIND_BY_ID + props.gId
      )
      const templateResponseData = templateResponse?.data
      formData.contentAr = templateResponseData?.contentAr
      formData.contentEn = templateResponseData?.contentEn
      formData.subjectAr = templateResponseData?.subjectAr
      formData.subjectEn = templateResponseData?.subjectEn
      formData.isDefault = templateResponseData?.isDefault
      templateType.value = templateResponseData?.templateType
      emailSnippetsEn.value = templateResponseData?.templateType.mandatoryFieldsListEn as string[]
      emailSnippetsAr.value = templateResponseData?.templateType.mandatoryFieldsListAr as string[]
    }

    const saveTemplate = async () => {
      if (!valid.value) {
        toast.error(t('error.mandatory'))
        return
      }

      formData.templateTypeId = templateType.value?.id ?? 0

      try {
        const creationResponse = await update<SuccessResponse<any>>(
          URIS.EMAIL_TEMPLATE.BASE + Characters.QUESTION_MARK + Fields.TEMPLATE_ID + Characters.EQUAL + props.gId,
          formData
        )
        if (creationResponse?.success) {
          toast.success(t('Messages.success.emailTemplate.updated'))
          emit('templateUpdated')
          cancel()
        } else {
          console.log('There is an issue with Update Email Template')
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
      formData.templateTypeId = 0
      formData.isDefault = false
      emit('close')
    }

    const onTemplateTypeChange = (templateType: TemplateTypeResponse) => {
      emailSnippetsEn.value = templateType.mandatoryFieldsListEn as string[]
      emailSnippetsAr.value = templateType.mandatoryFieldsListAr as string[]
      console.log('templateType:' + emailSnippetsEn.value)
    }

    return {
      formData,
      emailTypes,
      emailSnippetsEn,
      emailSnippetsAr,
      valid,
      templateType,
      onTemplateTypeChange,
      rules,
      saveTemplate,
      cancel,
      selectedId,
      isAdminOrModerator
    }
  }
})
</script>

<template src="./UpdateEmailTemplate.html" v-if="isAdminOrModerator"></template>
<style src="./UpdateEmailTemplate.scss" lang="scss" scoped></style>
