<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.expertise.edit')"
    section-subtitle="Update the localized expertise labels while keeping the existing record context visible."
    :meta-label="$t('settings.expertise.id')"
    :meta-value="expertiseId"
  >
    <v-text-field v-model="nameAr" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.expertise.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.expertise.nameEn') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <template #actions>
      <v-btn color="error" variant="text" @click="cancel">
        {{ $t('common.cancel') }}
      </v-btn>
      <v-btn color="success" variant="flat" rounded="pill" @click="updateExpertise">
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
import { ExpertiseRequest } from '@/dto/expertise/ExpertiseRequest.ts'
import { Characters, Fields, URIS } from '@/constants/DpConstants.ts'
import { ExpertiseResponse } from '@/dto/expertise/ExpertiseResponse.ts'
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import SettingsDialogFormShell from '@/components/common/SettingsDialogFormShell.vue'

export default {
  components: { SettingsDialogFormShell },
  props: {
    expertiseId: {
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
        // Fetch expertise data by expertiseId
        const response: SuccessResponse<ExpertiseResponse> | null = await get<ExpertiseResponse>(
          URIS.EXPERTISE.BASE + Characters.QUESTION_MARK + Fields.EXPERTISE_ID + Characters.EQUAL + props.expertiseId
        )

        const finalResponse = response?.data

        console.log(finalResponse)

        // If data is fetched successfully, populate the text fields
        if (finalResponse) {
          console.log(finalResponse)
          nameEn.value = finalResponse.nameEn
          nameAr.value = finalResponse.nameAr
        } else {
          // Handle error or show message if expertise data is not available
          toast.error(t('Messages.error.expertise.notFound'))
        }
      } catch (error) {
        // Handle error or show message if there is an error fetching expertise data
        console.error('Error fetching expertise data:', error)
        toast.error(t('Messages.error.expertise.fetchFailed'))
      }
    })

    const updateExpertise = async () => {
      // Handle saving expertise data
      // You can access expertiseId from props
      console.log('Expertise ID:', props.expertiseId)

      // Create expertise data object
      const expertiseData: ExpertiseRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      await update(
        URIS.EXPERTISE.BASE + Characters.QUESTION_MARK + Fields.EXPERTISE_ID + Characters.EQUAL + props.expertiseId,
        expertiseData
      )

      // Reset fields or perform other actions
      toast.success(t('Messages.success.expertise.updated'))
      // Emit close event to notify parent component
      emit('templateUpdated')

      emit('close')
    }

    const cancel = () => {
      // Emit close event to notify parent component
      emit('close')
    }

    return {
      nameEn,
      nameAr,
      updateExpertise,
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
