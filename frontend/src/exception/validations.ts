// validations.ts

export const createMandatoryValidator =
  (fieldName: string, t: (key: string, params?: Record<string, string | number>) => string) => (fieldValue: any) => {
    return fieldValue !== null && fieldValue !== undefined && fieldValue !== ''
      ? true
      : t('error.mandatory', { fieldName })
  }

export const createMaxLengthValidator =
  (fieldName: string, maxLength: number, t: (key: string, params?: Record<string, string | number>) => string) =>
    (fieldValue: string) => {
      return fieldValue.length <= maxLength ? true : t('error.maxLength', { fieldName, maxLength })
    }

export const createMinLengthValidator =
  (fieldName: string, minLength: number, t: (key: string, params?: Record<string, string | number>) => string) =>
    (fieldValue: string) => {
      return fieldValue.length >= minLength ? true : t('error.minLength', { fieldName, minLength })
    }
