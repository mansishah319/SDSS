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
import { Characters, URIS } from '@/constants/DpConstants.ts'
import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import { TemplateKeysResponse } from '@/dto/template/TemplateKeysResponse.ts'
import { useSidebarStore } from '@/store/sidebar.ts'

export default defineComponent({
  props: {
    gId: {
      type: String,
      required: true
    }
  },
  emits: ['close', 'templateUpdated'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const sidebarStore = useSidebarStore()
    const templateTypeKeyList = ref<TemplateKeysResponse[]>()

    const formData = reactive({
      titleEn: '',
      titleAr: '',
      templateTypeKey: '',
      mandatoryFieldsEn: [],
      mandatoryFieldsAr: []
    })
    const valid = ref(false)
    const selectedId = ref<number>(Number(props.gId))
    const rules = {
      required: (value: string) => !!value || 'Required.'
    }

    onMounted(async () => {
      sidebarStore.show()
      await loadTemplateTypeKeys()
      await loadTemplateDataToUpdate()
    })

    const loadTemplateTypeKeys = async () => {
      const response = await get<SuccessResponse<any>>(URIS.TEMPLATE_TYPE.BASE + URIS.TEMPLATE_TYPE.KEYS)
      templateTypeKeyList.value = response?.data as TemplateKeysResponse[]
    }

    const loadTemplateDataToUpdate = async () => {
      const response = await get<SuccessResponse<any>>(URIS.TEMPLATE_TYPE.BASE + '?templateTypeId=' + selectedId.value)
      const templateTypeResponse = response?.data as TemplateTypeResponse
      formData.titleEn = templateTypeResponse.titleEn
      formData.titleAr = templateTypeResponse.titleAr
      formData.templateTypeKey = templateTypeResponse.templateTypeKey
    }

    const saveTemplate = async () => {
      if (!valid.value) {
        toast.error(t('error.mandatory'))
        return
      }

      try {
        const creationResponse = await update<SuccessResponse<any>>(
          URIS.TEMPLATE_TYPE.BASE + Characters.QUESTION_MARK + 'templateTypeId' + Characters.EQUAL + props.gId,
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
      emit('close')
      formData.titleAr = ''
      formData.titleEn = ''
      formData.templateTypeKey = ''
    }

    return {
      formData,
      templateTypeKeyList,
      valid,
      rules,
      saveTemplate,
      cancel,
      selectedId,
      isAdminOrModerator
    }
  }
})
</script>

<template src="./UpdateTemplateType.html" v-if="isAdminOrModerator"></template>
<style src="./UpdateTemplateType.scss" lang="scss" scoped></style>
