<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template v-if="isAdminOrModerator()">
  <SettingsDialogFormShell
    :section-title="$t('settings.challengeSettings.edit')"
    section-subtitle="Update the setting type and bilingual labels from the same modernized dialog."
    :meta-label="$t('settings.challengeSettings.id')"
    :meta-value="gId"
  >
    <v-autocomplete
      v-model="challengeSettingType"
      :clearable="true"
      :items="[
        {
          id: 'TOPIC_CLASSIFICATION',
          name: $t('settings.challengeSettings.settingsType.topicClassification'),
        },
        {
          id: 'LEVEL_OF_DECISION',
          name: $t('settings.challengeSettings.settingsType.levelOfDecision'),
        },
        {
          id: 'IMPORTANCE_OF_DECISION',
          name: $t('settings.challengeSettings.settingsType.importanceOfDecision'),
        },
        {
          id: 'IMPACT',
          name: $t('settings.challengeSettings.settingsType.impact'),
        },
        {
          id: 'FIELD',
          name: $t('settings.challengeSettings.settingsType.field'),
        },
      ]"
      variant="outlined"
      item-value="id"
      item-title="name"
      density="comfortable"
      required
    >
      <template #label>
        <div>
          {{ $t('settings.challengeSettings.challengeSettingsType') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-autocomplete>

    <v-text-field v-model="nameAr" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.challengeSettings.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" :rules="[rules.required]" variant="outlined" density="comfortable">
      <template #label>
        <div>
          {{ $t('settings.challengeSettings.nameEn') }}
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
import { SuccessResponse } from '@/dto/generic/SuccessResponse.ts'
import { ChallengeSettingsResponse } from '@/dto/challengeSettings/ChallengeSettingsResponse.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
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
    const challengeSettingType = ref<number>(0)

    onMounted(async () => {
      try {
        const response: SuccessResponse<ChallengeSettingsResponse> | null = await get<ChallengeSettingsResponse>(
          URIS.CHALLENGE_SETTINGS.BASE +
          Characters.QUESTION_MARK +
          Fields.CHALLENGE_SETTINGS_ID +
          Characters.EQUAL +
          props.gId
        )

        const finalResponse = response?.data as ChallengeSettingsResponse

        console.log(finalResponse)

        challengeSettingType.value = finalResponse.type

        // If data is fetched successfully, populate the text fields
        if (finalResponse) {
          console.log(finalResponse)
          nameEn.value = finalResponse.nameEn
          nameAr.value = finalResponse.nameAr
        } else {
          toast.error(t('Messages.error.strategicDirection.notFound'))
        }
      } catch (error) {
        console.error('Error fetching data:', error)
        toast.error(t('Messages.error.strategicDirection.fetchFailed'))
      }
    })

    const updateG = async () => {
      console.log('ID:', props.gId)

      const requestData: StrategicDirectionRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      const response = await update(
        URIS.CHALLENGE_SETTINGS.BASE +
        Characters.QUESTION_MARK +
        Fields.CHALLENGE_SETTINGS_ID +
        Characters.EQUAL +
        props.gId,
        requestData
      )
      emit('templateUpdated')
      // Reset fields or perform other actions
      toast.success(t('Messages.success.strategicDirection.updated'))
      // Emit close event to notify parent component
      emit('close')
      return response
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
      },
      challengeSettingType,
      isAdminOrModerator
    }
  }
}
</script>

<style scoped>
.dialog-required {
  color: #dc2626;
}
</style>
