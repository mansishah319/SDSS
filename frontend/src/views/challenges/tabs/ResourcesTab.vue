<template>
  <v-card class="dp-card common-challenge-card-container">
    <h4 class="tab-title mb-4">{{ $t('challenge.info.resources') }}</h4>
    <!-- YouTube Resources -->
    <div class="resource-section mb-6">
      <h5 class="mb-3">{{ $t('challenge.info.youtubeResources') }}</h5>
      <div class="d-flex gap-4 align-center mb-4">
        <v-text-field
          v-model="youtubeInputField"
          prepend-inner-icon="mdi-youtube"
          :label="$t('challenge.info.youtubeUrl')"
          class="youtube-txt-field flex-grow-1 challenge-text-direction-field"
          dir="ltr"
          :placeholder="$t('challenge.info.enterYoutubeUrl')"
          clearable
          @keydown.enter.prevent="addYoutubeTextUrl"
        />
        <v-btn
          class="custom-add-youtube-link-plus"
          :icon="true"
          color="primary"
          size="large"
          @click="addYoutubeTextUrl"
        >
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </div>
      <div v-if="youtubeLinks.length > 0">
        <v-chip
          v-for="(link, idx) in youtubeLinks"
          :key="idx"
          class="ma-1 mb-2"
          closable
          color="red"
          variant="elevated"
          :href="link"
          target="_blank"
          size="large"
          clickable
          @click:close.prevent="removeYoutubeLinkByValue(link)"
        >
          <v-icon start class="mr-1">mdi-youtube</v-icon>
          <span
            dir="ltr"
            class="challenge-text-direction-content"
            style="cursor: pointer; text-decoration: underline"
            @click.stop="openYoutubeLink(link)"
          >
            {{ link }}
          </span>
        </v-chip>
      </div>
    </div>
    <!-- Document Resources -->
    <div class="resource-section">
      <h5 class="mb-3">{{ $t('challenge.info.docsResources') }}</h5>
      <!-- Backend Docs Preview -->
      <v-card class="upload-area mb-4" variant="outlined" color="primary">
        <v-card-text class="text-center pa-8">
          <v-icon size="48" color="primary" class="mb-4">mdi-cloud-upload</v-icon>
          <h6 class="text-h6 mb-2">{{ $t('challenge.info.uploadDocuments') }}</h6>
          <p class="text-body-2 text-medium-emphasis mb-4">
            {{ $t('challenge.info.dragDropFiles') }} <br />
            {{ $t('challenge.info.supportedFormats') }}
          </p>
          <p class="text-caption text-medium-emphasis mb-4"> {{ $t('challenge.info.maxFileSize') }}: 10MB </p>
          <v-file-input
            :model-value="filesModel"
            :accept="'image/*,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx'"
            :multiple="true"
            :show-size="true"
            :prepend-icon="'mdi-camera'"
            :label="$t('challenge.info.chooseFiles')"
            variant="outlined"
            color="primary"
            class="max-width-400 mx-auto"
            :rules="[(v) => !v || v.length <= 10 || $t('challenge.info.maxFilesLimit')]"
            @update:model-value="updateFilesModel"
          />
          <div class="d-flex gap-2 justify-center mt-4">
            <v-btn color="error" variant="outlined" :disabled="validUploadedFiles.length === 0" @click="clearAllFiles">
              <v-icon start>mdi-delete</v-icon>
              {{ $t('challenge.info.clearAll') }}
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
      <div v-if="allFilesForPreview.length > 0" class="files-preview-section">
        <div class="d-flex align-center justify-space-between mb-3">
          <h6 class="text-h6">{{ $t('challenge.info.uploadedFiles') }} ({{ allFilesForPreview.length }})</h6>
          <v-chip color="primary" variant="elevated">
            {{ formatSize(totalSize) }}
          </v-chip>
        </div>
        <v-row>
          <v-col
            v-for="(file, index) in allFilesForPreview"
            :key="file._isBackend ? file.id : file.name + index"
            cols="12"
            sm="6"
            md="4"
            lg="3"
          >
            <v-card class="file-card" variant="outlined">
              <div class="file-card__preview">
                <div class="file-card__badge">{{ getFileKindLabel(file) }}</div>
                <div v-if="file._isBackend && isImage(file.type)">
                  <v-img :src="`data:image/*;base64,${file.content}`" height="168" cover class="file-preview-image" />
                </div>
                <div v-else-if="file._isBackend && file.type === 'pdf'" class="file-card__embed-shell">
                  <embed
                    :src="`data:application/pdf;base64,${file.content}`"
                    type="application/pdf"
                    width="100%"
                    height="168"
                  />
                </div>
                <div v-else-if="!file._isBackend && file.preview">
                  <v-img :src="file.preview" height="168" cover class="file-preview-image" />
                </div>
                <div v-else class="file-icon-container">
                  <v-icon :icon="getFileIcon(file.type)" :color="getFileColor(file.type)" size="72" />
                </div>
              </div>
              <v-card-text class="file-card__content">
                <div class="file-info">
                  <p class="file-card__name" :title="file.name">
                    {{ file.name }}
                  </p>
                  <div class="file-card__meta">
                    <span class="file-card__meta-item">{{
                        file._isBackend ? $t('common.saved') : $t('common.new')
                      }}</span>
                    <span v-if="!file._isBackend" class="file-card__meta-item">{{ formatSize(file.size) }}</span>
                  </div>
                </div>
              </v-card-text>
              <v-card-actions class="file-card__actions">
                <v-btn
                  variant="tonal"
                  color="primary"
                  prepend-icon="mdi-open-in-new"
                  class="file-card__action-btn"
                  @click="openFilePreview(file)"
                >
                  {{ $t('common.view') }}
                </v-btn>
                <v-btn
                  :icon="true"
                  size="small"
                  color="error"
                  variant="text"
                  @click="file._isBackend ? removeBackendDoc(index, file) : removeFile(index)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </div>
      <div v-else class="empty-files-state text-center pa-8">
        <v-icon size="64" color="grey-lighten-1" class="mb-3">mdi-file-document-outline</v-icon>
        <p class="text-grey-lighten-1">{{ $t('challenge.info.noFilesUploaded') }}</p>
        <p class="text-caption text-grey-lighten-2">{{ $t('challenge.info.startUploadingFiles') }}</p>
      </div>
    </div>
    <!-- Hashtags -->
    <div class="resource-section mt-6">
      <h5 class="mb-3">{{ $t('challenge.info.hashtags') }}</h5>
      <div class="resource-section__input-row mb-3">
        <v-text-field
          :model-value="inputTextLocal"
          :label="$t('challenge.info.addHashtag')"
          class="flex-grow-1 challenge-text-direction-field"
          :dir="hashtagInputDirection"
          variant="outlined"
          density="compact"
          @update:model-value="(val) => (inputTextLocal = val)"
          @keydown.enter.prevent="addHashtag"
        />
        <div class="resource-section__actions">
          <v-btn :icon="true" class="bg-primary resource-section__action-btn" @click="addHashtag">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>
      </div>
      <div v-if="(hashtagTexts ?? []).length > 0" class="hashtags-list">
        <v-chip
          v-for="(hashtag, index) in hashtagTexts"
          :key="index"
          class="ma-1"
          closable
          @click:close="removeHashtag(index)"
        >
          <span :dir="getTextDirection(hashtag)" class="challenge-text-direction-content">#{{ hashtag }}</span>
        </v-chip>
      </div>
    </div>
    <!-- Reminders -->
    <div class="resource-section mt-6">
      <h5 class="mb-3">{{ $t('challenge.info.reminders') }}</h5>
      <div class="resource-section__input-row mb-3">
        <DPDateTimePicker
          :model-value="newReminderLocal"
          :label="$t('challenge.info.addReminder')"
          class="flex-grow-1"
          density="compact"
          @update:model-value="(val) => (newReminderLocal = val)"
          @keydown.enter.prevent="addReminder"
        />
        <div class="resource-section__actions">
          <v-btn :icon="true" class="bg-primary reminder-section__action-btn" @click="addReminder">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
          <v-btn :icon="true" class="bg-secondary reminder-section__action-btn" @click="clearReminders">
            <v-icon>mdi-delete</v-icon>
          </v-btn>
        </div>
      </div>
      <div v-if="challengeRequest.challengeRemindersDateTimeList.length > 0" class="reminders-list">
        <v-chip
          v-for="(reminder, index) in challengeRequest.challengeRemindersDateTimeList"
          :key="index"
          class="ma-1"
          color="info"
          variant="outlined"
          closable
          @click:close="removeReminder(index)"
        >
          {{ formatReminder(reminder) }}
        </v-chip>
      </div>
    </div>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { customNotify } from '@/utils/custom-notify'
