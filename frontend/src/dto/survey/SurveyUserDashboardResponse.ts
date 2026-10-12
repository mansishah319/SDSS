// BaseMarkerDTO can be an empty interface or shared base if needed

export interface BaseMarkerDTO {
  // marker dto
}

// -----------------------------
// SurveyUserActiveResponse
// -----------------------------
export interface SurveyUserActiveResponse extends BaseMarkerDTO {
  surveyId: number
  surveyNameEn: string
  surveyNameAr: string
  descriptionEn: string
  descriptionAr: string
  totalRounds: number
  activeRounds: number
  hasAnswered: boolean
  status: string
}

// -----------------------------
// SurveyUserDashboardResponse
// -----------------------------
export interface SurveyUserDashboardResponse extends BaseMarkerDTO {
  internalUser: boolean
  delphiModerator: boolean

  // User Information
  employeeGrpNumber: number
  employeeFullName: string
  departmentName: string
  jobTitle: string

  // Survey Statistics
  totalSurveys: number
  answeredSurveys: number
  pendingSurveys: number
  participationRate: number
  recentActivity: number
  lastActivityDate: string // ISO string (from LocalDateTime)

  // Survey Counts
  activeSurveysCount: number
  pendingSurveysCount: number
  completedSurveysCount: number
  totalAnswersCount: number

  // Delphi moderator review statistics
  totalReviewsMade: number
  reviewedSolutionsCount: number
  reviewedChallengesCount: number

  // Survey Lists
  activeSurveysList: SurveyUserActiveResponse[]
  pendingSurveysList: SurveyUserActiveResponse[]
  completedSurveysList: SurveyUserActiveResponse[]
}
