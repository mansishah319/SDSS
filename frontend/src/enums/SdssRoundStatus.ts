/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

// Define SdssStatus Enum in TypeScript
export enum SdssRoundStatus {
  PENDING = 'Pending',
  IN_PROGRESS = 'In Progress',
  COMPLETED = 'Completed',
  DONE = 'Done',
  CANCELED = 'Canceled',
  FAILED = 'Failed',
  ON_HOLD = 'On Hold',
  REVIEW = 'Under Review',
}

// Define Arabic translations for the statuses
export const SdssStatusArabic = {
  PENDING: 'معلق',
  IN_PROGRESS: 'قيد التنفيذ',
  COMPLETED: 'مكتمل',
  DONE: 'تم',
  CANCELED: 'ملغى',
  FAILED: 'فشل',
  ON_HOLD: 'معلق',
  REVIEW: 'قيد المراجعة'
}

// Method to get SdssStatus enum by string input
export function sdssRoundStatusFromString(text: string): SdssRoundStatus | undefined {
  const status = Object.keys(SdssRoundStatus).find((key) => {
    const english = SdssRoundStatus[key as keyof typeof SdssRoundStatus]
    const arabic = SdssStatusArabic[key as keyof typeof SdssStatusArabic]
    return (
      english.toLowerCase() === text.toLowerCase() ||
      arabic.toLowerCase() === text.toLowerCase() ||
      key.toLowerCase() === text.toLowerCase()
    )
  })

  return status ? SdssRoundStatus[status as keyof typeof SdssRoundStatus] : undefined
}
