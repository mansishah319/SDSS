import type { BaseResponse } from '@/dto/generic/BaseResponse'

export interface ErrorResponse extends BaseResponse {
  errorCode: string
}
