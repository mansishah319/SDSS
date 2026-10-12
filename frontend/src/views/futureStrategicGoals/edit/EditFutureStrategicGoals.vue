<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.futureStrategicGoals.edit')"
    section-subtitle="Update the future goal naming using the same visual language as the other settings dialogs."
    :meta-label="$t('settings.futureStrategicGoals.id')"
    :meta-value="gId"
  >
    <v-text-field v-model="nameAr" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.futureStrategicGoals.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.futureStrategicGoals.nameEn') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <template #actions>
      <v-btn color="error" variant="text" @click="cancel">
        {{ $t('common.cancel') }}
      </v-btn>
      <v-btn color="success" variant="flat" rounded="pill" :disabled="!isFormValid" @click="updateG">
        {{ $t('common.edit') }}
      </v-btn>
    </template>
  </SettingsDialogFormShell>
</template>

<script lang="ts">
import { computed, onMounted, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { get, update } from '@/utils/GenericApiHelper.ts'
import { StrategicGoalsRequest } from '@/dto/strategicGoals/StrategicGoalsRequest.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { StrategicGoalsResponse } from '@/dto/strategicGoals/StrategicGoalsResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { StrategicDirectionResponse } from '@/dto/strategicDirection/StrategicDirectionResponse.ts'
import SettingsDialogFormShell from '@/components/common/SettingsDialogFormShell.vue'

export default {
  components: { SettingsDialogFormShell },
  props: {
    gId: {
      type: String,
      required: true
    }
  },
  emits: ['close', 'templateUpdated'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const nameEn = ref('')
    const nameAr = ref('')
    const isFormValid = computed(() => {
      return nameEn.value.trim() !== '' && nameAr.value.trim() !== ''
    })
    const directionResponse = ref<StrategicDirectionResponse>([])

    onMounted(async () => {
      try {
        const response: SuccessResponse<StrategicGoalsResponse> | null = await get<
          SuccessResponse<StrategicGoalsResponse>
        >(
          URIS.FUTURE_STRATEGIC_GOALS.BASE +
          Characters.QUESTION_MARK +
          Fields.STRATEGIC_GOALS_ID +
          Characters.EQUAL +
          props.gId
        )

        const finalResponse = response?.data as StrategicGoalsResponse

        console.log(finalResponse)

        // If data is fetched successfully, populate the text fields
        if (finalResponse) {
          console.log(finalResponse)
          nameEn.value = finalResponse.nameEn
          nameAr.value = finalResponse.nameAr
        } else {
          toast.error(t('Messages.error.futureStrategicGoals.notFound'))
        }
      } catch (error) {
        console.error('Error fetching data:', error)
        toast.error(t('Messages.error.futureStrategicGoals.fetchFailed'))
      }
    })

    const loadRequiredData = async () => {
      try {
        const response = await get<SuccessResponse<StrategicDirectionResponse>>(
          URIS.FUTURE_STRATEGIC_DIRECTION.BASE + URIS.FUTURE_STRATEGIC_DIRECTION.DROPDOWN_LIST
        )
        if (response?.success) {
          console.log(response.data)
          directionResponse.value = response.data as StrategicDirectionResponse[]
          console.log('Response:')
          console.table(directionResponse.value)
        } else {
          console.error('API request was not successful:', response?.message)
          // Handle error scenario
        }
      } catch (error) {
        console.error('Error loading data:', error)
        // Handle error scenario
      }
    }

    const updateG = async () => {
      console.log('ID:', props.gId)

      if (!isFormValid.value) {
        return
      }

      const requestData: StrategicGoalsRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      await update(
        URIS.FUTURE_STRATEGIC_GOALS.BASE +
        Characters.QUESTION_MARK +
        Fields.STRATEGIC_GOALS_ID +
        Characters.EQUAL +
        props.gId,
        requestData
      )

      // Reset fields or perform other actions
      toast.success(t('Messages.success.futureStrategicGoals.updated'))
      emit('templateUpdated')
      // Emit close event to notify parent component
      emit('close')
    }

    const cancel = () => {
      emit('close')
    }

    onMounted(async () => {
      await loadRequiredData()
    })

    return {
      nameEn,
      isFormValid,
      nameAr,
      updateG,
      directionResponse,
      cancel,
      rules: {
        required: (value: any) => !!value || t('error.mandatory')
      }
    }
  }
}
</script>

<style scoped>
.dialog-required {
  color: #dc2626;
}
</style>
