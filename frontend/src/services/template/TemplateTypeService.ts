/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'
import { ref } from 'vue'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { get } from '@/utils/GenericApiHelper.ts'
import { URIS } from '@/constants/DpConstants.ts'

export const loadTemplatesTypes = async () => {
  const isLoading = ref(false)
  const dtoResponse = ref<TemplateTypeResponse[]>([])

  try {
    isLoading.value = true
    const response = await get<SuccessResponse<any>>(URIS.TEMPLATE_TYPE.BASE + URIS.TEMPLATE_TYPE.ALL)
    if (response?.success) {
      dtoResponse.value = response.data as TemplateTypeResponse[]
    } else {
      console.error('API request was not successful:', response?.message)
    }
  } catch (error) {
    console.error('Error loading data:', error)
  } finally {
    isLoading.value = false
  }

  return { dtoResponse, isLoading }
}
