/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export enum ChallengeStatus {
  COMPLETED = 'COMPLETED',
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  VOTING = 'VOTING',
  REVISED = 'REVISED',
  PAUSED = 'PAUSED',
  UNDER_VALIDATOR_REVIEW = 'UNDER_VALIDATOR_REVIEW',
  UNDER_ADMIN_REVIEW = 'UNDER_ADMIN_REVIEW',
  NEW = 'NEW',
  URGENT = 'URGENT',
}

export function toChallengeStatus(status: string): ChallengeStatus | undefined {
  return ChallengeStatus[status as keyof typeof ChallengeStatus]
}
