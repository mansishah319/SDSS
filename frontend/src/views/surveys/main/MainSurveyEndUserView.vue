<template lang="html">
  <v-form class="main-survey-end-user-form">
    <!-- Authorization Popup -->
    <v-dialog v-model="dialog" persistent max-width="600">
      <AccessSurveyCard
        :grp-number="grpNumber || ''"
        :access-key="accessKey"
        :password-visible="passwordVisible"
        :is-has-any-role="isHasAnyRole"
        @update:grp-number="grpNumber = $event"
        @update:access-key="accessKey = $event"
        @update:password-visible="passwordVisible = $event"
        @user-not-authorized="userNotAuthorized"
        @user-authorized="authorizedUser"
        @close-popup="handleCloseAccessSurveyCard"
      />
    </v-dialog>

    <v-card v-if="isAuthorized" class="custom-v-card-main-survey-end-user-view">
      <v-card-text class="font-dubai-bold custom-survey-end-user-card-title-view">
        {{ surveyResponse.localTitle }}
      </v-card-text>
      <v-card-subtitle v-show="surveyResponse.localDescription" class="custom-survey-end-user-card-subtitle-view">
        <span class="font-dubai-regular"> {{ surveyResponse.localDescription }} </span>
      </v-card-subtitle>
      <v-container class="custom-main-survey-end-user-view-container">
        <!-- Survey Content dddd -->
        <v-card v-if="isAuthorized" class="mx-auto custom-survey-user-content-style mt-3" max-width="70%" outlined>
          <v-card-title class="text-h6 font-bukra-bold custom-user-survey-title-style">
            {{ activeRoundResponse.localTitle }}
          </v-card-title>
          <v-card-subtitle class="font-bukra-regular custom-end-user-survey-subtitle-style p-0 mb-5">
            {{ $t('sdssSurvey.endUserMessage') }}
          </v-card-subtitle>

          <v-card-text v-if="questionsList.length > 0" class="custom-main-survey-questions-view-container">
            <MainSurveyQuestionsView
              v-if="isAnswersLoaded"
              :questions="questionsList"
              :user-answers="userAnswers"
              @submit-responses="handleSubmitSurveyAnswers"
            ></MainSurveyQuestionsView>
          </v-card-text>
          <v-card-text v-else>
            <EmptyDataTable :image-size="128" :message="$t('sdssSurvey.noQuestionsExistForLive')"></EmptyDataTable>
          </v-card-text>

          <div v-if="!(questionsList.length > 0)" class="custom-questions-and-options-card-back-button align-center">
            <BackButton :redirect-path="backRedirectPath" :icon="backIcon" :color="backColor" />
          </div>
          <v-col cols="12"></v-col>
          <!-- Footer -->
          <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
        </v-card>
      </v-container>
    </v-card>
    <!-- Access Denied View -->
    <AccessDeniedView
      v-else-if="!isAuthorized || hasAttempted"
      back-icon="mdi mdi-home-lightbulb"
      back-title="sdssSurvey.backButton"
      back-color="#008b8b"
      back-redirect-path="/surveys/home"
    ></AccessDeniedView>
  </v-form>
</template>

