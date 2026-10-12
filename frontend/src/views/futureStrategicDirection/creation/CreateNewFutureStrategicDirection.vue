<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <SettingsDialogFormShell
    :section-title="$t('settings.futureStrategicDirection.add')"
    section-subtitle="Create a future strategic direction using the updated, cleaner dialog pattern."
  >
    <v-text-field v-model="nameAr" variant="outlined" density="comfortable" required>
      <template #label>
        <div>
          {{ $t('settings.futureStrategicDirection.nameAr') }}
          <span class="dialog-required">*</span>
        </div>
      </template>
    </v-text-field>

    <v-text-field v-model="nameEn" variant="outlined" density="comfortable" required>
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
      <v-btn color="success" variant="flat" rounded="pill" :disabled="!isFormValid" @click="save">
        {{ $t('common.create') }}
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
import { StrategicDirectionRequest } from '@/dto/strategicDirection/StrategicDirectionRequest.ts'
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

    const save = async () => {
      // Check if the form is valid
      if (!isFormValid.value) {
        return
      }

      const requestData: StrategicDirectionRequest = {
        nameEn: nameEn.value,
        nameAr: nameAr.value
      }

      console.log('Name (English):', nameEn.value)
      console.log('Name (Arabic):', nameAr.value)

      await create(URIS.FUTURE_STRATEGIC_DIRECTION.BASE, requestData)

      // Reset fields
      nameEn.value = ''
      nameAr.value = ''

      toast.success(t('Messages.success.futureStrategicDirection.created'))

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

    return {
      nameEn,
      nameAr,
      save,
      cancel,
      isFormValid
    }
  }
}
</script>

<style scoped>
.dialog-required {
  color: #dc2626;
}
</style>
