import { QuestionType } from '@/types/QuestionTypesEnums'

export interface SurveyQuestionRequest {
  titleEn: string
  titleAr: string
  descriptionEn?: string | null
  descriptionAr?: string | null
  questionType: QuestionType
  surveyId?: number
  roundId?: number
  showPublicQuestions: boolean
  optionsRequestList?: Option[]
}

interface Option {
  optionEn: string
  optionAr: string
}

export interface UpdateSurveyQuestionRequest {
  titleEn: string
  titleAr: string
  descriptionEn?: string | null
  descriptionAr?: string | null
  questionType: QuestionType
  showPublicQuestions: boolean
}
