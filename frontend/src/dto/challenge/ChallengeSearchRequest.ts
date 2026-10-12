/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface ChallengeSearchRequest {
  title: string | null
  status: string | null
  moderator: string | null
  visibility: string | null
  department: string | null
  description: string | null
  startTime: string | null
  endTime: string | null
  createdAt: string | null
  lastModifiedAt: string | null
  startVotingDate: string | null
  endVotingDate: string | null
}
