<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <div class="language-switcher">
    <v-menu location="bottom" :close-on-content-click="true" transition="scale-transition">
      <template #activator="{ props }">
        <v-btn class="language-button" v-bind="props" variant="text">
          <div class="button-content">
            <v-icon color="#00008B" icon="mdi-earth" size="20"></v-icon>
            <span class="uppercase language-label">
              {{ getLanguageName(currentLanguage) }}
            </span>
            <v-icon icon="mdi-chevron-down" size="16"></v-icon>
          </div>
        </v-btn>
      </template>

      <v-list class="language-dropdown" density="compact">
        <v-list-item
          v-for="(lang, index) in availableLocales"
          :key="index"
          :value="lang"
          :class="{ active: lang === currentLanguage }"
          :aria-selected="lang === currentLanguage"
          @click="setLanguage(lang)"
        >
          <template #prepend>
            <v-icon v-if="lang === currentLanguage" icon="mdi-check" size="16" class="mr-2"></v-icon>
            <span v-else class="icon-spacer"></span>
          </template>
          <v-list-item-title class="language-option">
            <span class="language-full-name">{{ getLanguageName(lang) }}</span>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import { onBeforeUnmount, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import UserClientService from '@/services/user/UserClientService'
import { Fields, SecurityConstants } from '@/constants/DpConstants'

export default {
  setup() {
    const { locale, t } = useI18n()
    const lang = window.localStorage.getItem(Fields.EMPLOYEE_LANG)
    const currentLanguage = ref(lang || locale.value)
    const token = localStorage.getItem(SecurityConstants.TOKEN)

    const availableLocales = ['en', 'ar']

    const getLanguageName = (code) => {
      return code === 'en' ? t('common.en') : t('common.ar')
    }

    const applyLanguageToDocument = (language) => {
      const dir = language === 'ar' ? 'rtl' : 'ltr'
      document.documentElement.setAttribute('dir', dir)
      document.documentElement.setAttribute('lang', language)
      document.body.className = dir
    }

    const syncLanguageFromStorage = () => {
      const savedLanguage = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || locale.value
      currentLanguage.value = savedLanguage
      locale.value = savedLanguage
      applyLanguageToDocument(savedLanguage)
    }

    window.addEventListener('storage', syncLanguageFromStorage)
    window.addEventListener('language-changed', syncLanguageFromStorage)

    const setLanguage = async (lang) => {
      try {
        if (token) {
          await UserClientService.updateLanguage(lang)
        }
        locale.value = lang
        window.localStorage.setItem(Fields.EMPLOYEE_LANG, lang)
        currentLanguage.value = lang
        applyLanguageToDocument(lang)
        window.dispatchEvent(new CustomEvent('language-changed', { detail: { lang } }))
        window.dispatchEvent(new Event('storage'))
        window.location.reload()
      } catch (error) {
        console.error('Language change failed:', error)
      }
    }

    onBeforeUnmount(() => {
      window.removeEventListener('storage', syncLanguageFromStorage)
      window.removeEventListener('language-changed', syncLanguageFromStorage)
    })

    return {
      currentLanguage,
      setLanguage,
      availableLocales,
      getLanguageName
    }
  }
}
</script>

<style src="./LanguageSwitcher.scss" scoped lang="scss"></style>
