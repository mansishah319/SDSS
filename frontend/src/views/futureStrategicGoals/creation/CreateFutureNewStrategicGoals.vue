<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.futureStrategicGoals.add')"
    section-subtitle="Create a future goal entry with bilingual naming in the new dialog style."
  >
    <v-text-field v-model="nameAr" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ $t('settings.futureStrategicGoals.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" variant="outlined" density="comfortable" required>
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
      <v-btn color="success" variant="flat" rounded="pill" :disabled="!isFormValid" @click="save">{{
          $t('common.create')
        }}
      </v-btn>
    </template>
  </SettingsDialogFormShell>
</template>

<script lang="ts">
import { computed, onMounted, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { create, get } from '@/utils/GenericApiHelper.ts'
import { URIS } from '@/constants/DpConstants.ts'
import { StrategicGoalsRequest } from '@/dto/strategicGoals/StrategicGoalsRequest.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { StrategicDirectionResponse } from '@/dto/strategicDirection/StrategicDirectionResponse.ts'
import SettingsDialogFormShell from '@/components/common/SettingsDialogFormShell.vue'

export default {
  components: { SettingsDialogFormShell },
  emits: ['close', 'templateUpdated'],
  setup(_, { emit }) {
    const { t } = useI18n()
    const nameEn = ref('')
    const nameAr = ref('')
    const isFormValid = computed(() => {
      return nameEn.value.trim() !== '' && nameAr.value.trim() !== ''
    })
    const directionResponse = ref<StrategicDirectionResponse>([])

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

    const save = async () => {
      // Check if the form is valid
      if (!isFormValid.value) {
        return
      }

      const requestData: StrategicGoalsRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      console.log('Name (English):', nameEn.value)
      console.log('Name (Arabic):', nameAr.value)

      await create(URIS.FUTURE_STRATEGIC_GOALS.BASE, requestData)

      // Reset fields
      nameEn.value = ''
      nameAr.value = ''

      toast.success(t('Messages.success.futureStrategicGoals.created'))

      emit('templateUpdated')

      // Emit close event to notify parent component
      emit('close')
    }

    const cancel = () => {
      // Reset fields
      nameEn.value = ''
      nameAr.value = ''
      emit('close')
    }

    onMounted(async () => {
      await loadRequiredData()
    })

    return {
      nameEn,
      nameAr,
      isFormValid,
      directionResponse,
      save,
      cancel
    }
  }
}
</script>

<style scoped>
.dialog-required {
  color: #dc2626;
}
</style>
