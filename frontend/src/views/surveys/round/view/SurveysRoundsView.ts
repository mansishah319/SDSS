import { defineComponent, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import SdssOptionCardView from '@/views/surveys/round/view/options/SdssOptionCardView.vue'
import { create, deleteRequest, get, update } from '@/utils/GenericApiHelper.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import {
  OptionResponse,
  QuestionResponse,
  SdssSurveyRoundResponse
} from '@/dto/surveys/round/SdssSurveyRoundResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import QuestionForm from '@/views/surveys/questions/QuestionForm.vue'
import { QuestionTypesDropDownList } from '@/dto/generic/BaseDropDownList'
import { SurveyQuestionRequest, UpdateSurveyQuestionRequest } from '@/dto/surveys/question/SdssSurveyQuestionRequest'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { SdssSurveyQuestionResponse } from '@/dto/surveys/question/SdssSurveyQuestionResponse'
import ConfirmationDialog from '@/components/common/ConfirmationDialog.vue'
import QuestionOptionsForm from '@/views/surveys/questionOptions/QuestionOptionsForm.vue'
import { QuestionOptionsRequest } from '@/dto/surveys/options/QuestionOptionsRequest'
import { SdssSurveyQuestionOptionsResponse } from '@/dto/surveys/options/SdssSurveyQuestionOptionsResponse'
import SurveyPreview from '@/views/surveys/survey/preview/SurveyPreview.vue'
import CustomGoBackButton from '@/components/custom/CustomGoBackButton.vue'
import AppDialogShell from '@/components/common/AppDialogShell.vue'

