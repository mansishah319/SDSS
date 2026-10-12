/*
 *  ***************************************************************
 *  Copyright ©1956-2024 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

interface UsersExpertises {
  areaOfExpertisesId: string
  expertLevel: string
  isPrimary: boolean
}

export interface UserCreationRequest {
  grpNumber: string
  language: string
  email: string
  alternativeEmail?: string
  mobile: string
  alternativeMobile?: string
  internalDocuments: boolean
  chatContents: boolean
  socialMediaFeeds: boolean
  decision: boolean
  questionView: boolean
  scrollingText: boolean
  roles: number[]
  usersExpertises: UsersExpertises[]
  isDelphiModerator: boolean
}
