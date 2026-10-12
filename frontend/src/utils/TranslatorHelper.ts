import i18n from '@/plugins/i18n' // adjust a path to your i18n config

export function translate(key: string, params = {}) {
  return i18n.global.t(key, params)
}
