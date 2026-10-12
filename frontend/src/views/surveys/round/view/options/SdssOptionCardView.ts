import { defineComponent, PropType } from 'vue'
import { OptionResponse } from '@/dto/surveys/round/SdssSurveyRoundResponse'
import { QuestionType } from '@/types/QuestionTypesEnums'

export default defineComponent({
  name: 'OptionCard',
  props: {
    option: {
      type: Object as PropType<OptionResponse>,
      default: () => ({}) as OptionResponse,
      required: false
    },
    questionType: {
      type: String as PropType<QuestionType>,
      required: true
    },
    isAllowedToUpdateOrDelete: {
      type: Boolean as PropType<boolean>,
      required: true
    }
  },
  emits: ['onEdit', 'onDelete'],
  setup(props, { emit }) {
    const emitEditOption = () => {
      emit('onEdit', props.option)
    }

    const emitDeleteOption = () => {
      emit('onDelete', props.option)
    }

    return {
      emitEditOption,
      emitDeleteOption
    }
  }
})
