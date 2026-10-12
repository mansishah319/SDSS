/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { createRouter, createWebHistory } from 'vue-router'
import { Fields, SecurityConstants } from '@/constants/DpConstants.ts'
import Employee from '@/dto/Employee.ts'
import { isAdmin, isAdminOrModerator, isDelphiModerator } from '@/utils/roleUtils'
import Wso2Service, { WSO2_AUTH_REDIRECT_KEY } from '@/idm/Wso2Service'
import { applyEmployeeLanguage, applyLanguage, getStoredLanguage } from '@/utils/language'
import { clearAuthSession } from '@/utils/authSession'

const SurveyReportsDetails = () =>
  import(/* webpackChunkName: "feature-surveys-reports" */ '@/views/surveys/survey/report/SurveyReportsDetails.vue')
const AdminWelcome = () =>
  import(/* webpackChunkName: "feature-admin" */ '@/views/admin/AdminWelcome.vue')
const ProfileSettings = () =>
  import(/* webpackChunkName: "feature-profile" */ '@/views/profile/ProfileSettings.vue')
const MyProfileView = () =>
  import(/* webpackChunkName: "feature-profile" */ '@/views/profile/MyProfileView.vue')

// Core/Critical Routes - Prefetch these
const LoginPage = () => import(/* webpackChunkName: "core-login" */ '@/views/login/LoginScreen.vue')
const Wso2Callback = () => import(/* webpackChunkName: "core-login" */ '@/idm/callBack.vue')
const SdssHomePage = () =>
  import(/* webpackChunkName: "core-home", webpackPrefetch: true */ '@/views/home/Homepage.vue')
const AdminDashboard = () =>
  import(/* webpackChunkName: "core-admin", webpackPrefetch: true */ '@/views/dashboard/AdminDashboard.vue')

// User Management - Group in same chunk
const UserList = () => import(/* webpackChunkName: "feature-users" */ '@/views/Users/Users.vue')
const UsersCreation = () => import(/* webpackChunkName: "feature-users" */ '@/views/Users/creation/UsersCreation.vue')
const UpdateUser = () => import(/* webpackChunkName: "feature-users" */ '@/views/Users/update/UpdateUser.vue')

// Challenge Features
const ChallengeList = () =>
  import(/* webpackChunkName: "feature-challenges-list" */ '@/views/challenges/list/challenges.vue')
const ChallengeCreation = () =>
  import(/* webpackChunkName: "feature-challenges-create" */ '@/views/challenges/creation/ChallengeCreation.vue')
const ChallengeUpdate = () =>
  import(/* webpackChunkName: "feature-challenges-create" */ '@/views/challenges/update/ChallengeUpdate.vue')
const MyChallenges = () =>
  import(/* webpackChunkName: "feature-challenges-list" */ '@/views/challenges/myChallenges/MyChallenges.vue')
const ChallengeView = () =>
  import(/* webpackChunkName: "feature-challenges-view" */ '@/views/challenges/view/ChallengesView.vue')
const ChallengeDashboard = () =>
  import(/* webpackChunkName: "feature-challenges-dashboard" */ '@/views/challenges/dashboard/ChallengeDashboard.vue')
const ChallengeSettingList = () =>
  import(
    /* webpackChunkName: "feature-challenges-settings" */ '@/views/challengeSettingList/list/ChallengeSettingList.vue'
    )

// Survey Features
const SdssSurvey = () =>
  import(/* webpackChunkName: "feature-surveys-list" */ '@/views/surveys/survey/list/SdssSurvey.vue')
const SurveyFormPage = () =>
  import(/* webpackChunkName: "feature-surveys-form" */ '@/views/surveys/survey/form/SurveyFormPage.vue')
const SdssSurveyView = () =>
  import(/* webpackChunkName: "feature-surveys-view" */ '@/views/surveys/survey/view/SdssSurveyView.vue')
