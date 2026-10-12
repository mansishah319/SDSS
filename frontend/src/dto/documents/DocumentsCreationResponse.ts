import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface DocumentsCreationResponse extends BaseModelResponse {
  name: string
  path: string
  type: string
  challengeId: number
}
