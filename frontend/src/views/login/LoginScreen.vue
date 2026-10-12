<script lang="ts">
import { computed, defineComponent, ref, watchEffect } from 'vue'
import LoginHeader from '@/components/template/LoginHeader.vue'
import LoginBanner from '@/components/template/LoginBanner.vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Fields } from '@/constants/DpConstants.ts'
import FooterComponent from '@/components/common/FooterComponent.vue'
import SdssLoginHandler from '@/views/login/SdssLoginHandler.ts'
import { Paths } from '@/Paths'
import { SystemRoles } from '@/dto/enums/SystemRoles.ts'
import { resolveLocalizedValue } from '@/utils/localizationUtils'

export default defineComponent({
  name: 'LoginScreenPage',
  components: {
    FooterComponent,
    LoginHeader,
    LoginBanner
  },
  setup() {
    const router = useRouter()
    const showPassword = ref(false)
    const grpNumber = ref<number | null>(null)
    const username = ref()
    const password = ref()
    const usernameError = ref<string | null>(null)
    const passwordError = ref<string | null>(null)
    const selectedSystemRoles = ref<string[]>([])

    const { locale, t } = useI18n()
    const direction = ref<string>('ltr')
    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'
    const isLocalEnvironment = computed(() => (Paths.NODE_ENV ?? '').toLowerCase().includes('local'))

    const systemRoleItems = computed(() =>
      Object.values(SystemRoles).map((role) => ({
        title: resolveLocalizedValue({
          locale: locale.value,
          ar: role.ar,
          en: role.en,
          fallback: role.key
        }),
        value: role.key
      }))
    )

    const systemRolesLabel = computed(() => (locale.value === 'ar' ? 'أدوار النظام' : 'System Roles'))

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      direction.value = dir
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    const required = (value: string) => !!value || t('error.mandatory')

    const validate = (): boolean => {
      let isValid = true
      if (!username.value) {
        usernameError.value = t('validation.username')
        isValid = false
      }
      if (password.value) {
        usernameError.value = null
      } else {
        passwordError.value = t('validation.password')
        isValid = false
      }

      return isValid
    }

    const login = async () => {
      if (!isLocalEnvironment.value && !validate()) {
        return
      }
      localStorage.setItem('grpNo', username.value)
      const userGrp = grpNumber.value ? Number(grpNumber.value) : null
      const systemRoles = isLocalEnvironment.value ? selectedSystemRoles.value : undefined
      await SdssLoginHandler.sdssLogin(username.value, password.value, userGrp, router, systemRoles)
    }

    return {
      required,
      usernameError,
      passwordError,
      username,
      password,
      showPassword,
      login,
      direction,
      isLocalEnvironment,
      selectedSystemRoles,
      systemRoleItems,
      systemRolesLabel,
      grpNumber
    }
  }
})
</script>

<template src="./LoginScreen.html"></template>
<style src="./LoginScreen.scss" lang="scss"></style>
