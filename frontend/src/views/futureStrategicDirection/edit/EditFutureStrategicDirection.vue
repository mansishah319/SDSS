<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.futureStrategicDirection.edit')"
    section-subtitle="Adjust the future direction naming while keeping its reference visible in the dialog."
    :meta-label="$t('settings.futureStrategicDirection.id')"
    :meta-value="gId"
  >
    <v-text-field v-model="nameAr" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.futureStrategicDirection.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.futureStrategicDirection.nameEn') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <template #actions>
      <v-btn color="error" variant="text" @click="cancel">
        {{ $t('common.cancel') }}
      </v-btn>
      <v-btn color="success" variant="flat" rounded="pill" @click="updateG">
        {{ $t('common.edit') }}
      </v-btn>
    </template>
  </SettingsDialogFormShell>
</template>

<script lang="ts">
import { onMounted, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { get, update } from '@/utils/GenericApiHelper.ts'
import { StrategicDirectionRequest } from '@/dto/strategicDirection/StrategicDirectionRequest.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { StrategicDirectionResponse } from '@/dto/strategicDirection/StrategicDirectionResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
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

    onMounted(async () => {
      try {
        const response: SuccessResponse<StrategicDirectionResponse> | null = await get<StrategicDirectionResponse>(
          URIS.FUTURE_STRATEGIC_DIRECTION.BASE +
          Characters.QUESTION_MARK +
          Fields.STRATEGIC_DIRECTION_ID +
          Characters.EQUAL +
          props.gId
        )

        const finalResponse = response?.data

        console.log(finalResponse)

        // If data is fetched successfully, populate the text fields
        if (finalResponse) {
          console.log(finalResponse)
          nameEn.value = finalResponse.nameEn
          nameAr.value = finalResponse.nameAr
        } else {
          toast.error(t('Messages.error.futureStrategicDirection.notFound'))
        }
      } catch (error) {
        console.error('Error fetching data:', error)
        toast.error(t('Messages.error.futureStrategicDirection.fetchFailed'))
      }
    })

    const updateG = async () => {
      console.log('ID:', props.gId)

      const requestData: StrategicDirectionRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      await update(
        URIS.FUTURE_STRATEGIC_DIRECTION.BASE +
        Characters.QUESTION_MARK +
        Fields.STRATEGIC_DIRECTION_ID +
        Characters.EQUAL +
        props.gId,
        requestData
      )

      // Reset fields or perform other actions
      toast.success(t('Messages.success.futureStrategicDirection.updated'))
      emit('templateUpdated')

      // Emit close event to notify parent component
      emit('close')
    }

    const cancel = () => {
      // Emit close event to notify parent component
      emit('close')
    }

    return {
      nameEn,
      nameAr,
      updateG,
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