const SurveysDashboard = () =>
  import(/* webpackChunkName: "feature-surveys-dashboard" */ '@/views/surveys/dashboard/SurveysDashboard.vue')

// Strategic Features
const strategicDirection = () =>
  import(/* webpackChunkName: "feature-strategic" */ '@/views/strategicDirection/list/strategicDirection.vue')
const FutureStrategicDirection = () =>
  import(
    /* webpackChunkName: "feature-strategic" */ '@/views/futureStrategicDirection/list/FutureStrategicDirection.vue'
    )
const FutureStrategicGoals = () =>
  import(/* webpackChunkName: "feature-strategic" */ '@/views/futureStrategicGoals/list/FutureStrategicGoals.vue')
const StrategicGoals = () =>
  import(/* webpackChunkName: "feature-strategic" */ '@/views/strategicGoals/list/strategicGoals.vue')

// Admin Features
const UserRequestsDashboard = () =>
  import(/* webpackChunkName: "feature-admin" */ '@/views/userRequests/UserRequestsDashboard.vue')
const ValidatorRequestsDashboard = () =>
  import(/* webpackChunkName: "feature-admin" */ '@/views/validator/requests/ValidatorDashboard.vue')
const EmailTemplateManager = () =>
  import(/* webpackChunkName: "feature-admin" */ '@/views/emailTemplate/list/EmailTemplateList.vue')
const TemplateType = () => import(/* webpackChunkName: "feature-admin" */ '@/views/TemplateType/list/TemplateType.vue')
const AreaOfExpertise = () =>
  import(/* webpackChunkName: "feature-admin" */ '@/views/expertise/list/areaOfExpertise.vue')

// Survey Feature Components
const SurveyRoundView = () =>
  import(/* webpackChunkName: "feature-surveys-round" */ '@/views/surveys/round/view/SurveyRoundView.vue')
const MainSurveyEndUserView = () =>
  import(/* webpackChunkName: "feature-surveys-user" */ '@/views/surveys/main/MainSurveyEndUserView.vue')
const LiveSurveys = () =>
  import(/* webpackChunkName: "feature-surveys-live" */ '@/views/surveys/survey/live/LiveSurveys.vue')
const SurveySummaryPage = () =>
  import(/* webpackChunkName: "feature-surveys-summary" */ '@/views/surveys/summary/SurveySummaryPage.vue')
const SurveyHistoryApp = () =>
  import(/* webpackChunkName: "feature-surveys-history" */ '@/views/surveys/survey/history/SurveyHistoryApp.vue')
const SurveyReports = () =>
  import(/* webpackChunkName: "feature-surveys-reports" */ '@/views/surveys/survey/report/SurveyReports.vue')
const SurveyHomepage = () =>
  import(
    /* webpackChunkName: "feature-surveys-home", webpackPrefetch: true */ '@/views/surveys/home/SurveyHomepage.vue'
    )
const SdssHomePage2 = () =>
  import(/* webpackChunkName: "feature-surveys-home", webpackPrefetch: true */ '@/views/sdssDashboard/SdssHomepage.vue')

// Common Components - Group small components together
const SurveySuccessPage = () =>
  import(/* webpackChunkName: "common-components" */ '@/components/common/SurveySuccessPage.vue')
const LiveSurveysEmptyScreenPage = () =>
  import(/* webpackChunkName: "common-components" */ '@/components/common/EmptyScreenPage.vue')
const NotFoundPage = () => import(/* webpackChunkName: "common-components" */ '@/components/common/NotFoundPage.vue')

// Additional Features
const FuturePollScreen = () =>
  import(/* webpackChunkName: "feature-future-poll" */ '@/views/futurePoll/FuturePollScreen.vue')
const ImpactScreen = () => import(/* webpackChunkName: "feature-impact" */ '@/views/impact/ImpactScreen.vue')

