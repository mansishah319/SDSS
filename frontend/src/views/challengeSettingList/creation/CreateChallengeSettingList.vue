<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template v-if="isAdminOrModerator()">
  <SettingsDialogFormShell
    :section-title="t('settings.challengeSettings.add')"
    section-subtitle="Create a challenge setting with its type and bilingual labels using the refreshed dialog style."
  >
    <v-autocomplete
      v-model="challengeSettingType"
      :clearable="true"
      :items="[
        {
          id: 'TOPIC_CLASSIFICATION',
          name: t('settings.challengeSettings.settingsType.topicClassification'),
        },
        {
          id: 'LEVEL_OF_DECISION',
          name: t('settings.challengeSettings.settingsType.levelOfDecision'),
        },
        {
          id: 'IMPORTANCE_OF_DECISION',
          name: t('settings.challengeSettings.settingsType.importanceOfDecision'),
        },
        {
          id: 'IMPACT',
          name: t('settings.challengeSettings.settingsType.impact'),
        },
        {
          id: 'FIELD',
          name: t('settings.challengeSettings.settingsType.field'),
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
          {{ t('settings.challengeSettings.challengeSettingsType') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-autocomplete>

    <v-text-field v-model="nameAr" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ t('settings.challengeSettings.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ t('settings.challengeSettings.nameEn') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <template #actions>
      <v-btn color="error" variant="text" @click="cancel">
        {{ t('common.cancel') }}
      </v-btn>
      <v-btn color="success" variant="flat" rounded="pill" :disabled="!isFormValid" @click="save">
        {{ t('common.create') }}
      </v-btn>
    </template>
  </SettingsDialogFormShell>
</template>

<script lang="ts">
import { computed, ref } from 'vue'
import { toast } from 'vue3-toastify'
import { useI18n } from 'vue-i18n'
import { create } from '@/utils/GenericApiHelper.ts'
import { URIS } from '@/constants/DpConstants.ts'
import { isAdminOrModerator } from '@/utils/roleUtils.ts'
import SettingsDialogFormShell from '@/components/common/SettingsDialogFormShell.vue'

export default {
  components: { SettingsDialogFormShell },
  emits: ['close', 'templateUpdated'],
  setup(_, { emit }) {
    const { t } = useI18n()
    const nameEn = ref('')
    const nameAr = ref('')
    const challengeSettingType = ref('')

    const isFormValid = computed(() => {
      return nameEn.value.trim() !== '' && nameAr.value.trim() !== ''
    })

    const save = async () => {
      // Check if the form is valid
      if (!isFormValid.value) {
        return
      }

      const requestData = {
        nameEn: nameEn.value,
        nameAr: nameAr.value,
        type: challengeSettingType.value
      }

      console.log('Name (English):', nameEn.value)
      console.log('Name (Arabic):', nameAr.value)

      const response = await create(URIS.CHALLENGE_SETTINGS.BASE, requestData)

      // Reset fields
      nameEn.value = ''
      nameAr.value = ''
      emit('templateUpdated')
      // Emit close event to notify parent component
      emit('close')
      toast.success(t('Messages.success.strategicDirection.created'))
      return response
    }

    const cancel = () => {
      // Reset fields
      nameEn.value = ''
      nameAr.value = ''

      emit('close')
    }

    return {
      t,
      nameEn,
      nameAr,
      save,
      cancel,
      isFormValid,
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
