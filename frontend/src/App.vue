<script lang="ts">
import { useTheme } from 'vuetify'
import { useI18n } from 'vue-i18n'
import { computed, ref, watch, watchEffect } from 'vue'
import LocaleSwitcher from '@/components/locals/LocaleSwitcher.vue'
import UserProfileDropdown from '@/components/account/UserProfileDropdown.vue'
import {
  getEmployeeData,
  hasAnyExceptCommanderRole,
  isAdmin,
  isAdminOrModerator,
  isCommanderOnly,
  isHasAnyRole,
} from '@/utils/roleUtils'
import { useSidebarStore } from '@/store/sidebar'
import { useSdssInternalUserCheckStore } from '@/store/sdssInternalUserCheck'
import { useRoute, useRouter } from 'vue-router'
import futureForesightLogoUrl from '@/assets/images/logo/future-foresight-white.svg'
import { applyLanguage, getStoredLanguage } from '@/utils/language'

/**
 * ============================================================
 * SDSS HARDCODED LOGIN TOKEN
 * ============================================================
 */

const SDSS_LOGIN_TOKEN =
  'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZ3JwTnVtYmVyIjoiNjk2Nzk3IiwiZW1wbG95ZWVOYW1lIjoiQWRtaW4iLCJmdWxsTmFtZSI6IkFkbWluIEFkbWluIiwidXNlck5hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW5AZHViYWlwb2xpY2UuZ292LmFlIiwicm9sZXMiOlsiUE9SVEFMQURNSU4iXSwicm9sZUxpc3QiOlsiUk9MRV9QT1JUQUxBRE1JTiJdLCJsYW5ndWFnZSI6ImVuIiwiZW1wVHlwZSI6MiwiZ2VuZGVyIjoibWFsZSIsImRlcGFydG1lbnRJZCI6MSwiZGVwYXJ0bWVudE5hbWUiOiJJVCBEZXBhcnRtZW50Iiwic2VjdG9ySWQiOjEsInNlY3Rvck5hbWUiOiJJVCBTZWN0b3IiLCJyYXRpbmdzIjp0cnVlLCJkZWNpc2lvbiI6dHJ1ZSwicXVlc3Rpb25WaWV3Ijp0cnVlLCJjaGF0Q29udGVudHMiOnRydWUsInNjcm9sbGluZ1RleHQiOnRydWUsInNvY2lhbE1lZGlhRmVlZHMiOnRydWUsImludGVybmFsRG9jdW1lbnRzIjp0cnVlLCJpc0RlbHBoaU1vZGVyYXRvciI6ZmFsc2UsImlzcyI6IkRFRkFVTFRfSVNTVUVSIiwiaWF0IjoxNzkwMzU5NTIxLCJleHAiOjE4ODQ5Njc1MjF9.9__CnJO6gZSX29cXt9reYTq2JSiovaLV600uPQvvHG6rCfiUel4GC3M-FkI7QvT3MjFkafRhCVdNP9apn9L2PQ'

/**
 * Set the hardcoded token whenever the application starts.
 */
const initializeHardcodedAuth = () => {
  localStorage.setItem('sdss_login_token', SDSS_LOGIN_TOKEN)

  console.log('SDSS hardcoded login token initialized')
}

/**
 * ============================================================
 * SIDEBAR
 * ============================================================
 */

const getSidebarPanelFromRoute = (path: string): string | null => {
  if (path.startsWith('/users')) {
    return 'users_panel'
  }

  if (path.startsWith('/challenge/')) {
    return 'challenges_panel'
  }

  if (path.startsWith('/settings/')) {
    return 'settings_panel'
  }

  if (path.startsWith('/emailSettings/')) {
    return 'emailSettings_panel'
  }

  return null
}