const routes = [
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
    meta: { requiresAuth: false, isHomeScreen: true }
  },
  {
    path: '/callback',
    name: 'callBack',
    component: Wso2Callback,
    meta: { requiresAuth: false, isHomeScreen: true }
  },
  {
    path: '/sdss/profile/me',
    name: 'MyProfileView',
    component: MyProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/settings',
    name: 'ProfileSettings',
    component: ProfileSettings,
    meta: { requiresAuth: true }
  },
  {
    path: '/surveys/round/view/survey/:surveyId/round/:roundId',
    name: 'SurveyRoundView',
    component: SurveyRoundView,
    meta: { requiresAuth: true },
    props: true
  },
  {
    path: '/surveys/form/:surveyId',
    name: 'MainSurveyEndUserView',
    component: MainSurveyEndUserView,
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/home',
    name: 'MainUsersSurveyHomeScreen',
    component: SurveyHomepage,
    meta: { requiresAuth: false, isHomeScreen: true }
  },
  {
    path: '/surveys/submit-form/success',
    name: 'SurveySuccessPage',
    component: SurveySuccessPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/summary',
    name: 'SurveySummaryPage',
    component: SurveySummaryPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/history',
    name: 'SurveyHistoryApp',
    component: SurveyHistoryApp,
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/admin-reports',
    name: 'SurveyReports',
    component: SurveyReports,
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/admin-reports/round/:roundId',
    name: 'SurveyReportsDetails',
    component: SurveyReportsDetails,
    meta: { requiresAuth: false },
    props: true
  },
  {
    path: '/surveys/my-dashboard',
    name: 'SurveysDashboard',
    component: SurveysDashboard,
    meta: { requiresAuth: false }
  },
  {
    path: '/future-poll',
    name: 'FuturePollScreen',
    component: FuturePollScreen,
    meta: { requiresAuth: false }
  },
  {
    path: '/impact',
    name: 'ImpactScreen',
    component: ImpactScreen,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'home',
    component: SdssHomePage,
    meta: { requiresAuth: true, isHomeScreen: true }
  },
  // {
  //   path: '/',
  //   name: 'home',
  //   component: MainHomeScreen,
  //   meta: { requiresAuth: true },
  // },
  {
    path: '/MainDashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/MyRequestsDashboard/:tab?',
    name: 'UserRequestsDashboard',
    component: UserRequestsDashboard,
    meta: { requiresAuth: true, requiresValidator: true }
  },
  {
    path: '/validator/dashboard',
    name: 'ValidatorRequests',
    component: ValidatorRequestsDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'UsersList',
    component: UserList,
    meta: { requiresAuth: true }
  },
  {
    path: '/users/registration',
    name: 'UsersCreation',
    component: UsersCreation,
    meta: { requiresAuth: true }
  },
  {
    path: '/users/update/:grpNumber',
    name: 'UpdateUser',
    component: UpdateUser,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/registration',
    name: 'ChallengeCreation',
    component: ChallengeCreation,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/update/:id',
    name: 'ChallengeUpdate',
    component: ChallengeUpdate,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/list',
    name: 'ChallengeList',
    component: ChallengeList,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/MyChallenges',
    name: 'MyChallenges',
    component: MyChallenges,
    meta: { requiresAuth: true }
  },
  {
    path: '/surveys',
    name: 'SdssSurvey',
    component: SdssSurvey,
    meta: { requiresAuth: true }
  },
  {
    path: '/surveys/create',
    name: 'SdssSurveyCreate',
    component: SurveyFormPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/surveys/update/:surveyId',
    name: 'SdssSurveyUpdate',
    component: SurveyFormPage,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/surveys/live',
    name: 'LiveSurveys',
    component: LiveSurveys,
    meta: { requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFoundPage
  },
  {
    path: '/surveys/live/empty',
    name: 'LiveSurveysEmptyScreenPage',
    component: LiveSurveysEmptyScreenPage,
    props: (route: any) => ({
      imageSize: route.query.imageSize ? Number(route.query.imageSize) : undefined,
      message: route.query.message,
      redirectTo: route.query.redirectTo,
      backButtonTitle: route.query.backButtonTitle,
      backButtonIcon: route.query.backButtonIcon,
      backButtonColor: route.query.backButtonColor
    }),
    meta: { requiresAuth: false }
  },
  {
    path: '/surveys/details/:surveyId',
    name: 'SdssSurveyView',
    component: SdssSurveyView,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/view/:challengeId',
    name: 'ChallengeView',
    component: ChallengeView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/challenge/dashboard/:challengeId',
    name: 'ChallengeDashboard',
    component: ChallengeDashboard,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/emailSettings/emailTemplate',
    name: 'EmailTemplateList',
    component: EmailTemplateManager,
    meta: { requiresAuth: true }
  },
  {
    path: '/emailSettings/template/types',
    name: 'TemplateTypeList',
    component: TemplateType,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/areaExpertises',
    name: 'AreaOfExpertise',
    component: AreaOfExpertise,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/strategicDirection',
    name: 'StrategicDirection',
    component: strategicDirection,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/strategicGoals',
    name: 'StrategicGoals',
    component: StrategicGoals,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/futureStrategicDirection',
    name: 'FutureStrategicDirection',
    component: FutureStrategicDirection,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/futureStrategicGoals',
    name: 'FutureStrategicGoals',
    component: FutureStrategicGoals,
    meta: { requiresAuth: true }
  },
  {
    path: '/settings/challengeSetting',
    name: 'ChallengeSettingScreen',
    component: ChallengeSettingList,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/test',
    name: 'SdssHomePage',
    component: () => import('../views/test/TestVueCode.vue'),
    // component: () => import('../views/surveys/home/SurveyHomepage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminWelcome,
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
      isHomeScreen: false
    }
  },
  {
    path: '/access-denied',
    name: 'AccessDenied',
    component: () => import('@/views/common/AccessDeniedView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/sdssDashboard/SdssHomepage',
    name: 'SdssHomepage2',
    component: SdssHomePage2,
    meta: { requiresAuth: false, isHomeScreen: true }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  console.log('Navigating to:', to.fullPath)
  console.log('Navigating from:', from.fullPath)

  let nextCalled = false
  const handleNext = (redirect = false, routeName = 'LoginPage') => {
    if (!nextCalled) {
      nextCalled = true
      if (redirect) {
        next({ name: routeName }) // Explicit redirect
      } else {
        next() // Explicit continuation
      }
    }
  }
  const redirectToLogin = () => {
    if (!nextCalled) {
      nextCalled = true
      clearSession()
      next({ name: 'LoginPage', replace: true })
    }
  }

  // Local modes keep the existing SDSS login. Every deployed environment is
  // authenticated through WSO2 before a protected route can be entered.
  if (!Wso2Service.isLocalEnvironment()) {
    if (to.name === 'callBack') {
      handleNext()
      return
    }

    if (!to.meta.requiresAuth && to.name !== 'LoginPage') {
      handleNext()
      return
    }

    const token = await Wso2Service.ensureValidToken()
    if (token) {
      await Wso2Service.hydrateEmployeeFromAuthorities(Wso2Service.loadEmployeeFromToken())
      applyStoredEmployeeLanguage()
      if (to.name === 'LoginPage') {
        handleNext(true, 'home')
        return
      }
    } else {
      sessionStorage.setItem(WSO2_AUTH_REDIRECT_KEY, to.name === 'LoginPage' ? '/' : to.fullPath)

      // If the IDS session still exists, exchange it for the SDSS backend token.
      if (await Wso2Service.isSignedIn()) {
        const callbackHandled = await Wso2Service.handleCallback()
        if (callbackHandled) {
          handleNext(true, 'home')
          return
        }
      }

      await Wso2Service.login()
      return
    }
  }

  // Check if a user is already logged in when accessing the login page
  if (shouldRedirectFromLogin(to)) {
    console.log('User is already logged in, redirecting to home page.')
    handleNext(true, 'home')
    return
  }

  // Check token expiration and session validity
  if (isSessionExpiredOrInvalid()) {
    console.log('Token is expired or invalid, clearing session and redirecting.')
    redirectToLogin()
    return
  }

  // Check authentication requirements
  if (requiresAuthButNotLoggedIn(to)) {
    console.log('Authentication required but not logged in, redirecting to login page.')
    redirectToLogin()
    return
  }

  // Delphi Moderator check for /surveys/admin-reports
  if (
    to.name === 'SurveyReports' ||
    to.name === 'SurveyReportsDetails' ||
    to.name === 'SdssSurvey' ||
    to.name === 'SdssSurveyView' ||
    to.name === 'SurveyRoundView'
  ) {
    if (!isDelphiModerator()) {
      return redirectToLogin()
    }
  }

  // Admin or Moderator check
  if (
    to.path === '/admin' ||
    to.path === '/challenge/list' ||
    to.name === 'ChallengeUpdate' ||
    to.name === 'ChallengeView' ||
    to.name === 'AreaOfExpertise' ||
    to.name === 'StrategicDirection' ||
    to.name === 'FutureStrategicDirection' ||
    to.name === 'FutureStrategicGoals' ||
    to.name === 'ChallengeSettingScreen' ||
    to.name === 'TemplateTypeList' ||
    to.name === 'StrategicGoals' ||
    to.name === 'EmailTemplateList'
  ) {
    if (!isAdminOrModerator()) {
      return redirectToLogin()
    }
  }

  if (to.path === '/users' || to.path === '/users/registration' || to.name === 'UpdateUser') {
    if (!isAdmin()) {
      return redirectToLogin()
    }
  }

  console.log('No authentication required or token exists, proceeding to next')
  handleNext()
})

