/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface CommitteeResponse {
  teamID: number
  teamTitle: string
}

/**
 * Safely extracts committee ID from either a CommitteeResponse object or direct ID value
 * @param selectedCommittee - Can be CommitteeResponse object or direct ID (number/string)
 * @returns The extracted committee ID or undefined if invalid
 */
export function getCommitteeId(
  selectedCommittee: CommitteeResponse | number | string | null | undefined
): number | undefined {
  if (!selectedCommittee) {
    return undefined
  }

  // If it's a CommitteeResponse object
  if (typeof selectedCommittee === 'object' && 'teamID' in selectedCommittee) {
    return selectedCommittee.teamID
  }

  // If it's already a number
  if (typeof selectedCommittee === 'number') {
    return selectedCommittee
  }

  // If it's a string that can be converted to number
  if (typeof selectedCommittee === 'string') {
    const parsed = Number(selectedCommittee)
    return isNaN(parsed) ? undefined : parsed
  }

  return undefined
}
