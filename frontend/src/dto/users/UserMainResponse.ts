import type { BaseModelResponse } from '@/dto/generic/BaseModelResponse.ts'

export interface UserMainResponse extends BaseModelResponse {
  fullName?: string
  grpNumber?: string
  language?: string
  nationality?: string
  middleName?: string
  status?: string
  mobile?: string
  alternativeMobile?: string
  email?: string
  alternativeEmail?: string
  type?: string
  title?: string
  roles?: RolesResponse[]
  mainExpertLevel?: string
  areaOfExpertise?: string
  expertLevel?: string
  expertisesId?: number

  isMailSend?: boolean

  internalDocuments?: boolean
  chatContents?: boolean
  socialMediaFeeds?: boolean
  decision?: boolean
  questionView?: boolean
  scrollingText?: boolean

  gradeName?: string

  sectorName?: string
  sectorShortName?: string

  departmentName?: string
  parentDepartmentName?: string
  departmentType?: string

  confirmationUrl?: string
  committeeName?: string
  committeeId?: string
  shortName?: string

  isDelphiModerator?: boolean

  usersExpertisesResponses?: UsersExpertisesResponse[]

  // Alias for shortName
  shortDepartmentName?: string
}

interface UsersExpertisesResponse {
  userId?: number
  expertisesId?: number
  expertisesName?: string
  expertLevel?: string
  isPrimary?: boolean

  // Computed property for localized primary status
  isPrimaryLocal?: string
}

interface RolesResponse extends BaseModelResponse {
  nameAr?: string
  nameEn?: string

  // Computed property for localized name
  localName?: string
}
