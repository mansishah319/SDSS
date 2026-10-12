<template>
  <v-container>
    <v-row class="mb-3">
      <v-col cols="2">
        <v-btn icon class="custom-history-view-user-answers-main-back-button" @click="goBack">
          <v-icon>{{ employeeLang === 'ar' ? 'mdi-arrow-right' : 'mdi-arrow-left' }}</v-icon>
        </v-btn>
      </v-col>

      <v-col cols="8">
        <h1 class="custom-history-view-user-answers-main-title-h1 d-inline-block font-dubai-bold">
          {{ $t('sdssSurvey.history.surveyDetails.main-title') }}
        </h1>
      </v-col>
      <v-col cols="2"></v-col>
    </v-row>

    <!-- Survey Details Card -->
    <v-card variant="outlined" class="custom-user-questions-and-answers-history-view-main-card">
      <v-card-title class="custom-user-questions-and-answers-history-view-card-title font-bukra-bold">
        {{ $t('sdssSurvey.history.surveyDetails.title') }}
      </v-card-title>
      <v-card-text class="custom-user-questions-and-answers-history-view-card-text justify-space-between">
        <v-row>
          <v-col cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.currentUserGrpNumber') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.invitedUserResponse.employeeGrpNumber }}
              </span>
            </v-col>
          </v-col>

          <v-col cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.currentUserName') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.invitedUserResponse.employeeInfo.fullName }}
              </span>
            </v-col>
          </v-col>

          <v-col cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.yourPrimaryEmail') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.invitedUserResponse.employeeInfo.emailAddress }}
              </span>
            </v-col>
          </v-col>
        </v-row>
        <v-divider :thickness="2" color="info"></v-divider>
        <v-row>
          <v-col cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.surveyTitle') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.localTitle }}
              </span>
            </v-col>
          </v-col>
          <v-col v-show="survey.localDescription" cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.surveyDescription') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">{{ survey.localDescription }}</span>
            </v-col>
          </v-col>
          <v-col cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.roundTitle') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.activeRoundResponse.localTitle }}
              </span>
            </v-col>
          </v-col>
          <v-col v-show="survey.activeRoundResponse.localDescription" cols="3">
            <v-col cols="12" class="pb-0">
              <span class="font-bukra-bold">
                <strong>{{ $t('sdssSurvey.history.surveyDetails.roundDescription') }}</strong>
              </span>
            </v-col>
            <v-col cols="12">
              <span class="font-dubai-regular">
                {{ survey.activeRoundResponse.localDescription }}
              </span>
            </v-col>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <v-row class="mt-3">
      <v-col v-for="userResponse in userQuestionAnswersResponse" :key="userResponse.id" cols="12" lg="4" sm="4" md="4">
        <v-card variant="outlined" class="custom-history-view-user-answers-card flex-column">
          <v-card-title class="custom-history-view-user-answers-card-title font-bukra-bold">
            {{ userResponse.question.localTitle }}
          </v-card-title>
          <v-card-subtitle class="custom-history-view-user-answers-card-sub-title font-bukra-regular">
            <span class="custom-history-view-user-answers-card-sub-title-span">
              {{ userResponse.question.localDescription }}
            </span>
          </v-card-subtitle>
          <v-card-text class="custom-history-view-user-answers-card-text flex-grow-1 font-bukra-regular">
            <!-- Multi-Select and Single-Choice -->
            <template
              v-if="
                Array.isArray(userResponse.selectedOptions) &&
                (userResponse.question?.questionTypeKey.trim().toLowerCase() === 'multiple-choice' ||
                  userResponse.question?.questionTypeKey.trim().toLowerCase() === 'single-choice')
              "
            >
              <v-row class="pa-2 custom-history-view-user-answers-chips-container">
                <v-chip
                  v-for="response in userResponse.selectedOptions"
                  :key="response.id"
                  class="ma-1 custom-history-view-user-answers-v-chip font-dubai-regular"
                  outlined
                >
                  {{ response.localOption }}
                </v-chip>
              </v-row>
            </template>
            <!-- Text Answers -->
            <template v-else-if="userResponse.question?.questionTypeKey.trim().toLowerCase() === 'text'">
              <p class="custom-history-view-user-answers-scrollable-text font-dubai-regular">
                {{ userResponse.responseText }}
              </p>
            </template>
            <!-- True/False -->
            <template v-else-if="userResponse.question?.questionTypeKey.trim().toLowerCase() === 'true-false'">
              <p v-if="userResponse.correct" class="text-success font-dubai-regular"> ✅ {{ $t('common.yes') }} </p>
              <p v-else class="text-error font-dubai-regular"> ❌ {{ $t('common.no') }} </p>
            </template>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { onMounted, PropType, ref, watchEffect } from 'vue'
