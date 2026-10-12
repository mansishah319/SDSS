/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

export interface DepartmentResponse {
  id: number
  depOrder: number
  parentDepartmentId: number
  parentDepartmentTypeCode: number
  departmentType: string
  departmentTypeCode: number
  departmentName: string
  shortName: string | null
  encId: string
}
