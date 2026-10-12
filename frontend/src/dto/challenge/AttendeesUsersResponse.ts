import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface AttendeesUsersResponse extends BaseModelResponse {
  fullName: string
  grpNumber: string
  onlineStatus: boolean
  firstname: string
  lastname: string
}
