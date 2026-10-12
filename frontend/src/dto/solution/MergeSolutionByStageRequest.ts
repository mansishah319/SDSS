/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

/**
 * Represents a request to merge solutions by stage.
 */
export interface MergeSolutionByStageRequest {
  /**
   * New solution text.
   * @example "عقد جلسة حوارية تستهدف الموظفين من مختلف المناصب والمسميات لمناقشة وربط المهام والمؤشرات"
   */
  newSolutionText: string

  /**
   * List of solution IDs to be merged.
   */
  mergedSolutionIds: number[]

  /**
   * Question level associated with the solutions.
   */
  queLevel?: number

  /**
   * ID of the challenge related to the solutions.
   */
  challengeId?: number
}
