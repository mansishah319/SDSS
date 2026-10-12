import { ReportSdssSurveyQuestionResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'

export const mockQuestionData: ReportSdssSurveyQuestionResponse = {
  id: 281,
  createdBy: '32011',
  createdAt: '2025-05-13 11:48:07',
  updatedBy: '32011',
  updatedAt: '2025-05-13 11:49:45',
  titleEn: 'ما هي أنواع الجرائم التي تراها منتشرة في منطقتك؟',
  titleAr: 'ما هي أنواع الجرائم التي تراها منتشرة في منطقتك؟',
  descriptionEn: 'اختر كل ما ينطبق من الأنواع التالية.',
  descriptionAr: 'اختر كل ما ينطبق من الأنواع التالية.',
  showPublicQuestions: true,
  userAnswersStatistics: {
    averageProbabilityOfOccurrence: 60.0,
    averageImpactSeverity: 30.0,
    optionStatistics: [
      {
        id: 242,
        createdBy: '32011',
        createdAt: '2025-05-13 11:48:08',
        updatedBy: '32011',
        updatedAt: '2025-05-13 11:48:08',
        optionEn: 'Cyber fraud',
        optionAr: 'احتيال إلكتروني',
        count: 1,
        currentUserIsTheSurveyModerator: true,
        localOption: 'احتيال إلكتروني'
      },
      {
        id: 245,
        createdBy: '32011',
        createdAt: '2025-05-13 11:48:08',
        updatedBy: '32011',
        updatedAt: '2025-05-13 11:48:08',
        optionEn: 'Property vandalism',
        optionAr: 'تخريب ممتلكات',
        count: 0,
        currentUserIsTheSurveyModerator: true,
        localOption: 'تخريب ممتلكات'
      },
      {
        id: 244,
        createdBy: '32011',
        createdAt: '2025-05-13 11:48:08',
        updatedBy: '32011',
        updatedAt: '2025-05-13 11:48:08',
        optionEn: 'Harassment',
        optionAr: 'تحرش',
        count: 0,
        currentUserIsTheSurveyModerator: true,
        localOption: 'تحرش'
      },
      {
        id: 243,
        createdBy: '32011',
        createdAt: '2025-05-13 11:48:08',
        updatedBy: '32011',
        updatedAt: '2025-05-13 11:48:08',
        optionEn: 'Domestic violence',
        optionAr: 'عنف منزلي',
        count: 1,
        currentUserIsTheSurveyModerator: true,
        localOption: 'عنف منزلي'
      },
      {
        id: 241,
        createdBy: '32011',
        createdAt: '2025-05-13 11:48:08',
        updatedBy: '32011',
        updatedAt: '2025-05-13 11:48:08',
        optionEn: 'Theft',
        optionAr: 'سرقة',
        count: 1,
        currentUserIsTheSurveyModerator: true,
        localOption: 'سرقة'
      }
    ],
    totalCount: 1
  },
  questionAnalysis: {
    userQualificationSpecificationAnalysis: {
      'ادارة تكنولوجيا المعلومات': 1,
      'ادارة تكنولوجيا ': 10,
      'ادارة  ': 20,
      'الاصطناعي  ': 15
    },
    userNationalityAnalysis: {
      UAE_P: 10,
      EG: 11,
      USA: 2,
      UK: 9
    },
    userJobAnalysis: {
      'أخصائي تطوير نظم رئيسي - الادارة العامة للذكاء الاصطناعي ': 1,
      'أخصائي تطوير نظم رئيسي': 4
    },
    userDepartmentAnalysis: {
      'الادارة العامة للذكاء الاصطناعي ': 1
    }
  },
  options: [
    {
      id: 242,
      createdBy: '32011',
      createdAt: '2025-05-13 11:48:08',
      updatedBy: '32011',
      updatedAt: '2025-05-13 11:48:08',
      optionEn: 'Cyber fraud',
      optionAr: 'احتيال إلكتروني',
      currentUserIsTheSurveyModerator: true,
      localOption: 'احتيال إلكتروني'
    },
    {
      id: 243,
      createdBy: '32011',
      createdAt: '2025-05-13 11:48:08',
      updatedBy: '32011',
      updatedAt: '2025-05-13 11:48:08',
      optionEn: 'Domestic violence',
      optionAr: 'عنف منزلي',
      currentUserIsTheSurveyModerator: true,
      localOption: 'عنف منزلي'
    },
    {
      id: 241,
      createdBy: '32011',
      createdAt: '2025-05-13 11:48:08',
      updatedBy: '32011',
      updatedAt: '2025-05-13 11:48:08',
      optionEn: 'Theft',
      optionAr: 'سرقة',
      currentUserIsTheSurveyModerator: true,
      localOption: 'سرقة'
    },
    {
      id: 244,
      createdBy: '32011',
      createdAt: '2025-05-13 11:48:08',
      updatedBy: '32011',
      updatedAt: '2025-05-13 11:48:08',
      optionEn: 'Harassment',
      optionAr: 'تحرش',
      currentUserIsTheSurveyModerator: true,
      localOption: 'تحرش'
    },
    {
      id: 245,
      createdBy: '32011',
      createdAt: '2025-05-13 11:48:08',
      updatedBy: '32011',
      updatedAt: '2025-05-13 11:48:08',
      optionEn: 'Property vandalism',
      optionAr: 'تخريب ممتلكات',
      currentUserIsTheSurveyModerator: true,
      localOption: 'تخريب ممتلكات'
    }
  ],
  currentUserIsTheSurveyModerator: true,
  questionTypeKey: 'multiple-choice',
  localQuestionType: 'اختيارات متعددة',
  localTitle: 'ما هي أنواع الجرائم التي تراها منتشرة في منطقتك؟',
  localDescription: 'اختر كل ما ينطبق من الأنواع التالية.'
}
