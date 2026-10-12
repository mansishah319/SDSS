<template>
  <v-container :readonly="isReadOnly" class="custom-main-survey-questions-view-container">
    <div v-for="question in questions" :key="question.id" class="question-ans-div mb-4">
      <v-col cols="12" class="p-0">
        <v-card>
          <v-card-title class="font-dubai-bold custom-end-user-questions-and-options-card-title p-0 mb-2">
            1&nbsp; {{ question.localTitle }}
          </v-card-title>
          <v-card-subtitle
            v-if="question.localDescription"
            class="custom-end-user-questions-and-options-card-subtitle mb-2"
          >
            {{ question.localDescription }}
          </v-card-subtitle>
          <v-card-text class="font-bukra-regular">
            <v-form ref="questionForms" class="font-bukra-regular" :readonly="isReadOnly">
              <template v-if="question.questionTypeKey === 'multiple-choice'">
                <!-- Generate Checkboxes for multiple-choice questions -->
                <v-checkbox
                  v-for="option in question.options"
                  :key="option.id"
                  v-model="userResponses[question.id].optionIds"
                  :value="option.id"
                  :label="option.localOption"
                  :hide-details="true"
                  class="custom-end-user-survey-multiple-choice-question-checkbox p-0"
                ></v-checkbox>
              </template>

              <template v-else-if="question.questionTypeKey === 'single-choice'">
                <v-autocomplete
                  :model-value="userResponses[question.id]?.optionIds?.[0] ?? null"
                  :items="question.options || []"
                  item-title="localOption"
                  item-value="id"
                  :label="$t('common.select')"
                  :placeholder="$t('common.search')"
                  prepend-inner-icon="mdi-magnify"
                  variant="outlined"
                  clearable
                  hide-details
                  class="custom-end-user-survey-single-choice-question-autocomplete"
                  @update:model-value="setSingleChoiceResponse(question.id, $event)"
                ></v-autocomplete>
              </template>

              <template v-else-if="question.questionTypeKey === 'text'">
                <v-textarea
                  v-model="userResponses[question.id].responseText"
                  :label="$t('common.insertYourAnswer')"
                  variant="outlined"
                  auto-grow
                  :maxlength="TEXT_ANSWER_MAX_LENGTH"
                  :counter="TEXT_ANSWER_MAX_LENGTH"
                  :rules="textAnswerRules"
                  class="custom-end-user-survey-text-question-text-area p-0"
                ></v-textarea>
              </template>

              <template v-else-if="question.questionTypeKey === 'true-false'">
                <v-radio-group
                  v-model="userResponses[question.id].correct"
                  class="custom-end-user-survey-true-false-question-radio-group p-0"
                >
                  <v-radio :label="$t('common.yes')" :value="true"></v-radio>
                  <v-radio :label="$t('common.no')" :value="false"></v-radio>
                </v-radio-group>
              </template>
            </v-form>

            <v-form
              ref="publicQuestionsForm"
              v-if="question.showPublicQuestions"
              v-show="question.showPublicQuestions"
              :readonly="isReadOnly"
              class=""
            >
              <v-card class="custom-end-user-questions-and-options-public-additional-questions-card">
                <v-card-title
                  class="custom-end-user-questions-and-options-public-additional-questions-card-title font-dubai-bold p-0"
                >
                  {{ $t('sdssSurvey.surveyRounds.questions.publicQuestions.mainTitle') }}
                </v-card-title>
                <v-card-text class="mb-0 p-0">
                  <v-row>
                    <v-col cols="6" md="6" class="text-center">
                      <v-text-field
                        v-model.number="userResponses[question.id].probabilityOfOccurrence"
                        type="number"
                        min="0"
                        max="100"
                        step="any"
                        :rules="percentageRules"
                        density="compact"
                        variant="outlined"
                        :placeholder="
                          $t('sdssSurvey.surveyRounds.questions.publicQuestions.probabilityOfOccurrenceQuestion') +
                          ' (%)'
                        "
                        :label="
                          $t('sdssSurvey.surveyRounds.questions.publicQuestions.probabilityOfOccurrenceQuestion') +
                          ' (%)'
                        "
                      />
                    </v-col>
                    <v-col cols="6" md="6" class="text-center">
                      <v-text-field
                        v-model.number="userResponses[question.id].impactSeverity"
                        type="number"
                        min="0"
                        max="100"
                        step="any"
                        :rules="percentageRules"
                        density="compact"
                        variant="outlined"
                        :placeholder="$t('sdssSurvey.surveyRounds.questions.publicQuestions.impactSeverity') + ' (%)'"
                        :label="$t('sdssSurvey.surveyRounds.questions.publicQuestions.impactSeverity') + ' (%)'"
                      />
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </div>

    <v-card-actions v-if="showActionButtons">
      <v-spacer></v-spacer>
      <v-btn color="green darken-1" text class="dp-primary-sm-btn" @click="submitSurvey">
        {{ $t('common.submit') }}
      </v-btn>
      <v-btn text class="border-sm-btn" @click="cancelSurvey">
        {{ $t('common.cancel') }}
      </v-btn>
    </v-card-actions>
  </v-container>
