/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

interface Role {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  nameAr: string
  nameEn: string
}

interface UsersExpertisesResponse {
  userId: number
  expertisesId: number
  expertisesName: string
  expertLevel: string
  isPrimary: boolean
}

export interface UserSearchData {
  id: number
  createdBy: string
  createdAt: string
  updatedBy: string
  updatedAt: string
  firstname: string
  lastname: string
  grpNumber: string
  language: string
  nationality?: string
  status: string
  mobile: string
  email: string
  alternativeEmail?: string
  type: string
  title: string
  roles: Role[]
  mainExpertLevel: string
  areaOfExpertise: string
  expertLevel: string
  isMailSend: boolean
  internalDocuments: boolean
  chatContents: boolean
  socialMediaFeeds: boolean
  decision: boolean
  questionView: boolean
  scrollingText: boolean
  gradeName: string
  sectorName: string
  departmentName: string
  departmentType: string
  sectorShortName: string
  usersExpertisesResponses: UsersExpertisesResponse[]
  shortDepartmentName: string
  fullName: string
}
