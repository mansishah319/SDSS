<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.strategicGoals.add')"
    section-subtitle="Create strategic goals with bilingual names and link them to one or more strategic directions."
  >
    <v-text-field v-model="nameAr" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ $t('settings.strategicGoals.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ $t('settings.strategicGoals.nameEn') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-select
      v-model="selectedDirections"
      :items="directionResponse"
      item-title="localName"
      item-value="id"
      :clearable="true"
      :multiple="true"
      :chips="true"
      :deletable-chips="true"
      closable-chips
      density="comfortable"
      variant="outlined"
    >
      <template #label>
        <div>
          {{ $t('side_bar.settings.strategicDirection.title') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-select>

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
    const selectedDirections = ref<number[]>([])
    const isFormValid = computed(() => {
      return nameEn.value.trim() !== '' && nameAr.value.trim() !== '' && selectedDirections.value.length > 0
    })
    const directionResponse = ref<StrategicDirectionResponse>([])

    const loadRequiredData = async () => {
      try {
        const response = await get<SuccessResponse<StrategicDirectionResponse>>(
          URIS.STRATEGIC_DIRECTION.BASE + URIS.STRATEGIC_DIRECTION.DROPDOWN_LIST
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
        nameAr: nameAr.value,
        strategicDirectionIds: selectedDirections.value
      }

      console.log('Name (English):', nameEn.value)
      console.log('Name (Arabic):', nameAr.value)
      console.log('Selected IDs:', selectedDirections.value)

      await create(URIS.STRATEGIC_GOALS.BASE, requestData)

      // Reset fields
      nameEn.value = ''
      nameAr.value = ''
      selectedDirections.value = []

      emit('templateUpdated')
      toast.success(t('Messages.success.strategicGoals.created'))
      // Emit close event to notify parent component
      emit('close')
    }

    const cancel = () => {
      // Reset fields
      nameEn.value = ''
      nameAr.value = ''
      selectedDirections.value = []

      emit('close')
    }

    onMounted(async () => {
      await loadRequiredData()
    })

    return {
      nameEn,
      nameAr,
      selectedDirections,
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