<script lang="ts">
import { onMounted, ref, watch, watchEffect } from 'vue'
import MainSurveyQuestionsView from '@/views/surveys/main/questions/MainSurveyQuestionsView.vue'
import AccessDeniedView from '@/views/common/AccessDeniedView.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { isHasAnyRole } from '@/utils/roleUtils'
import { Characters, Fields, SecurityConstants, URIS } from '@/constants/DpConstants.ts'
import { createPublicSurvey, getPublicSurveyAuth } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { useRouter } from 'vue-router'
import PublicEndUserSurveyResponse, {
  PublicActiveRoundResponse,
  PublicSurveyQuestion
} from '@/dto/surveys/public/PublicEndUserSurveyResponse'
import { AxiosResponse } from 'axios'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { InvitedUserSingleAnswerRequest } from '@/dto/surveys/answers/InvitedUserSingleAnswerRequest.ts'
import AccessSurveyCard from '@/views/surveys/security/AccessSurvey.vue'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore'
import BackButton from '@/components/common/BackButton.vue'
import { SdssSurveyUserQuestionAnswersResponse } from '@/dto/surveys/answers/SdssSurveyUserQuestionAnswersResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default {
  name: 'MainSurveyEndUserView',
  components: {
    EmptyDataTable,
    BackButton,
    AccessSurveyCard,
    FooterComponent,
    MainSurveyQuestionsView,
    AccessDeniedView
  },
  setup() {
    const surveyTokenStore = useSurveyTokenStore()
    const router = useRouter()
    const { locale } = useI18n()
    const dialog = ref(!isHasAnyRole())
    const accessKey = ref('')
    const grpNumber = ref<number>()

    const isAuthorized = ref(isHasAnyRole())
    const hasAttempted = ref(false)
    const passwordVisible = ref(false)

    const surveyToken = ref(surveyTokenStore.surveyToken || localStorage.getItem(SecurityConstants.Survey_TOKEN))
    const employeeLang: string = window.localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'
    const surveyId = router.currentRoute.value.params.surveyId

    const surveyResponse = ref<PublicEndUserSurveyResponse>({} as PublicEndUserSurveyResponse)
    const activeRoundResponse = ref<PublicActiveRoundResponse>({} as PublicActiveRoundResponse)
    const questionsList = ref<PublicSurveyQuestion[]>([] as PublicSurveyQuestion[])
    const userAnswers = ref<InvitedUserSingleAnswerRequest[]>([] as InvitedUserSingleAnswerRequest[])
    const isAnswersLoaded = ref(false)

    const backRedirectPath = ref<string>('LiveSurveys')
    const backColor = ref<string>('#008b8b')
    const backIcon = ref('mdi mdi-broadcast')
    const backTitle = ref<string>('sdssSurvey.live.backButton')

    watchEffect(() => {
      const dir = employeeLang === 'ar' ? 'rtl' : 'ltr'
      locale.value = employeeLang
      document.documentElement.setAttribute('dir', dir)
      document.body.className = dir
    })

    if (surveyToken.value) {
      authorizedUser()
    } else {
      // You can also handle the absence of a token here.
      userNotAuthorized()
    }

    watch(
      () => surveyTokenStore.surveyToken,
      (newToken: string) => {
        surveyToken.value = newToken
      }
    )

    onMounted(async () => {
      await retrieveCurrentUserAnswers()
      isAnswersLoaded.value = true
    })

    async function handleAuthorizedUserResponseData() {
      if (surveyToken.value) {
        await getPublicSurveyAuth<SuccessResponse<PublicEndUserSurveyResponse>>(
          URIS.SURVEYS.SURVEY.ACTIVE_PUBLIC_SURVEY +
          Characters.QUESTION_MARK +
          Fields.SURVEY_ID +
          Characters.EQUAL +
          surveyId,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<PublicEndUserSurveyResponse>>) => {
          if (response.data.success) {
            const data = response.data.data
            surveyResponse.value = data
            activeRoundResponse.value = data.activeRoundResponse
            questionsList.value = data.activeRoundResponse.questions
          } else {
            router.push({
              name: 'LiveSurveysEmptyScreenPage',
              query: {
                imageSize: 150,
                message: response.data.message,
                backButtonTitle: backTitle.value,
                backButtonColor: backColor.value,
                redirectTo: backRedirectPath.value,
                backButtonIcon: backIcon.value
              }
            })
          }
        })
      } else {
        userNotAuthorized()
      }
    }

    async function retrieveCurrentUserAnswers() {
      if (surveyToken.value) {
        await getPublicSurveyAuth<SuccessResponse<InvitedUserSingleAnswerRequest[]>>(
          URIS.SURVEYS.SURVEY_QUESTION_ANSWERS.CURRENT_SURVEY_ANSWERS +
          Characters.QUESTION_MARK +
          Fields.SURVEY_ID +
          Characters.EQUAL +
          surveyId,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<InvitedUserSingleAnswerRequest[]>>) => {
          if (response.data.success) {
            userAnswers.value = response.data.data
          } else {
            toast.error(response.data.message)
          }
        })
      } else {
        userNotAuthorized()
      }
    }

    const togglePasswordVisibility = () => {
      passwordVisible.value = !passwordVisible.value
    }

    const submitSurvey = () => {
      // Add logic to submit a survey
    }

    const cancelSurvey = () => {
      // Add logic to handle survey cancellation
      if (!isHasAnyRole() && !surveyToken.value) {
        dialog.value = true
      }
    }

    async function authorizedUser(funSurveyToken?: string) {
      if (funSurveyToken) surveyToken.value = funSurveyToken
      isAuthorized.value = true
      hasAttempted.value = false
      dialog.value = false
      await handleAuthorizedUserResponseData()
    }

    function userNotAuthorized() {
      isAuthorized.value = false
      hasAttempted.value = true
      dialog.value = true
    }

    const handleCloseAccessSurveyCard = () => {
      isAuthorized.value = false
      hasAttempted.value = true
      dialog.value = false
    }

    const handleSubmitSurveyAnswers = async (request: InvitedUserSingleAnswerRequest[]) => {
      if (surveyToken.value) {
        await createPublicSurvey<SuccessResponse<SdssSurveyUserQuestionAnswersResponse>>(
          URIS.SURVEYS.SURVEY_QUESTION_ANSWERS.BASE,
          request,
          employeeLang
        ).then((response: AxiosResponse<SuccessResponse<SdssSurveyUserQuestionAnswersResponse>>) => {
          if (response.data.success) {
            const data = response.data.data
            console.log(data)
            router.push({ name: 'SurveySuccessPage' })
          } else {
            toast.error(response.data.message)
          }
        })
      } else {
        userNotAuthorized()
      }
    }

    return {
      dialog,
      backIcon,
      accessKey,
      backColor,
      userAnswers,
      isAuthorized,
      hasAttempted,
      passwordVisible,
      backRedirectPath,
      togglePasswordVisibility,
      submitSurvey,
      cancelSurvey,
      isHasAnyRole,
      grpNumber,
      surveyResponse,
      questionsList,
      activeRoundResponse,
      handleSubmitSurveyAnswers,
      surveyId,
      isAnswersLoaded,
      userNotAuthorized,
      authorizedUser,
      handleCloseAccessSurveyCard
    }
  }
}
</script>

<style scoped src="./MainSurveyEndUserView.scss" lang="scss"></style>
