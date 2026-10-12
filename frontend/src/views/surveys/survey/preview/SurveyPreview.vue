<template lang="html">
  <v-form class="main-survey-end-user-form">
    <v-card class="custom-v-card-main-survey-end-user-view">
      <v-card-text class="font-dubai-bold custom-survey-preview-card-title-view">
        {{ surveyResponse.localTitle }}
      </v-card-text>
      <v-card-subtitle
        v-show="surveyResponse.localDescription"
        class="font-dubai-regular custom-survey-preview-card-subtitle-view"
      >
        <span class="font-dubai-regular"> {{ surveyResponse.localDescription }} </span>
      </v-card-subtitle>
      <v-container class="custom-survey-preview-card-view-container">
        <!-- Survey Content-->
        <v-card class="mx-auto custom-survey-preview-content-style my-5" max-width="80%" outlined>
          <v-card-title class="text-h6 font-bukra-bold custom-survey-preview-content-title-style">
            {{ activeRoundResponse.localTitle }}
          </v-card-title>
          <v-card-subtitle class="font-bukra-regular custom-survey-preview-content-subtitle-style">
            {{ $t('sdssSurvey.endUserMessage') }}
          </v-card-subtitle>
          <v-divider></v-divider>
          <v-card-text class="custom-main-survey-questions-view-container">
            <MainSurveyQuestionsView
              :questions="questionsList"
              :is-read-only="true"
              :show-action-buttons="false"
            ></MainSurveyQuestionsView>
          </v-card-text>

          <v-col cols="12"></v-col>
          <!-- Footer -->
          <FooterComponent :justify-content="'justify-content-start'"></FooterComponent>
        </v-card>
      </v-container>
    </v-card>
  </v-form>
</template>

<script lang="ts">
import { onMounted, ref } from 'vue'
import MainSurveyQuestionsView from '@/views/surveys/main/questions/MainSurveyQuestionsView.vue'
import FooterComponent from '@/components/common/FooterComponent.vue'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { get } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import PublicEndUserSurveyResponse, {
  PublicActiveRoundResponse,
  PublicSurveyQuestion
} from '@/dto/surveys/public/PublicEndUserSurveyResponse'
import { toast } from 'vue3-toastify'

export default {
  name: 'SurveyPreview',
  components: {
    FooterComponent,
    MainSurveyQuestionsView
  },
  props: {
    roundId: {
      type: Number,
      required: true
    }
  },
  setup(props) {
    const surveyRoundId = ref<number>(props.roundId)
    const surveyResponse = ref<PublicEndUserSurveyResponse>({} as PublicEndUserSurveyResponse)
    const activeRoundResponse = ref<PublicActiveRoundResponse>({} as PublicActiveRoundResponse)
    const questionsList = ref<PublicSurveyQuestion[]>([] as PublicSurveyQuestion[])

    async function loadData() {
      await get<SuccessResponse<PublicEndUserSurveyResponse>>(
        URIS.SURVEYS.ROUND.PREVIEW_SURVEY +
        Characters.QUESTION_MARK +
        Fields.SURVEY_ROUND_ID +
        Characters.EQUAL +
        surveyRoundId.value
      ).then((response: SuccessResponse<PublicEndUserSurveyResponse>) => {
        if (response.success) {
          const data = response.data
          surveyResponse.value = data
          activeRoundResponse.value = data.activeRoundResponse
          questionsList.value = data.activeRoundResponse.questions
        } else {
          toast.error(response.message)
        }
      })
    }

    onMounted(async () => {
      await loadData()
    })

    return {
      surveyResponse,
      questionsList,
      activeRoundResponse
    }
  }
}
</script>

<style scoped src="./SurveyPreview.scss"></style>
