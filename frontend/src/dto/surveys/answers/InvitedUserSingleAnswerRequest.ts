export interface InvitedUserSingleAnswerRequest {
  optionIds?: number[]
  responseText?: string
  questionId: number
  correct?: boolean
  probabilityOfOccurrence?: number | null
  impactSeverity?: number | null
}
