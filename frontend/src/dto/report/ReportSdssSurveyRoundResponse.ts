import { LookupInfoResponse } from '@/dto/lookup/LookupInfoResponse'
import { BaseModelResponse } from '@/dto/generic/BaseModelResponse'
import { RoundReportMainResponse } from '@/dto/surveys/round/RoundReportMainResponse'

export interface ReportSdssSurveyQuestionResponse extends LookupInfoResponse {
  questionType: string // Enum or type definition for QuestionType
  showPublicQuestions: boolean
  userAnswersStatistics: Record<string, any> // Key-value pair for statistics
  options: SdssSurveyQuestionOptionsResponse[]
  questionAnalysis: ReportSurveyQuestionAnalysis
  usersSurveyAnswers: UserSurveyAnswersReportResponse[]

  currentUserIsTheSurveyModerator: boolean // Computed property equivalent
  questionTypeKey: string // Computed property equivalent
  localQuestionType: string // Computed property equivalent
}

export interface ReportSdssSurveyResponse extends LookupInfoResponse {
  active: boolean
  status: string // Enum for SurveyStatus
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

export interface EmployeeInfo {
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

export interface ReportUserResponse {
  id: number
  employeeGrpNumber: number
  employeeInfo: EmployeeInfo
}

export interface SurveyUser {
  reportUserResponse: ReportUserResponse
  hasAnswered: boolean
}

export interface UserSurveyAnswersReportResponse extends BaseModelResponse {
  correct?: boolean
  responseText?: string
  impactSeverity?: number
  probabilityOfOccurrence?: number
  user?: ReportInvitedSurveyUserResponse
  question?: UserReportSdssSurveyQuestionResponse
  selectedOptions?: SdssSurveyQuestionOptionsResponse[]
}

export interface ReportSurveyQuestionAnalysis {
  userQualificationSpecificationAnalysis: Record<string, number>
  userNationalityAnalysis: Record<string, number>
  userMaritalStatusAnalysis: Record<string, number>
  userJobAnalysis: Record<string, number>

  userGenderAnalysis: Record<string, number>
  userDepartmentAnalysis: Record<string, number>
}

export interface ReportInvitedSurveyUserResponse {
  id?: number
  employeeGrpNumber?: number
  employeeInfoKey?: string
  employeeInfo?: EmployeeInfo
}

export interface UserReportSdssSurveyQuestionResponse extends LookupInfoResponse {
  questionType?: string
  showPublicQuestions?: boolean
  currentUserIsTheSurveyModerator?: boolean
  questionTypeKey?: string
  localQuestionType?: string
}

export interface SdssSurveyQuestionOptionsResponse extends LookupInfoResponse {
  optionEn?: string
  optionAr?: string
  currentUserIsTheSurveyModerator?: boolean
  localOption?: string
}

export interface ReportSdssSurveyRoundResponse extends LookupInfoResponse {
  startTime: string // ISO 8601 date string
  endTime: string // ISO 8601 date string
  manualHandle: boolean
  status: string // Enum for SdssStatus
  roundNumber: number
  totalNumberOfInvitedUsers: number
  numberOfQuestions: number
  totalTextQuestions: number
  totalMultipleChoiceQuestions: number
  totalSingleSelectQuestions: number
  totalTrueFalseQuestions: number
  active: boolean
  survey: ReportSdssSurveyResponse
  questions: ReportSdssSurveyQuestionResponse[]
  surveyUsers: SurveyUser[]
  usersSurveyAnswers: UserSurveyAnswersReportResponse[]
  retrieveAllTextQuestions: UserSurveyAnswersReportResponse[]
  roundDetails: RoundReportMainResponse

  localStatus: string // Computed property equivalent
  currentUserIsTheSurveyModerator: boolean // Computed property equivalent
  isAllowedToUpdateOrDeleteThisSurveyRound: boolean // Computed property equivalent
}
