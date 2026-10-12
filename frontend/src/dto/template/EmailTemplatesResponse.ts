/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { TemplateTypeResponse } from '@/dto/template/TemplateTypeResponse.ts'

export interface EmailTemplatesResponse {
  id: number
  title: string
  subjectAr: string
  subjectEn: string
  contentAr: string
  contentEn: string
  isDefault: boolean
  localSubject: string
  localContent: string
  templateType: TemplateTypeResponse
}