import PublicEndUserSurveyResponse from '@/dto/surveys/public/PublicEndUserSurveyResponse.js'
import { getPublicSurveyAuth } from '@/utils/GenericApiHelper.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { Characters, Fields, SecurityConstants, URIS } from '@/constants/DpConstants.ts'
import { AxiosResponse } from 'axios'
import { toast } from 'vue3-toastify'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore.ts'
import { useI18n } from 'vue-i18n'
import { SdssSurveyUserQuestionAnswersResponse } from '@/dto/surveys/answers/SdssSurveyUserQuestionAnswersResponse.ts'

export default {
  name: 'SurveyDetails',
  props: {
    survey: {
      type: Object as PropType<PublicEndUserSurveyResponse>,
      required: true
    }
  },
  emits: ['go-back'],
  setup(props) {
    const { locale } = useI18n()

    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    const isAuthorized = ref<boolean>(false)
    const surveyTokenStore = useSurveyTokenStore()
    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))

    const currentSurvey = ref<PublicEndUserSurveyResponse>(props.survey)
    const userQuestionAnswersResponse = ref<SdssSurveyUserQuestionAnswersResponse[]>(
      [] as SdssSurveyUserQuestionAnswersResponse[]
    )

    console.log(currentSurvey.value.activeRoundResponse.id)

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    async function retrieveCurrentUserAnswers() {
      if (surveyToken.value) {
        await getPublicSurveyAuth<SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>>(
          URIS.SURVEYS.SURVEY_QUESTION_ANSWERS.CURRENT_SURVEY_QUESTIONS_AND_ANSWERS +
          Characters.QUESTION_MARK +
          Fields.ROUND_ID +
          Characters.EQUAL +
          currentSurvey.value.activeRoundResponse.id,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<SdssSurveyUserQuestionAnswersResponse[]>>) => {
          if (response.data.success) {
            userQuestionAnswersResponse.value = response.data.data as SdssSurveyUserQuestionAnswersResponse[]
            authorizedUser()
          } else {
            toast.error(response.data.message)
          }
        })
      } else {
        // await router.push({ name: 'MainUsersSurveyHomeScreen' })
        userNotAuthorized()
      }
    }

    function userNotAuthorized() {
      isAuthorized.value = false
    }

    function authorizedUser() {
      isAuthorized.value = true
    }

    onMounted(async () => {
      await retrieveCurrentUserAnswers()
    })

    return {
      userQuestionAnswersResponse,
      employeeLang
    }
  },
  methods: {
    goBack() {
      this.$emit('go-back')
    },
    statusClass(status: any) {
      return (
        {
          COMPLETED: 'text-success',
          PENDING: 'text-warning',
          FAILED: 'text-error'
        }[status] || 'text-muted'
      )
    }
  }
}
</script>
<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.custom-user-questions-and-answers-history-view-card-title {
  background-color: #0da06e;
  color: $white-color;
}

.custom-history-view-user-answers-card {
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(30vh);
  min-height: 100px;
  max-height: 200px;
  background-color: #fff;
}

.custom-history-view-user-answers-card-title {
  background-color: #0da06e;
  color: $white-color;
  font-size: 1rem;
  overflow-wrap: break-word;
  text-overflow: ellipsis;
  white-space: normal;
}

.custom-history-view-user-answers-card-sub-title {
  background-color: $sdss-description-color;
  font-style: italic;
}

.rtl .custom-history-view-user-answers-card-sub-title-span {
  padding-right: 2%;
}

.ltr .custom-history-view-user-answers-card-sub-title-span {
  padding-right: 2%;
}

.custom-history-view-user-answers-card-text {
  align-self: center;
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.custom-history-view-user-answers-chips-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-history-view-user-answers-v-chip {
  width: 100%;
  white-space: normal;
  word-wrap: break-word;
  overflow: hidden;
  word-break: break-word;
  padding: 8px;
}

.custom-history-view-user-answers-scrollable-text {
  max-height: 150px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

@media (max-width: 768px) {
  .custom-history-view-user-answers-card {
    margin-bottom: 16px;
  }
}

.custom-history-view-user-answers-main-title-h1 {
  font-size: 1.5rem;
  font-family: bukrabold;
  text-align: center;
  width: 100%;
}

.custom-user-questions-and-answers-history-view-card-text {
  padding-top: 1px;
  background-color: #fff;
}

.custom-user-questions-and-answers-history-view-main-card {
  border-radius: 12px;
}
</style>
