/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn OCT, 2023
 */

class Employee {
  name: string | null = null
  userName: string | null = null
  encId: string | null = null
  avatar: string | null = null
  id: number | null = null
  empGrp: number | null = null
  grpNumber: number | null = null
  email: string | null = null
  empGrpNumber: number | null = null
  gender: string | null = null
  slmMilitaryFName: string | null = null
  empType: number | null = null
  departmentName: string | null = null
  departmentId: number | null = null
  sectorName: string | null = null
  sectorId: number | null = null
  realm: string | null = null
  issuedAt: number | null = null
  issuer: string | null = null
  exp: number | null = null
  permissionList: [] = []
  roles: Array<string> = []
  tokenRoles: Array<string> = []
  language: string | null = null
  languageName: string | null = null
  internalDocuments: boolean | null = null
  chatContents: boolean | null = null
  socialMediaFeeds: boolean | null = null
  decision: boolean | null = null
  questionView: boolean | null = null
  ratings: boolean | null = null
  scrollingText: boolean | null = null
  isDelphiModerator: boolean | null = null
  userPhoto: Uint8Array | null = null
  gradeName: string | null = null
  phoneNumber: string | null = null
  guid: string | null = null
}

export default Employee