export default {
  name: 'App',

  components: {
    LocaleSwitcher,
    UserProfileDropdown,
  },

  setup() {
    /**
     * ========================================================
     * INITIALIZE HARDCODED AUTH
     * ========================================================
     *
     * This runs when the App component starts.
     */
    initializeHardcodedAuth()

    const route = useRoute()
    const router = useRouter()

    const sidebarStore = useSidebarStore()

    const isInternalUser = useSdssInternalUserCheckStore()

    const theme = useTheme()

    const activeTheme = ref(localStorage.getItem('theme') === 'dark')

    const isActiveTheme = ref(localStorage.getItem('theme') === 'dark')

    const { locale } = useI18n()

    const isRtl = ref(locale.value === 'ar')

    /**
     * ========================================================
     * LANGUAGE
     * ========================================================
     */

    const setLanguage = (lang: string) => {
      locale.value = applyLanguage(lang)
    }

    applyLanguage(getStoredLanguage())

    watch(locale, (newLocale) => {
      isRtl.value = newLocale === 'ar'
    })

    /**
     * ========================================================
     * FULL SCREEN
     * ========================================================
     */

    const isFullScreenPage = computed(() => {
      return route.meta?.fullScreen || false
    })

    /**
     * ========================================================
     * FULL PAGE
     * ========================================================
     */

    const openedSidebarPanel = ref<string | null>(sidebarStore.activePanel)

    const isFullPage = computed(() => {
      const fullScreenMeta = route.meta?.isHomeScreen || false

      const isHomeOrSurveyHome = route.name === 'home' || route.name === 'surveyHome'

      return fullScreenMeta || isHomeOrSurveyHome
    })

    /**
     * ========================================================
     * RTL / LTR
     * ========================================================
     */

    watchEffect(() => {
      const dir = locale.value === 'ar' ? 'rtl' : 'ltr'

      document.documentElement.setAttribute('dir', dir)

      document.body.classList.remove('rtl', 'ltr')

      document.body.classList.add(dir)
    })

    /**
     * ========================================================
     * SIDEBAR ROUTE WATCH
     * ========================================================
     */

    watch(
      () => route.path,
      (path) => {
        const matchingPanel = getSidebarPanelFromRoute(path)

        openedSidebarPanel.value = matchingPanel

        sidebarStore.setActivePanel(matchingPanel)
      },
      {
        immediate: true,
      },
    )

    watch(openedSidebarPanel, (panel) => {
      sidebarStore.setActivePanel(panel)
    })

    /**
     * ========================================================
     * THEME
     * ========================================================
     */

    const changeTheme = (isDark: boolean) => {
      theme.change(isDark ? 'dark' : 'light')

      localStorage.setItem('theme', isDark ? 'dark' : 'light')
    }

    changeTheme(isActiveTheme.value)

    /**
     * ========================================================
     * HOME
     * ========================================================
     */

    const goToHome = () => {
      router.push({
        name: 'home',
      })
    }

    /**
     * ========================================================
     * RETURN
     * ========================================================
     */

    return {
      activeTheme,
      isActiveTheme,
      theme,

      goToHome,
      changeTheme,

      currentLanguage: locale,
      setLanguage,

      isRtl,

      isHasAnyRole,
      isCommanderOnly,
      hasAnyExceptCommanderRole,
      isAdminOrModerator,
      isAdmin,

      sidebarStore,

      openedSidebarPanel,

      isInternalUser,

      isFullScreenPage,
      isFullPage,

      futureForesightLogoUrl,
    }
  },

  /**
   * ==========================================================
   * DATA
   * ==========================================================
   */

  data() {
    return {
      snackTimeOut: Number(2400),

      lazy: false,

      name: '',

      rightMenu: [],

      isFullScreen: false,

      isRightSidebar: false,
    }
  },

  /**
   * ==========================================================
   * COMPUTED
   * ==========================================================
   */

  computed: {
    employee() {
      return getEmployeeData()
    },
  },

  /**
   * ==========================================================
   * METHODS
   * ==========================================================
   */

  methods: {
    makeFullScreen() {
      const elem = document.documentElement

      if (this.isFullScreen) {
        this.isFullScreen = false

        if (document.exitFullscreen) {
          document.exitFullscreen()
        } else {
          console.log('Fullscreen API is not supported.')
        }
      } else {
        this.isFullScreen = true

        if (elem.requestFullscreen) {
          elem.requestFullscreen()
        } else {
          console.log('Fullscreen API is not supported.')
        }
      }
    },
  },
}
</script>

<template src="./App.html"></template>

<style src="./App.scss" lang="scss" scoped></style>

<style lang="scss" src="@/assets/css/app.scss"></style>
