import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface UserDropDownListResponse extends BaseModelResponse {
  firstname: string
  grpNumber: string
  lastname: string
  email: string
  expertLevel: string
  areaOfExpertise: string
  fullName: string
}
