export default interface PublicEndUserSurveyResponse {
  id: number
  titleEn: string
  titleAr: string
  descriptionEn: string
  descriptionAr: string
  activeRoundResponse: PublicActiveRoundResponse
  invitedUserResponse: PublicInvitedUserResponse
  localTitle: string
  localDescription: string
}

export interface PublicActiveRoundResponse {
  id: number
  titleEn: string
  titleAr: string
  startTime: string
  updatedAt: string
  endTime: string
  descriptionEn: string
  descriptionAr: string
  manualHandle: boolean
  roundNumber: number
  status: string
  localStatus: string
  active: boolean
  questions: PublicSurveyQuestion[]
  localTitle: string
  localDescription: string
}

export interface PublicSurveyQuestion {
  id: number
  titleEn: string
  titleAr: string
  descriptionEn?: string
  descriptionAr?: string
  options?: PublicOption[]
  localTitle: string
  localDescription?: string
  questionTypeKey: string
  localQuestionType: string
  showPublicQuestions: boolean
}

export interface PublicOption {
  id: number
  optionEn: string
  optionAr: string
  localOption: string
}

export interface PublicInvitedUserResponse {
  id: number
  employeeGrpNumber: number
  employeeInfo: PublicEmployeeInfo
}

export interface PublicEmployeeInfo {
  id: number
  personId: number
  emailAddress: string
  gender: string
  fullName: string
  arabLastName: string
  jobId: number
  jobName: string
  officeNumber: string
  departmentName: string
  departmentId: number
  personalTitle: string
  positionId: number
  positionName: string
  gradeId: number
  gradeName: string
  parentDepartmentName: string
  parentDepartmentId: number
  parentOrganizationType: string
  slmNameE: string
  firstName: string
  lastName: string
  personTitleAr: string
  maritalStatusDesc: string
  qualificationType: string
  qualificationSpecification: string
}
