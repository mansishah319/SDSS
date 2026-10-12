/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

/**
 * @author Mohammed Abdu
 * @version vr0.1
 * @createdOn OCT, 2023
 */
export const SecurityConstants = {
  TOKEN: 'sdss_login_token',
  Survey_TOKEN: 'sdss_survey_token',
  BEARER: 'Bearer '
}
export const FailedMessages = {
  AUTHENTICATION_TYPE: 'WWW-Authenticate'
}
export const SuccessMessages = {
  USER_AUTHORITIES: 'User authorities: ',
  GRP_NO: 'grpNo'
}
export const Environment = {
  PRODUCTION: 'production'
}
export const Codes = {
  NOT_FOUND_ERR: {
    message: 'Employee not exist',
    name: '404',
    errorCode: '404'
  }
}
export const Characters = {
  SLASH: '/',
  PERCENTAGE: '%',
  TWO_ZERO: '00',
  EMPTY: '',
  DOT: '.',
  MINUS: '-',
  PLUS: '+',
  UNDER_SCORE: '_',
  QUESTION_MARK: '?',
  EQUAL: '=',
  AND: '&'
}
export const Fields = {
  SET_EMPLOYEE: 'sdssSetEmployee',
  SET_LOGGING: 'setLogging',
  LOGGED_IN_EMP: 'loggedInEmp',
  EMPLOYEE: 'sdss_employee',
  EMPLOYEE_LANG: 'sdssSelectedLanguage',
  EMPLOYEE_LANG_LOCAL_STORAGE: 'selectedLanguageLocalStorage',
  CURRENT_USER_SIDE_BAR_VISIBILITY: 'currentUserSideBarVisibility',
  CURRENT_USER_DASHBOARD_TAB: 'currentUserDashboardTab',
  IS_THE_FIRST_LOGIN: 'employeeFirstLogin',
  MILITARY: 'MILITARY',
  CIVILIAN: 'CIVILIAN',
  THEME: 'theme',
  EXPERTISE_ID: 'expertiseId',
  STRATEGIC_DIRECTION_ID: 'strategicDirectionId',
  STRATEGIC_GOALS_ID: 'strategicGoalsId',
  EXPERT_LEVEL: 'expertLevel',
  AREA_OF_EXPERTISE: 'areaOfExpertise',
  CHALLENGE_SETTINGS_ID: 'challengeSettingsId',
  CHALLENGE_ID: 'challengeId',
  STATUS: 'status',
  INVITATION_STATUS: 'invitationStatus',
  USER_STATUS: 'userStatus',
  GRP_NUMBER: 'grpNumber',
  EMPLOYEE_GRP_NUMBER: 'employeeGrpNumber',
  TEMPLATE_ID: 'templateId',
  SURVEY_ID: 'surveyId',
  SURVEY_ROUND_ID: 'surveyRoundId',
  ROUND_NUMBER: 'roundNumber',
  ROUND_ID: 'roundId',
  REASON: 'reason',
  QUESTION_ID: 'questionId',
  OPTION_ID: 'optionId',
  ACTIVE_ROUND_ID: 'activeRoundId'
}
export const URIS = {
  LOGIN: {
    BASE: 'public/api/v1/core/sdss/login'
  },
  METHODS: {
    GET: 'get',
    POST: 'post',
    DELETE: 'delete',
    UPDATE: 'put'
  },
  SECURITY: {
    AUTHORITIES_URI: 'api/v1/users/authorities',
    AUTH_URI: 'auth/getToken/'
  },
  EXPERTISES: {
    EXPERTISES_ALL_URI: 'api/v1/expertises/all'
  },
  COMMITTEE: {
    BASE: 'api/v1/committee',
    DROPDOWN_LIST: '/dropdown-list'
  },
  ROLES: {
    ROLES_ALL_URI: 'api/v1/roles'
  },
  USER: {
    BASE: 'api/v1/users',
    USER_SEARCH_URI: 'api/v1/users/search',
    BASE_USER_URI: 'api/v1/users',
    DELETE_USER_URI: 'api/v1/users/grp',
    USER_PROFILE_IMAGE_URI: '/profile-img',
    USER_LIST_PAGEABLE_API: 'api/v1/users/pageable',
    USER_EXCEL_DOWNLOAD_URI: 'api/v1/users/export/excel',
    USER_UPDATE_LANG: 'api/v1/users/change-lang',
    USER_RETRIEVE_LANG: 'api/v1/users/user-lang',
    UPDATE_USER_STATUS: '/update-user-status/',
    get UPDATE_MY_PROFILE() {
      return this.BASE + '/update-my-profile'
    },
    get MY_PROFILE() {
      return this.BASE + '/profile'
    }
  },
  DOCUMENTS: {
    BASE: 'api/v1/documents',
    UPLOAD_LIST: '/upload-list',
    UPLOAD_LIST_WITHOUT_CHALLENGE_ID: '/upload-list-without-challenge'
  },
  LOOKUP: {
    BASE: 'api/v1/lookup',
    MY_DASHBOARD: '/user/my-dashboard',
    MY_VALIDATOR_DASHBOARD: '/validator/dashboard',
    get MAIN_HOME_SCREEN_STATISTICS() {
      return this.BASE + '/main-home-screen-statistics'
    },
    get MAIN_SURVEY_HOME_SCREEN_STATISTICS() {
      return this.BASE + '/main-survey-home-screen-statistics'
    },
    USERS: {
      LIST: 'api/v1/lookup/users/list'
    },
    CHALLENGES: {
      CREATION: '/challenges/creation',
      TEAM_MEMBERS: '/challenges/creation/team-members'
    },
    DASHBOARD: '/admin/dashboard'
  },
  CHALLENGES: {
    BASE: 'api/v1/challenges',
    VIEW: '',
    USER_ATTENDEES: '/user-attendees',
    DASHBOARD: '/dashboard',
    SEARCH: '/search',
    MY_CHALLENGES: '/my-challenges',
    STATUS: '/change-status',
    RETRIEVE_BY_STATUS: '/retrieve-by-status?status=',
    SEND_WELCOME_EMAIL: '/send-welcome-voting-email',
    get RETRIEVE_STATUS() {
      return this.BASE + '/status-list'
    },
    DOWNLOAD_PDF_FILE(challengeId: number) {
      return `api/v1/challenges/details-pdf?challengeId=${challengeId}`
    }
  },
  VOTING: {
    BASE: 'api/v1/voting',
    RESULT: '/voting-result?challengeId=',
    CHECK_CURRENT_USER: '/check-vote-for-current-user?solutionId=',
    REMOVE_MY_VOTE: '/remove-my-vote?solutionId='
  },
  EXPERTISE: {
    BASE: 'api/v1/expertises',
    SEARCH: '/search'
  },
  STRATEGIC_DIRECTION: {
    BASE: 'api/v1/strategic/direction',
    ALL: '/all',
    DROPDOWN_LIST: '/dropdown-list',
    ALL_GOALS: '/all-goals'
  },
  STRATEGIC_GOALS: {
    BASE: 'api/v1/strategic/goals',
    ALL: '/all'
  },
  FUTURE_STRATEGIC_DIRECTION: {
    BASE: 'api/v1/strategic/future/direction',
    ALL: '/all-pagination',
    DROPDOWN_LIST: '/all'
  },
  FUTURE_STRATEGIC_GOALS: {
    BASE: 'api/v1/strategic/future/goals',
    ALL: '/all'
  },
  SOLUTIONS: {
    BASE: 'api/v1/solution',
    VIEW: '',
    MERGE: '/merge',
    GET_BY_CHALLENGE_ID: '/get-by-challenge-id',
    SEARCH: '/search',
    UPDATE_ROUND: '/update-round-level'
  },
  CHAT_MESSAGES: {
    BASE: 'api/v1/chat/messages',
    UPLOAD_FILE: '/upload-file',
    VIEW: '',
    GET_BY_CHALLENGE_ID: '/ordered-by-creation-date',
    SEARCH: '/search'
  },
  SOLUTION_COMMENTS: {
    BASE: 'api/v1/solution/comments',
    VIEW: '',
    GET_BY_SOLUTION_ID: '/get-all-by-solution-id',
    SEARCH: '/search'
  },
  CHALLENGE_SETTINGS: {
    BASE: 'api/v1/challenges/settings',
    ALL: '/all'
  },
  USER_INVITATION: {
    BASE: 'api/v1/invitation',
    UPDATE_STATUS: '/update-status',
    INVITE_LIST: '/invite-list'
  },
  CORE: {
    BASE: 'api/v1/core',
    EMPLOYEE_INFO: '/employee',
    EMPLOYEE_RETRIEVE_BY_JOBS_AND_DEPARTMENTS: '/employee/retrieve-by-jobs-and-departments',
    EMPLOYEE_JOBS_BY_DEPARTMENTS: '/employee/organizations/jobs',
    DEPARTMENT_ALL: '/department/all',
    DEPARTMENT_ALL_WITHOUT_RESTRICTIONS: '/department/all-without-restrictions',
    DEPARTMENT_SUB_DEPARTMENT: '/department/sub-departments',
    DEPARTMENT_MULTI_SUB_DEPARTMENT: '/department/multi-sub-departments',
    DEPARTMENT_SECTIONS: '/department/sections'
  },
  VALIDATOR: {
    BASE: 'api/v1/validator',
    UPDATE_STATUS: '/update-status'
  },
  TEMPLATE_TYPE: {
    BASE: 'api/v1/template/types',
    ALL: '/all',
    KEYS: '/keys'
  },
  EMAIL_TEMPLATE: {
    BASE: 'api/v1/email/templates',
    ALL: '/all',
    FIND_BY_ID: '?templateId='
  },
  EMPLOYEE_CORE_PROFILE: {
    BASE: 'api/v1/core/employee/profile',
    IMG: '/img'
  },
  SURVEY: {
    BASE: 'api/v1/survey',
    ALL: '/all',
    BY_SOLUTION: '/by-solution'
  },
  WEB_SOCKET: {
    USER: {
      BASE: 'api/v1/users/presence',
      OFFLINE: '/offline'
    },
    CHALLENGES: {
      BASE: 'api/v1/challenges',
      USER_ATTENDEES: '/user-attendees',
      STATUS_SYNC: '/status-sync'
    }
  },
  YOUTUBE_VIDEOS: {
    BASE: 'api/v1/youtube'
  },
  FILES: {
    BASE: 'api/v1/files',
    get USER_MANUAL() {
      return this.BASE + '/static/user-manual/download'
    }
  },
  SURVEYS: {
    QUESTION_REPORTS: {
      BASE: 'api/v1/question-reports',
      DETAILS_REPORT: (questionId: number) => {
        return `api/v1/question-reports/question-details?questionId=${questionId}`
      }
    },
    SURVEY_REPORTS: {
      BASE: 'api/v1/survey-reports',
      get BY_ROUND() {
        return this.BASE + '/by-round'
      },
      get DOWNLOAD_REPORT_EXCEL_FILE() {
        return this.BASE + '/download'
      },
      DOWNLOAD_REPORT_PDF_FILE(roundId: number) {
        return this.BASE + `/details-pdf?roundId=${roundId}`
      }
    },
    SURVEY: {
      BASE: 'api/v1/sdss-survey',
      SEARCH: '/search',
      IS_USER_ALLOWED: '/is-user-allowed',
      RESEND_INVITATION_EMAIL: '/resend-invitation-email',
      RESEND_MISSING_INVITATION_EMAILS: '/resend-missing-invitation-emails',
      USER_DASHBOARD_UTI: '/public/dashboard',
      get ACTIVE_ROUND_SURVEY() {
        return this.BASE + '/active-round-status'
      },
      get COMPLETED_ROUND_SURVEY() {
        return this.BASE + '/completed-round-status'
      },
      get ACTIVE_PUBLIC_SURVEY() {
        return this.BASE + '/public/retrieve-my-invited-survey'
      }
    },
    SURVEY_QUESTION_ANSWERS: {
      BASE: 'api/v1/public/survey-user-answers',
      get CURRENT_SURVEY_ANSWERS() {
        return this.BASE + '/retrieve-current-answers'
      },
      get CURRENT_SURVEY_QUESTIONS_AND_ANSWERS() {
        return this.BASE + '/retrieve-my-questions-and-answers'
      },
      get RETRIEVE_ALL_DETAILS_FOR_NON_ZERO_PROBABILITY_BY_QUESTION() {
        return this.BASE + '/retrieve-all-details-for-non-zero-probability-by-question'
      },
      get RETRIEVE_ALL_DETAILS_FOR_NON_ZERO_IMPACT_BY_QUESTION() {
        return this.BASE + '/retrieve-all-details-for-non-zero-impact-by-question'
      }
    },
    SURVEY_USER_PROFILE: {
      BASE: 'v1/public/api/v1/invited-user-survey-profile',
      get LIVE_SURVEYS() {
        return this.BASE + '/my-live-surveys'
      },
      get HISTORY_SURVEYS() {
        return this.BASE + '/my-surveys-history'
      }
    },
    ROUND: {
      BASE: 'api/v1/survey-round',
      IS_USER_ALLOWED: '/is-moderator-user',
      get PREVIEW_SURVEY() {
        return this.BASE + '/preview-questions-form'
      },
      get SURVEYS_REPORTS() {
        return this.BASE + '/retrieve-my-completed-surveys-for-reports'
      }
    },
    QUESTIONS: {
      BASE: 'api/v1/survey-questions',
      TYPES: '/question-types'
    },
    OPTIONS: {
      BASE: 'api/v1/questions-options'
    },
    USERS: {
      BASE: 'v1/api/v1/survey-users',
      get LOGIN() {
        return this.BASE + '/public/login-survey-access-key'
      }
    }
  },
  JOBS: {
    BASE: 'api/v1/core/jobs',
    ALL_PAGEABLE: '/all-pageable'
  }
}
