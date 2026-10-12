import { BaseModelResponse } from '@/dto/generic/BaseModelResponse'
import {
  PublicInvitedUserResponse,
  PublicOption,
  PublicSurveyQuestion
} from '@/dto/surveys/public/PublicEndUserSurveyResponse'

export interface SdssSurveyUserQuestionAnswersResponse extends BaseModelResponse {
  correct: boolean
  responseText: string
  impactSeverity: number
  probabilityOfOccurrence: number
  question: PublicSurveyQuestion
  user: PublicInvitedUserResponse
  selectedOptions: PublicOption[]
}
