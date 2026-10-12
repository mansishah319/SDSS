import { computed, defineComponent, onMounted, PropType, ref, watch } from 'vue'
import { QuestionTypesDropDownList } from '@/dto/generic/BaseDropDownList'
import { SurveyQuestionRequest } from '@/dto/surveys/question/SdssSurveyQuestionRequest'
import CustomFormLabel from '@/components/custom/CustomFormLabel.vue'

export default defineComponent({
  name: 'QuestionForm',
  components: { CustomFormLabel },
  props: {
    cardTitle: {
      type: String,
      required: true
    },
    questionTypes: {
      type: Array as PropType<QuestionTypesDropDownList[]>,
      required: true
    },
    isEditing: {
      type: Boolean,
      default: false
    },
    question: {
      type: Object as PropType<SurveyQuestionRequest>,
      default: () =>
        ({
          questionType: 'text',
          titleEn: '',
          titleAr: '',
          descriptionEn: '',
          descriptionAr: '',
          showPublicQuestions: false,
          optionsRequestList: []
        }) as SurveyQuestionRequest
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const selectedQuestionType = ref<string | null>(null)
    const showOptions = ref(false)
    const options = ref<Array<{ optionEn: string; optionAr: string }>>([])
    const formData = ref<SurveyQuestionRequest>({
      questionType: props.question.questionType,
      titleEn: props.question.titleEn,
      titleAr: props.question.titleAr,
      descriptionEn: props.question.descriptionEn,
      descriptionAr: props.question.descriptionAr,
      showPublicQuestions: props.question.showPublicQuestions,
      optionsRequestList: props.question.optionsRequestList
    } as SurveyQuestionRequest)

    const handleQuestionTypeChange = (type: string | null) => {
      showOptions.value = ['single-choice', 'multiple-choice'].includes(type ?? 'NotExist')
      if (showOptions.value) {
        options.value = [{ optionEn: '', optionAr: '' }]
      } else {
        options.value = []
      }
    }

    const addOption = () => {
      options.value.push({ optionEn: '', optionAr: '' })
    }

    const removeOption = (index: number) => {
      options.value.splice(index, 1)
    }

    const isValidField = (field: string) => field && field.trim() !== ''

    const mandatoryFields = computed(() => {
      const fields: { [key: string]: any } = {
        titleEn: formData.value.titleEn,
        titleAr: formData.value.titleAr
      }

      if (showOptions.value && !props.isEditing) {
        options.value.forEach((option, index) => {
          fields[`optionEn${index}`] = option.optionEn
          fields[`optionAr${index}`] = option.optionAr
        })
      }

      return fields
    })

    const isValidForm = computed(() => {
      return !Object.values(mandatoryFields.value).some((field) => !isValidField(field))
    })

    watch(selectedQuestionType, (newValue: string) => {
      handleQuestionTypeChange(newValue)
    })

    onMounted(() => {
      const currentType = props.questionTypes.find((type) => type.key === props.question.questionType)
      if (currentType) {
        selectedQuestionType.value = currentType.key ?? null
        handleQuestionTypeChange(currentType.key ?? null)
      }
    })

    const handleSubmit = () => {
      if (!isValidForm.value) return

      const requestData = {
        ...formData.value,
        questionType: selectedQuestionType.value,
        optionsRequestList: showOptions.value ? options.value : undefined
      }

      emit('submit', requestData)
      // Send requestData to parent component or handle creation/updating logic
      console.log(requestData)
    }

    const handleCancel = () => {
      formData.value = {} as SurveyQuestionRequest
      emit('cancel')
    }

    return {
      selectedQuestionType,
      showOptions,
      options,
      formData,
      handleQuestionTypeChange,
      addOption,
      removeOption,
      handleSubmit,
      isValidForm,
      handleCancel
    }
  }
})