</template>

<script lang="ts">
import { PropType, ref, watch } from 'vue'
import { PublicSurveyQuestion } from '@/dto/surveys/public/PublicEndUserSurveyResponse.ts'
import { InvitedUserSingleAnswerRequest } from '@/dto/surveys/answers/InvitedUserSingleAnswerRequest.ts'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

export default {
  props: {
    questions: {
      type: Array as PropType<PublicSurveyQuestion[]>,
      required: false,
      default: () => [] as PublicSurveyQuestion[]
    },
    showActionButtons: {
      type: Boolean,
      default: true
    },
    isReadOnly: {
      type: Boolean,
      default: false
    },
    userAnswers: {
      type: Array as PropType<InvitedUserSingleAnswerRequest[]>,
      required: false,
      default: () => [] as InvitedUserSingleAnswerRequest[]
    }
  },
  emits: ['submit-responses'],
  setup(props, { emit }) {
    const router = useRouter()
    const { t } = useI18n()
    const TEXT_ANSWER_MAX_LENGTH = 1000
    const userResponses = ref<{ [key: number]: InvitedUserSingleAnswerRequest }>({})
    const questionForms = ref<any[] | any>([])
    const publicQuestionsForm = ref<any[] | any>([])
    const textAnswerRules = [
      (value: unknown) =>
        !value || String(value).length <= TEXT_ANSWER_MAX_LENGTH || t('validation.maxLength', { max: TEXT_ANSWER_MAX_LENGTH })
    ]
    const percentageRules = [
      (value: unknown) =>
        value === null ||
        value === undefined ||
        value === '' ||
        (Number.isFinite(Number(value)) && Number(value) >= 0 && Number(value) <= 100) ||
        t('validation.percentageRange')
    ]

    // Initialize userResponses
    watch(
      () => props.questions,
      (newQuestions) => {
        newQuestions.forEach((question) => {
          // Initialize a response object with empty data
          userResponses.value[question.id] = {
            questionId: question.id,
            optionIds: [],
            responseText: '',
            correct: false,
            probabilityOfOccurrence: null,
            impactSeverity: null
          }
        })

        // Pre-populate userResponses with the provided userAnswers if available
        props.userAnswers.forEach((answer) => {
          const response = userResponses.value[answer.questionId]
          if (response) {
            response.optionIds = answer.optionIds || []
            response.responseText = answer.responseText || ''
            response.correct = answer.correct
            response.probabilityOfOccurrence = answer.probabilityOfOccurrence
            response.impactSeverity = answer.impactSeverity
          }
        })
      },
      { immediate: true }
    )

    const onMultipleChoiceChange = (questionId: number, optionId: number, isChecked: boolean) => {
      const response = userResponses.value[questionId]
      response.optionIds ||= []
      if (isChecked) {
        if (!response.optionIds.includes(optionId)) {
          response.optionIds.push(optionId)
        }
      } else {
        response.optionIds = response.optionIds.filter((id) => id !== optionId)
      }
    }

    const setSingleChoiceResponse = (questionId: number, optionId: unknown) => {
      const response = userResponses.value[questionId]
      if (!response) return

      response.optionIds = optionId === null || optionId === undefined ? [] : [Number(optionId)]
    }

    const validateForms = async (formRefs: any[] | any) => {
      const forms = Array.isArray(formRefs) ? formRefs : [formRefs].filter(Boolean)
      const validationResults = await Promise.all(forms.map((form) => form.validate()))
      return validationResults.every((result) => result.valid)
    }

    const submitSurvey = async () => {
      const areQuestionFormsValid = await validateForms(questionForms.value)
      const arePublicQuestionFormsValid = await validateForms(publicQuestionsForm.value)
      if (!areQuestionFormsValid || !arePublicQuestionFormsValid) return

      const answers = props.questions.map((question) => {
        const response = userResponses.value[question.id]
        return {
          questionId: response.questionId,
          optionIds: response.optionIds || [],
          responseText: question.questionTypeKey === 'text' ? response.responseText || '' : '',
          correct: response.correct ?? null,
          probabilityOfOccurrence: response.probabilityOfOccurrence,
          impactSeverity: response.impactSeverity
        }
      })
      emit('submit-responses', answers)
    }

    const cancelSurvey = () => {
      router.push({ name: 'LiveSurveys' })
    }

    return {
      TEXT_ANSWER_MAX_LENGTH,
      userResponses,
      questionForms,
      publicQuestionsForm,
      textAnswerRules,
      percentageRules,
      onMultipleChoiceChange,
      setSingleChoiceResponse,
      submitSurvey,
      cancelSurvey
    }
  }
}
</script>
<style src="./MainSurveyQuestionsView.scss" lang="scss"></style>
