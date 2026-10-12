/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { AttendeesUsersResponse } from '@/dto/challenge/AttendeesUsersResponse.ts'

export interface ChallengeAttendeesUsersResponse {
  onlineCount: number
  offlineCount: number
  invitedAdminUsers: AttendeesUsersResponse[]
  invitedValidatorUsers: AttendeesUsersResponse[]
  allAttendeesUsers: AttendeesUsersResponse[]
}
