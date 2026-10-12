<script lang="ts">
import { computed, defineComponent, ref, toRefs, watch } from 'vue'
import { ApexOptions } from 'apexcharts'
import ApexCharts from 'vue3-apexcharts'
import { ChallengeStatus, toChallengeStatus } from '@/dto/challenge/ChallengeStatus'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

interface Messages {
  [key: string]: {
    title: string
    mainMessage: string
    subMessage: string
    endMessage: string
  }
}

export default defineComponent({
  components: {
    apexchart: ApexCharts
  },
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    finalSolutions: {
      type: Array as () => Array<{
        id: number
        solutionText: string
        voteCount: number
        finalRate: number
      }>,
      default: () => []
    },
    challengeStatus: {
      type: String,
      required: true
    },
    challengeId: {
      type: String,
      required: true
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const router = useRouter()
    const localIsOpen = ref(props.modelValue)
    const theChallengeStatus = ref<ChallengeStatus>(toChallengeStatus(props.challengeStatus))
    const currentChallengeId = ref<number>(Number(props.challengeId))

    watch(
      () => props.modelValue,
      (newValue) => {
        localIsOpen.value = newValue
      }
    )

    watch(
      () => props.challengeStatus,
      (newValue) => {
        theChallengeStatus.value = toChallengeStatus(newValue)
      }
    )

    watch(
      () => props.challengeId,
      (newValue) => {
        currentChallengeId.value = Number(newValue)
      }
    )

    function handleClose() {
      emit('update:modelValue', false)
    }

    const chartOptions: ApexOptions = {
      chart: { type: 'radialBar' },
      plotOptions: {
        radialBar: {
          hollow: { margin: 0, size: '70%' },
          dataLabels: {
            name: { show: true, fontSize: '16px' },
            value: { show: true, fontSize: '14px' }
          }
        }
      },
      colors: ['#FF4560'],
      labels: [t('challenge.finalSolutions.voteCount')]
    }

    const finalRate = 5 // Example rate
    const validatorRate = computed(() => (theChallengeStatus.value === ChallengeStatus.UNDER_ADMIN_REVIEW ? 75 : null))

    const dialogClass = computed(() => {
      switch (theChallengeStatus.value) {
        case ChallengeStatus.COMPLETED:
          return 'success-view'
        case ChallengeStatus.CLOSED:
          return 'fail-view'
        default:
          return 'default-view'
      }
    })

    const icon = computed(() => {
      switch (theChallengeStatus.value) {
        case ChallengeStatus.COMPLETED:
          return 'mdi-check-circle'
        case ChallengeStatus.CLOSED:
          return 'mdi-alert-circle'
        default:
          return 'mdi-clock'
      }
    })

    const iconColor = computed(() => {
      switch (theChallengeStatus.value) {
        case ChallengeStatus.COMPLETED:
          return 'success'
        case ChallengeStatus.CLOSED:
          return 'error'
        default:
          return 'info'
      }
    })

    // Define the messages with translation keys
    const messages: Messages = {
      [ChallengeStatus.COMPLETED]: {
        title: t('challenge.finalSolutions.completed.title'),
        mainMessage: t('challenge.finalSolutions.completed.mainMessage'),
        subMessage: t('challenge.finalSolutions.completed.subMessage'),
        endMessage: t('challenge.finalSolutions.completed.endMessage')
      },
      [ChallengeStatus.CLOSED]: {
        title: t('challenge.finalSolutions.closed.title'),
        mainMessage: t('challenge.finalSolutions.closed.mainMessage'),
        subMessage: t('challenge.finalSolutions.closed.subMessage'),
        endMessage: t('challenge.finalSolutions.closed.endMessage')
      },
      [ChallengeStatus.UNDER_ADMIN_REVIEW]: {
        title: t('challenge.finalSolutions.underManagerReview.title'),
        mainMessage: t('challenge.finalSolutions.underManagerReview.mainMessage'),
        subMessage: t('challenge.finalSolutions.underManagerReview.subMessage'),
        endMessage: t('challenge.finalSolutions.underManagerReview.endMessage')
      },
      [ChallengeStatus.UNDER_VALIDATOR_REVIEW]: {
        title: t('challenge.finalSolutions.underValidatorReview.title'),
        mainMessage: t('challenge.finalSolutions.underValidatorReview.mainMessage'),
        subMessage: t('challenge.finalSolutions.underValidatorReview.subMessage'),
        endMessage: t('challenge.finalSolutions.underValidatorReview.endMessage')
      }
    }

    const title = computed(() => messages[theChallengeStatus.value]?.title || '')
    const mainMessage = computed(() => messages[theChallengeStatus.value]?.mainMessage || '')
    const subMessage = computed(() => messages[theChallengeStatus.value]?.subMessage || '')
    const endMessage = computed(() => messages[theChallengeStatus.value]?.endMessage || '')

    const viewChallengeDashboard = () => {
      const challengeId = currentChallengeId.value
      console.log('challengeId is -> ' + challengeId)
      localIsOpen.value = false
      router.push({
        name: 'ChallengeView',
        params: { challengeId }
      })
    }

    return {
      ...toRefs(props),
      localIsOpen,
      handleClose,
      chartOptions,
      finalRate,
      dialogClass,
      validatorRate,
      theChallengeStatus,
      icon,
      iconColor,
      title,
      mainMessage,
      subMessage,
      endMessage,
      viewChallengeDashboard
    }
  },
  computed: {
    ChallengeStatus() {
      return ChallengeStatus
    }
  }
})
</script>

<template src="./MemberFinalSolutionsDialog.html"></template>
<style src="./MemberFinalSolutionsDialog.scss" scoped></style>
