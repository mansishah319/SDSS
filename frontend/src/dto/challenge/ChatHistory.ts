/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { UserChatResponse } from '@/dto/challenge/UserChatResponse.ts'

export interface ChatHistory {
  id: number
  message: string
  fromName: string
  file: string
  time: number
  type: string
  readStatus: number
  user: UserChatResponse
}
