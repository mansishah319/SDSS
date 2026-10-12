import { LookupInfoResponse } from '@/dto/lookup/LookupInfoResponse'

export interface ReportSdssSurveyResponse extends LookupInfoResponse {
  active: boolean
  status: string
  manualHandle: boolean
  jobList: string[]
  numberOfRounds: number
  allowedGrpList: number[]
  departmentList: string[]

  localStatus: string // Computed property equivalent
  mode: string // Computed property equivalent
  currentUserIsTheSurveyModerator: boolean // Computed property equivalent
  isAllowedToUpdateOrDeleteThisSurvey: boolean // Computed property equivalent
}

export interface RoundReportMainResponse extends LookupInfoResponse {
  startTime: string // Using ISO 8601 format
  endTime: string // Using ISO 8601 format
  manualHandle: boolean
  status: string
  roundNumber: number
  active: boolean
  totalNumberOfInvitedUsers: number
  survey: ReportSdssSurveyResponse

  localStatus: string // Computed property equivalent
  currentUserIsTheSurveyModerator: boolean // Computed property equivalent
  isAllowedToUpdateOrDeleteThisSurveyRound: boolean // Computed property equivalent
}
