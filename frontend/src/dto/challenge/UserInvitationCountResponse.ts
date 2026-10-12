/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ChallengeUsersResponse } from '@/dto/challenge/ChallengeUsersResponse.ts'

export interface UserInvitationCountResponse {
  countOfInvitationUsers: number
  countOfInvitationRejected: number
  countOfInvitationAccepted: number
  countOfInvitationPending: number
  invitedInternalUserResponse: ChallengeUsersResponse[]
  pendingUsersResponse: ChallengeUsersResponse[]
  rejectedUsersResponse: ChallengeUsersResponse[]
  acceptedUsersResponse: ChallengeUsersResponse[]
}