// Helper functions
function shouldRedirectFromLogin(to: any): boolean {
  if (to.name !== 'LoginPage') return false
  const employeeToken = localStorage.getItem(SecurityConstants.TOKEN)
  const employeeJsonStr = localStorage.getItem(Fields.EMPLOYEE)

  // If session data is incomplete or invalid, do not redirect away from login
  if (!employeeToken || !employeeJsonStr) {
    return false
  }

  // If the stored session is expired/invalid, clear it and stay on login
  if (isSessionExpiredOrInvalid()) {
    clearSession()
    return false
  }

  // Token and employee exist and are valid → redirect from login to home
  return true
}

function isSessionExpiredOrInvalid(): boolean {
  const employeeJsonStr = localStorage.getItem(Fields.EMPLOYEE)
  if (!employeeJsonStr) return false

  try {
    const employee: Employee = JSON.parse(employeeJsonStr)
    const exp = employee.exp as number
    const currentTime = Math.floor(Date.now() / 1000)
    const bufferTime = 300 // 5-minute buffer

    return currentTime > exp - bufferTime
  } catch (error) {
    console.error('Error parsing employee data:', error)
    return true
  }
}

function clearSession(): void {
  clearAuthSession()
}

function applyStoredEmployeeLanguage(): void {
  try {
    applyEmployeeLanguage(JSON.parse(localStorage.getItem(Fields.EMPLOYEE) || 'null'))
  } catch (error) {
    console.error('Error parsing employee language:', error)
    applyLanguage(getStoredLanguage())
  }
}

function requiresAuthButNotLoggedIn(to: any): boolean {
  const employeeJsonStr = localStorage.getItem(Fields.EMPLOYEE)
  const employeeToken = localStorage.getItem(SecurityConstants.TOKEN)

  return (to.meta.requiresAuth && !employeeJsonStr) || (to.meta.requiresAuth && !employeeToken)
}

export default router
