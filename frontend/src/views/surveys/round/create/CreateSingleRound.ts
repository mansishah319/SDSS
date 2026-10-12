import { defineComponent, PropType, reactive, ref } from 'vue'
import { SdssSurveyRoundRequest } from '@/dto/surveys/round/SdssSurveyRoundRequest'

export default defineComponent({
  name: 'CreateSingleRound',
  props: {
    round: {
      type: Object as PropType<SdssSurveyRoundRequest>,
      default: () => ({
        titleEn: null,
        titleAr: null,
        descriptionEn: null,
        descriptionAr: null,
        startTime: null,
        endTime: null,
        manualHandle: true,
        roundNumber: null
      })
    },
    showUpdateManualInfo: {
      type: Boolean,
      default: false
    },
    isUpdate: {
      type: Boolean,
      default: false
    }
  },
  emits: ['onCreate', 'onClose'],
  setup(props, { emit }) {
    // Create a reactive local state
    let localRound = reactive<SdssSurveyRoundRequest>({
      titleEn: props.round.titleEn,
      titleAr: props.round.titleAr,
      descriptionEn: props.round.descriptionEn,
      descriptionAr: props.round.descriptionAr,
      startTime: props.round.startTime,
      endTime: props.round.endTime,
      manualHandle: props.round.manualHandle,
      roundNumber: props.round.roundNumber
    })

    const isUpdateProcess = ref<boolean>(props.isUpdate)

    // Function to emit the creation event with the local state
    const submitRound = () => {
      emit('onCreate', { ...localRound })
    }

    const cancel = () => {
      localRound = {
        titleEn: null,
        titleAr: null,
        descriptionEn: null,
        descriptionAr: null,
        startTime: null,
        endTime: null,
        manualHandle: true,
        roundNumber: null
      }
      emit('onClose')
    }

    return {
      localRound,
      submitRound,
      cancel,
      isUpdateProcess
    }
  }
})
