const ARABIC_CHAR_PATTERN = /[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFF]/

export const getTextDirection = (value?: string | null): 'rtl' | 'ltr' => {
  if (!value) {
    return 'ltr'
  }

  return ARABIC_CHAR_PATTERN.test(value) ? 'rtl' : 'ltr'
}
