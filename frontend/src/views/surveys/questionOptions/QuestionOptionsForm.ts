import { computed, PropType, ref, watch } from 'vue'
import { QuestionOptionsRequest } from '@/dto/surveys/options/QuestionOptionsRequest'
import CustomFormLabel from '@/components/custom/CustomFormLabel.vue'

export default {
  name: 'QuestionOptionsForm',
  components: { CustomFormLabel },
  props: {
    title: {
      type: String,
      required: true
    },
    optionRequest: {
      type: Object as PropType<QuestionOptionsRequest>,
      default: () =>
        ({
          optionEn: '',
          optionAr: ''
        }) as QuestionOptionsRequest
    }
  },
  emits: ['close', 'submit'],
  setup(props, { emit }) {
    const createEmptyOption = (): QuestionOptionsRequest => ({
      optionEn: '',
      optionAr: ''
    })

    const mapOptionRequest = (optionRequest?: QuestionOptionsRequest): QuestionOptionsRequest => ({
      optionEn: optionRequest?.optionEn ?? '',
      optionAr: optionRequest?.optionAr ?? ''
    })

    const formData = ref<QuestionOptionsRequest>(mapOptionRequest(props.optionRequest))

    watch(
      () => props.optionRequest,
      (nextOptionRequest) => {
        formData.value = mapOptionRequest(nextOptionRequest)
      },
      { deep: true }
    )

    const isFormValid = computed(() => {
      return (formData.value.optionEn ?? '').trim() !== '' && (formData.value.optionAr ?? '').trim() !== ''
    })

    const save = async () => {
      // Check if the form is valid
      if (!isFormValid.value) {
        return
      }
      emit('submit', formData.value)
    }

    const cancel = () => {
      formData.value = createEmptyOption()
      emit('close')
    }

    return {
      formData,
      save,
      cancel,
      isFormValid
    }
  }
}