import DPDateTimePicker from '@/components/date/DPDateTimePicker.vue'
import { format } from 'date-fns'
import { getTextDirection } from '@/utils/textDirection'

export default defineComponent({
  name: 'ResourcesTab',
  components: { DPDateTimePicker },
  props: {
    filesModel: { type: Array, required: true },
    uploadedFiles: { type: Array, required: true },
    totalSize: { type: Number, required: true },
    inputText: { type: String, required: true },
    hashtagTexts: { type: Array, required: true },
    newReminder: { type: String, required: true },
    challengeRequest: { type: Object, required: true },
    youtubeLinks: { type: Array, required: true },
    rules: {
      type: Object,
      required: false,
      default: () => ({})
    },
    getFileIcon: { type: Function, required: true },
    getFileColor: { type: Function, required: true },
    formatSize: { type: Function, required: true },
    challengeDocsResponseList: { type: Array, required: false, default: () => [] }
  },
  emits: [
    'update:filesModel',
    'clearAllFiles',
    'removeFile',
    'update:inputText',
    'addHashtag',
    'removeHashtag',
    'update:newReminder',
    'addReminder',
    'clearReminders',
    'update:challengeRequest',
    'removeBackendDoc',
    'update:youtubeLinks'
  ],
  setup(props, { emit }) {
    const { t } = useI18n()

    const validUploadedFiles = computed(() =>
      Array.isArray(props.uploadedFiles)
        ? props.uploadedFiles.filter((f) => f && typeof f === 'object' && typeof f.type === 'string')
        : []
    )

    // --- Merge backend docs and new uploads for preview ---
    const allFilesForPreview = computed(() => {
      const backendDocs = (props.challengeDocsResponseList || []).map((doc) => ({ ...doc, _isBackend: true }))
      const newFiles = validUploadedFiles.value.map((file) => ({ ...file, _isBackend: false }))
      return [...backendDocs, ...newFiles]
    })

    // Ensure backend doc IDs are in challengeRequest.documentsIds
    onMounted(() => {
      if (
        props.challengeDocsResponseList &&
        props.challengeRequest &&
        Array.isArray(props.challengeRequest.documentsIds)
      ) {
        props.challengeDocsResponseList.forEach((doc) => {
          if (!props.challengeRequest.documentsIds.includes(doc.id)) {
            props.challengeRequest.documentsIds.push(doc.id)
          }
        })
      }
    })
    watch(
      () => props.challengeDocsResponseList,
      (newVal) => {
        if (newVal && props.challengeRequest && Array.isArray(props.challengeRequest.documentsIds)) {
          newVal.forEach((doc) => {
            if (!props.challengeRequest.documentsIds.includes(doc.id)) {
              props.challengeRequest.documentsIds.push(doc.id)
            }
          })
        }
      },
      { immediate: true, deep: true }
    )

    // --- YouTube Single Input Logic ---
    const youtubeInputField = ref('')

    const addYoutubeTextUrl = () => {
      const newLink = (youtubeInputField.value ?? '').trim()
      if (!newLink) return
      if (props.youtubeLinks.includes(newLink)) {
        customNotify('warn', t('challenge.info.youtubeLinkAlreadyAdded'))
        return
      }
      emit('update:youtubeLinks', [...props.youtubeLinks, newLink])
      youtubeInputField.value = ''
    }

    const removeYoutubeLinkByValue = (youtubeLink: string) => {
      emit(
        'update:youtubeLinks',
        props.youtubeLinks.filter((item: string) => item !== youtubeLink)
      )
    }

    const openYoutubeLink = (link: string) => {
      window.open(link, '_blank')
    }

    // Local refs and syncing for inputText
    const inputTextLocal = ref(props.inputText)
    const hashtagInputDirection = computed(() => getTextDirection(inputTextLocal.value))
    watch(
      () => props.inputText,
      (val) => {
        inputTextLocal.value = val
      }
    )
    watch(inputTextLocal, (val) => {
      emit('update:inputText', val)
    })

    // Local refs and syncing for newReminder
    const newReminderLocal = ref(props.newReminder)
    watch(
      () => props.newReminder,
      (val) => {
        newReminderLocal.value = val
      }
    )
    watch(newReminderLocal, (val) => {
      emit('update:newReminder', val)
    })

    const updateFilesModel = (val) => emit('update:filesModel', val)
    const clearAllFiles = () => emit('clearAllFiles')
    const removeFile = (index) => emit('removeFile', index)
    const addHashtag = () => {
      if ((inputTextLocal.value ?? '').trim()) {
        emit('addHashtag', inputTextLocal.value)
        inputTextLocal.value = ''
      }
    }
    const removeHashtag = (index) => emit('removeHashtag', index)
    const formatReminder = (reminder: string) => {
      if (!reminder) return ''
      const date = new Date(reminder)
      if (isNaN(date.getTime())) return reminder
      return format(date, 'yyyy-MM-dd HH:mm:ss')
    }
    const addReminder = () => {
      if ((newReminderLocal.value ?? '').trim()) {
        const date = new Date(newReminderLocal.value)
        if (isNaN(date.getTime()) || date <= new Date()) {
          customNotify('warn', t('challenge.info.reminderMustBeFuture'))
          return
        }
        if (props.challengeRequest.challengeRemindersDateTimeList.length >= 2) {
          customNotify('warn', t('Messages.error.challenge.maxReminders'))
          return
        }
        emit('addReminder', newReminderLocal.value)
        newReminderLocal.value = ''
      }
    }

    const removeReminder = (index: number) => {
      // Create a new array without mutating the original
      const updatedReminders = [...props.challengeRequest.challengeRemindersDateTimeList]
      updatedReminders.splice(index, 1) // Remove the item at the specified index

      // Emit the update to the parent
      emit('update:challengeRequest', {
        ...props.challengeRequest,
        challengeRemindersDateTimeList: updatedReminders
      })
    }

    const clearReminders = () => emit('clearReminders')

    // --- Backend Docs Logic ---
    const isImage = (type) => {
      if (!type) return false
      const t = type.toLowerCase()
      return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(t) || t.startsWith('image/')
    }

    const removeBackendDoc = (index: number, file: any) => {
      emit('removeBackendDoc', { index, file }) // Use kebab-case and object parameter
    }

    const getFileKindLabel = (file: any) => {
      if (!file?.type) {
        return 'FILE'
      }

      const type = String(file.type).toUpperCase()
      if (type.startsWith('IMAGE/')) {
        return 'IMAGE'
      }

      return type.replace(/^APPLICATION\//, '')
    }

    const openFilePreview = (file: any) => {
      if (file?._isBackend && isImage(file.type)) {
        window.open(`data:image/*;base64,${file.content}`, '_blank')
        return
      }

      if (file?._isBackend && file.type === 'pdf') {
        window.open(`data:application/pdf;base64,${file.content}`, '_blank')
        return
      }

      if (file?.preview) {
        window.open(file.preview, '_blank')
        return
      }

      if (file?.file instanceof File) {
        const objectUrl = URL.createObjectURL(file.file)
        window.open(objectUrl, '_blank')
      }
    }

    return {
      validUploadedFiles,
      youtubeInputField,
      addYoutubeTextUrl,
      removeYoutubeLinkByValue,
      openYoutubeLink,
      inputTextLocal,
      hashtagInputDirection,
      newReminderLocal,
      updateFilesModel,
      clearAllFiles,
      removeFile,
      addHashtag,
      removeHashtag,
      addReminder,
      clearReminders,
      isImage,
      allFilesForPreview,
      formatReminder,
      removeReminder,
      removeBackendDoc,
      getFileKindLabel,
      openFilePreview,
      getTextDirection
    }
  }
})
</script>

<style scoped lang="scss">
.resource-section {
  background: linear-gradient(180deg, #ffffff 0%, #f9fbfd 100%);
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 20px;
}

.custom-add-youtube-link-plus {
  align-self: center;
  margin-bottom: 2%;
}

.resource-section__input-row {
  align-items: center;
  display: flex;
  gap: 16px;
}

.resource-section__actions {
  align-items: center;
  display: flex;
  flex-shrink: 0;
  gap: 12px;
}

.resource-section__action-btn {
  align-self: center;
  margin-bottom: 45%;
}

.reminder-section__action-btn {
  align-self: center;
  margin-bottom: 25%;
}

.challenge-text-direction-field :deep(input) {
  text-align: start;
}

.challenge-text-direction-content {
  display: inline-block;
  text-align: start;
  unicode-bidi: plaintext;
}

:deep(.v-chip__content),
:deep(.v-btn__content) {
  align-items: center;
  display: inline-flex;
  gap: 8px;
}

.files-preview-section {
  margin-top: 20px;
}

.file-card {
  border: 1px solid #dbe5f0;
  border-radius: 22px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  transition: transform 0.2s ease,
  box-shadow 0.2s ease;
}

.file-card:hover {
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.12);
  transform: translateY(-4px);
}

.file-card__preview {
  background: linear-gradient(180deg, #f8fbff 0%, #eef4fa 100%);
  min-height: 168px;
  padding: 12px;
  position: relative;
}

.file-card__badge {
  background: rgba(7, 163, 93, 0.12);
  border: 1px solid rgba(7, 163, 93, 0.18);
  border-radius: 999px;
  color: #047857;
  font-size: 11px;
  font-weight: 700;
  left: 12px;
  letter-spacing: 0.08em;
  padding: 6px 10px;
  position: absolute;
  top: 12px;
  z-index: 1;
}

.file-card__embed-shell,
.file-preview-image,
.file-icon-container {
  border-radius: 16px;
  overflow: hidden;
}

.file-icon-container {
  align-items: center;
  background: rgba(255, 255, 255, 0.88);
  display: flex;
  height: 168px;
  justify-content: center;
}

.file-card__content {
  padding: 14px 16px 8px;
}

.file-card__name {
  color: #0f172a;
  font-size: 14px;
  font-weight: 700;
  line-height: 1.45;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.file-card__meta-item {
  background: #f1f5f9;
  border-radius: 999px;
  color: #475569;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
}

.file-card__actions {
  align-items: center;
  display: flex;
  gap: 8px;
  justify-content: space-between;
  padding: 0 12px 12px;
}

.file-card__action-btn :deep(.v-btn__content) {
  align-items: center;
  display: inline-flex;
  gap: 8px;
}

.empty-files-state {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border: 1px dashed #cbd5e1;
  border-radius: 18px;
}

@media (max-width: 960px) {
  .resource-section {
    padding: 16px;
  }

  .resource-section__input-row,
  .resource-section__actions {
    align-items: stretch;
    flex-direction: column;
  }

  .resource-section__actions {
    width: 100%;
  }

  .resource-section__action-btn {
    width: 100%;
    margin-bottom: 45%;
  }

  .reminder-section__action-btn {
    width: 100%;
    margin-bottom: 25%;
  }

  .file-card__actions {
    align-items: stretch;
    flex-direction: column;
  }

  .file-card__action-btn {
    width: 100%;
  }
}
</style>
