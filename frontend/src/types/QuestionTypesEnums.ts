// src/types/QuestionTypes.ts
export const questionTypes = ['single-choice', 'multiple-choice', 'true-false', 'text'] as const
export type QuestionType = (typeof questionTypes)[number]
