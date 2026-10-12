/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface TemplateTypeResponse {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  titleEn: string
  titleAr: string
  templateTypeKey: string
  mandatoryFieldsEn: string
  mandatoryFieldsAr: string
  mandatoryFieldsListEn: string[]
  mandatoryFieldsListAr: string[]
  localTitle: string
}
