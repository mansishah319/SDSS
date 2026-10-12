<template lang="html">
  <v-card class="login-modal2" @keyup.enter="validateAndCheckAccessKey">
    <v-card-title class="headline font-dubai-bold main-user-survey-access-key-title-style mb-4">
      <h2 class="main-user-survey-access-key-title-style-h1">
        {{ $t('sdssSurvey.userView.accessTitle') }}
      </h2>
      <div v-show="!isHasAnyRole()">
        <LocaleSwitcher></LocaleSwitcher>
      </div>
    </v-card-title>
    <v-card-text class="p-0">
      <v-text-field
        v-model="localGrpNumber"
        variant="outlined"
        type="number"
        :label="$t('sdssSurvey.userView.grpPlaceHolder')"
        @keyup.enter="validateAndCheckAccessKey"
      ></v-text-field>
      <span v-if="!localGrpNumber && showErrorHint" class="error-message">
        {{ $t('sdssSurvey.grpNumberRequired') }}
      </span>

      <v-text-field
        v-model="localAccessKey"
        variant="outlined"
        :append-inner-icon="localPasswordVisible ? 'mdi-eye-off' : 'mdi-eye'"
        :label="$t('sdssSurvey.userView.accessPlaceHolder')"
        :type="localPasswordVisible ? 'text' : 'password'"
        class="mt-3"
        @keyup.enter="validateAndCheckAccessKey"
        @click:append-inner="togglePasswordVisibility"
      ></v-text-field>
      <span v-if="!localAccessKey && showErrorHint" class="error-message">
        {{ $t('sdssSurvey.accessKeyRequired') }}
      </span>

      <v-alert
        v-if="showErrorHint && errorMessage"
        type="error"
        border="start"
        elevation="2"
        color="red"
        outlined
        class="mt-4 alert-info-div"
      >
        {{ errorMessage }}
      </v-alert>

      <v-alert type="info" border="start" elevation="2" color="#0da06e" outlined class="mt-4 alert-info-div">
        {{ $t('sdssSurvey.userView.accessHint') }}
      </v-alert>
    </v-card-text>
    <v-card-actions class="p-0 mt-4">
      <v-btn
        :disabled="!isFormValid"
        class="dp-primary-sm-btn"
        text
        prepend-icon="mdi mdi-location-enter"
        @click="validateAndCheckAccessKey"
      >
        {{ $t('common.confirm') }}
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from 'vue'
import LocaleSwitcher from '@/components/locals/LocaleSwitcher.vue'
import { getPublicLogin } from '@/utils/GenericApiHelper'
import { SuccessResponse } from '@/dto/generic/SuccessResponse'
import { Fields, SecurityConstants, URIS } from '@/constants/DpConstants'
import { toast } from 'vue3-toastify'
import { useSurveyTokenStore } from '@/store/useSurveyTokenStore'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'AccessSurveyCard',
  components: { LocaleSwitcher },
  props: {
    grpNumber: {
      type: [Number, String, null],
      required: false,
      default: null
    },
    accessKey: {
      type: String,
      required: true
    },
    passwordVisible: {
      type: Boolean,
      required: true
    },
    isHasAnyRole: {
      type: Function,
      required: true
    }
  },
  emits: [
    'update:grpNumber',
    'update:accessKey',
    'update:passwordVisible',
    'userNotAuthorized',
    'userAuthorized',
    'closePopup'
  ],
  setup(props, { emit }) {
    const { t } = useI18n()
    const surveyTokenStore = useSurveyTokenStore()
    const localGrpNumber = ref(props.grpNumber ? Number(props.grpNumber) || null : null)
    const localAccessKey = ref(props.accessKey)
    const localPasswordVisible = ref(props.passwordVisible)

    const errorMessage = ref('')
    const showErrorHint = ref(false)

    const sdssMainToken = localStorage.getItem(SecurityConstants.TOKEN)

    const employeeLang: string = localStorage.getItem(Fields.EMPLOYEE_LANG) || 'ar'

    // Watch for changes to emit back to the parent
    watch(localGrpNumber, (newValue) => {
      emit('update:grpNumber', newValue)
    })

    watch(localAccessKey, (newValue) => {
      emit('update:accessKey', newValue)
    })

    watch(localPasswordVisible, (newValue) => {
      emit('update:passwordVisible', newValue)
    })

    // Computed property to check if the form is valid
    const isFormValid = computed(() => {
      return !!localGrpNumber.value && !!localAccessKey.value
    })

    const validateAndCheckAccessKey = () => {
      if (!isFormValid.value) {
        showErrorHint.value = true
        errorMessage.value = t('sdssSurvey.bothAccessAndGrpRequired')
        return
      }
      checkAccessKey()
    }

    const togglePasswordVisibility = () => {
      localPasswordVisible.value = !localPasswordVisible.value
    }

    if (sdssMainToken && !surveyTokenStore.surveyToken) {
      // If the token exists, set the user as authorized and hide the dialog
      handleLoginProcess(employeeLang, '')
    }

    async function handleLoginProcess(language: string, params?: string) {
      try {
        const response = await getPublicLogin<SuccessResponse<string>>(URIS.SURVEYS.USERS.LOGIN + params, language)
        if (response && response.data.success) {
          // notify you logged in successfully
          localStorage.setItem(SecurityConstants.Survey_TOKEN, response.data.data)
          surveyTokenStore.updateSurveyToken(response.data.data)
          authorizedUser(response.data.data)
        } else {
          userNotAuthorized()
          if (sdssMainToken) {
            emit('closePopup')
          } else {
            errorMessage.value = t('sdssSurvey.errorAccessLoginHint')
          }
        }
      } catch (error: any) {
        console.error('Error fetching data:', error)
        toast.error(error.message)
      }
    }

    const checkAccessKey = () => {
      handleLoginProcess(employeeLang, `?grpNumber=${localGrpNumber.value}&accessKey=${localAccessKey.value}`)
    }

    function authorizedUser(surveyToken: string) {
      emit('userAuthorized', surveyToken)
    }

    function userNotAuthorized() {
      showErrorHint.value = true
      emit('userNotAuthorized')
    }

    return {
      localGrpNumber,
      localAccessKey,
      localPasswordVisible,
      togglePasswordVisibility,
      checkAccessKey,
      showErrorHint,
      isFormValid,
      errorMessage,
      validateAndCheckAccessKey
    }
  }
})
</script>
<style src="./AccessSurvey.scss" lang="scss"></style>