export default defineComponent({
  name: 'SurveyRoundView',
  components: {
    EmptyDataTable,
    OptionCard: SdssOptionCardView,
    QuestionForm,
    ConfirmationDialog,
    QuestionOptionsForm,
    SurveyPreview,
    CustomGoBackButton,
    AppDialogShell
  },
  setup() {
    const { t, locale } = useI18n()
    const route = useRoute()
    const params = route.params
    const roundId = Number(params.roundId)
    const surveyId = Number(params.surveyId)

    const employeeLang: string = (window.localStorage.getItem(Fields.EMPLOYEE_LANG) ?? locale.value) || 'ar'

    const round = ref<SdssSurveyRoundResponse>({} as SdssSurveyRoundResponse)

    const questions = ref<QuestionResponse[]>([] as QuestionResponse[])
    const selectedSingleChoiceOptionIds = ref<Record<number, number | null>>({})

    const dialog = ref<boolean>(false)
    const showPreviewDialog = ref<boolean>(false)
    const updateDialog = ref<boolean>(false)
    const selectedUpdateQuestionId = ref<number>(0)
    const isEditing = ref<boolean>(false)

    const deleteQuestionDialog = ref<boolean>(false)
    const selectedQuestionIdToDelete = ref<number>(0)

    const currentQuestion = ref<QuestionResponse>({} as QuestionResponse)
    const currentUpdateQuestionRequest = ref<UpdateSurveyQuestionRequest>({} as UpdateSurveyQuestionRequest)

    const newOptionText = ref<string>('')
    const questionTypes = ref<QuestionTypesDropDownList[]>([] as QuestionTypesDropDownList[])

    // Handle Options Dialog
    const showCreateNewOptionDialog = ref<boolean>(false)
    const selectedQuestionIdForOptionCreation = ref<number>(0)

    const showUpdateOptionDialog = ref<boolean>(false)
    const selectedOptionIdForUpdateOrRemove = ref<number>(0)
    const optionsRequestForUpdate = ref<QuestionOptionsRequest>({} as QuestionOptionsRequest)
    const deleteOptionDialog = ref<boolean>(false)
    const selectedOptionIdToDelete = ref<number>(0)

    const showAddQuestionDialog = () => {
      isEditing.value = false
      currentQuestion.value = {} as QuestionResponse
      dialog.value = true
    }

    const isLanguageAr = (): boolean => {
      return employeeLang === 'ar'
    }

    const isModeratorAndAllowedToUpdateOrDelete = () => {
      return round.value.currentUserIsTheSurveyModerator && round.value.isAllowedToUpdateOrDelete
    }

    const showPreviewScreenDialog = () => {
      showPreviewDialog.value = true
    }

    const questionTypesColors = reactive({
      'single-choice': '#8b4513',
      'multiple-choice': '#0000cd',
      'true-false': '#800080',
      text: '#000'
    })

    const getQuestionTypeColor = (questionTypeKey: string) => {
      // Return the color based on questionTypeKey, default to 'gray' if not found
      return questionTypesColors[questionTypeKey] ?? 'grey'
    }

    const loadData = async () => {
      try {
        await get<SuccessResponse<SdssSurveyRoundResponse>>(URIS.SURVEYS.ROUND.BASE + '?surveyRoundId=' + roundId).then(
          (res) => {
            if (res?.success) {
              round.value = res.data
              questions.value = round.value.questions as QuestionResponse[]
              questions.value.forEach((question) => {
                if (question.questionTypeKey !== 'single-choice') return

                const selectedOptionId = selectedSingleChoiceOptionIds.value[question.id]
                const optionStillExists = question.options?.some((option) => option.id === selectedOptionId)
                selectedSingleChoiceOptionIds.value[question.id] = optionStillExists ? selectedOptionId : null
              })
            } else {
              console.error('API request was not successful:', res?.message)
              // Handle error scenario
            }
          }
        )
      } catch (error) {
        console.error('Error loading data:', error)
        // Handle error scenario
      }
    }

    const retrieveAllQuestionTypes = async () => {
      await get<SuccessResponse<QuestionTypesDropDownList[]>>(
        URIS.SURVEYS.QUESTIONS.BASE + URIS.SURVEYS.QUESTIONS.TYPES
      ).then((res) => {
        if (res?.success) {
          questionTypes.value = res.data
        } else {
          console.error('API request was not successful:', res?.message)
          // Handle error scenario
        }
      })
    }

    const editQuestion = (question: QuestionResponse) => {
      console.log('edit question data')
      isEditing.value = true
      selectedUpdateQuestionId.value = question.id
      currentUpdateQuestionRequest.value = {
        titleEn: question.localTitle ?? '',
        titleAr: question.localTitle ?? '',
        descriptionEn: question.localDescription ?? null,
        descriptionAr: question.localDescription ?? null,
        questionType: question.questionTypeKey ?? 'text',
        showPublicQuestions: question.showPublicQuestions || false
      } as UpdateSurveyQuestionRequest
      updateDialog.value = true
    }

    const saveQuestion = async (question: SurveyQuestionRequest) => {
      question.roundId = roundId
      question.surveyId = surveyId
      await create(URIS.SURVEYS.QUESTIONS.BASE, question).then(
        async (res: SuccessResponse<SdssSurveyQuestionResponse>) => {
          if (res.success) {
            toast.success(t('Messages.success.SurveyRoundQuestion.created'))
            await loadData()
          } else {
            toast.error(res.message)
          }
        }
      )
      dialog.value = false
    }

    const updateQuestion = async (question: UpdateSurveyQuestionRequest) => {
      await update(URIS.SURVEYS.QUESTIONS.BASE + '?questionId=' + selectedUpdateQuestionId.value, {
        titleEn: question.titleEn || '',
        titleAr: question.titleAr || '',
        descriptionEn: question.descriptionEn ?? null,
        descriptionAr: question.descriptionAr ?? null,
        questionType: question.questionType || 'text',
        showPublicQuestions: question.showPublicQuestions || false
      }).then(async (res: SuccessResponse<SdssSurveyQuestionResponse>) => {
        if (res.success) {
          toast.success(t('Messages.success.SurveyRoundQuestion.updated'))
          await loadData()
        } else {
          toast.error(res.message)
        }
      })
      handleCancellationUpdate()
    }

    const handleCancellation = () => {
      dialog.value = false
    }

    const handleCancellationUpdate = () => {
      currentUpdateQuestionRequest.value = {} as UpdateSurveyQuestionRequest
      selectedUpdateQuestionId.value = 0
      updateDialog.value = false
    }

    const deleteQuestion = (id: number) => {
      selectedQuestionIdToDelete.value = id
      deleteQuestionDialog.value = true
    }

    const confirmQuestionDeletion = async () => {
      await deleteRequest<SuccessResponse<string>>(
        URIS.SURVEYS.QUESTIONS.BASE +
        Characters.QUESTION_MARK +
        Fields.QUESTION_ID +
        Characters.EQUAL +
        selectedQuestionIdToDelete.value
      ).then(async (res: SuccessResponse<string>) => {
        if (res.success) {
          toast.success(t('Messages.success.SurveyRoundQuestion.deleted'))
          await loadData()
          cancelQuestionDeletion()
        } else {
          toast.error(res.message)
        }
      })
    }

    const cancelQuestionDeletion = () => {
      selectedQuestionIdToDelete.value = 0
      deleteQuestionDialog.value = false
    }

    const addOption = (questionId: number) => {
      selectedQuestionIdForOptionCreation.value = questionId
      showCreateNewOptionDialog.value = true
    }

    const handleCloseCreationOptionActionView = () => {
      showCreateNewOptionDialog.value = false
    }

    const handleUpdateOrRemoveOptionActionView = () => {
      selectedOptionIdForUpdateOrRemove.value = 0
      optionsRequestForUpdate.value = {} as QuestionOptionsRequest
      showUpdateOptionDialog.value = false
    }

    const handleCreateNewOption = async (request: QuestionOptionsRequest) => {
      await create(
        URIS.SURVEYS.OPTIONS.BASE +
        Characters.QUESTION_MARK +
        Fields.QUESTION_ID +
        Characters.EQUAL +
        selectedQuestionIdForOptionCreation.value,
        request
      ).then(async (res: SuccessResponse<SdssSurveyQuestionOptionsResponse>) => {
        if (res.success) {
          toast.success(t('Messages.success.questionOptions.created'))
          await loadData()
        } else {
          toast.error(res.message)
        }
      })
      handleCloseCreationOptionActionView()
    }

    const handleUpdateOption = async (request: QuestionOptionsRequest) => {
      await update(
        URIS.SURVEYS.OPTIONS.BASE +
        Characters.QUESTION_MARK +
        Fields.OPTION_ID +
        Characters.EQUAL +
        selectedOptionIdForUpdateOrRemove.value,
        request
      ).then(async (res: SuccessResponse<SdssSurveyQuestionOptionsResponse>) => {
        if (res.success) {
          toast.success(t('Messages.success.questionOptions.created'))
          await loadData()
        } else {
          toast.error(res.message)
        }
      })
      handleUpdateOrRemoveOptionActionView()
    }

    const removeOption = (option: OptionResponse) => {
      selectedOptionIdToDelete.value = option.id
      deleteOptionDialog.value = true
    }

    const openEditOptionDialog = (option: OptionResponse) => {
      selectedOptionIdForUpdateOrRemove.value = option.id
      optionsRequestForUpdate.value = {
        optionEn: option.optionEn,
        optionAr: option.optionAr
      } as QuestionOptionsRequest
      showUpdateOptionDialog.value = true
    }

    const getSelectedSingleChoiceOption = (question: QuestionResponse): OptionResponse | undefined => {
      const selectedOptionId = selectedSingleChoiceOptionIds.value[question.id]
      return question.options?.find((option) => option.id === selectedOptionId)
    }

    const editSelectedSingleChoiceOption = (question: QuestionResponse) => {
      const option = getSelectedSingleChoiceOption(question)
      if (option) openEditOptionDialog(option)
    }

    const removeSelectedSingleChoiceOption = (question: QuestionResponse) => {
      const option = getSelectedSingleChoiceOption(question)
      if (option) removeOption(option)
    }

    const handlePreviewScreenDialogClose = () => {
      showPreviewDialog.value = false
    }

    const cancelOptionDeletion = () => {
      selectedOptionIdToDelete.value = 0
      deleteOptionDialog.value = false
    }

    const confirmOptionDeletion = async () => {
      await deleteRequest<SuccessResponse<string>>(
        URIS.SURVEYS.OPTIONS.BASE +
        Characters.QUESTION_MARK +
        Fields.OPTION_ID +
        Characters.EQUAL +
        selectedOptionIdToDelete.value
      ).then(async (res: SuccessResponse<string>) => {
        if (res.success) {
          toast.success(t('Messages.success.questionOptions.deleted'))
          await loadData()
          cancelOptionDeletion()
        } else {
          toast.error(res.message)
        }
      })
    }

    onMounted(async () => {
      await loadData()
      await retrieveAllQuestionTypes()
    })

    return {
      round,
      roundId,
      questions,
      selectedSingleChoiceOptionIds,
      dialog,
      isEditing,
      currentQuestion,
      newOptionText,
      questionTypes,
      showPreviewDialog,
      showAddQuestionDialog,
      showPreviewScreenDialog,
      editQuestion,
      saveQuestion,
      deleteQuestion,
      addOption,
      removeOption,
      currentUpdateQuestionRequest,
      updateDialog,
      handleCancellation,
      handleCancellationUpdate,
      updateQuestion,
      deleteQuestionDialog,
      confirmQuestionDeletion,
      cancelQuestionDeletion,
      showCreateNewOptionDialog,
      handleCloseCreationOptionActionView,
      handleCreateNewOption,
      getQuestionTypeColor,
      openEditOptionDialog,
      getSelectedSingleChoiceOption,
      editSelectedSingleChoiceOption,
      removeSelectedSingleChoiceOption,
      showUpdateOptionDialog,
      handleUpdateOption,
      handleUpdateOrRemoveOptionActionView,
      optionsRequestForUpdate,
      handlePreviewScreenDialogClose,
      isModeratorAndAllowedToUpdateOrDelete,
      isLanguageAr,
      deleteOptionDialog,
      cancelOptionDeletion,
      confirmOptionDeletion
    }
  }
})
