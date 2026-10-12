import { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface DropdownResponse extends BaseModelResponse {
  name: string
  localName: string
  type: string
}
