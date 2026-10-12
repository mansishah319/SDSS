<script setup lang="ts">
import type {
  ChallengeAttachmentViewModel,
  ChallengeMediaCardLabels,
  ChallengeMediaVideoViewModel
} from './challengeReport.types'

const props = defineProps<{
  videos: ChallengeMediaVideoViewModel[]
  hashtags: string[]
  attachments: ChallengeAttachmentViewModel[]
  labels: ChallengeMediaCardLabels
}>()

const emit = defineEmits<{
  'open-video': [video: ChallengeMediaVideoViewModel]
}>()

const downloadAttachment = (attachment: ChallengeAttachmentViewModel) => {
  if (!attachment.content) {
    return
  }

  const byteCharacters = atob(attachment.content)
  const byteNumbers = Array.from(byteCharacters, (char) => char.charCodeAt(0))
  const byteArray = new Uint8Array(byteNumbers)
  const blob = new Blob([byteArray], { type: 'application/octet-stream' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = attachment.name
  link.click()
  URL.revokeObjectURL(link.href)
}
</script>

<template>
  <v-card class="challenge-media-card" rounded="xl" elevation="0">
    <v-card-text class="challenge-media-card__body">
      <div>
        <p class="challenge-media-card__eyebrow">{{ labels.subtitle }}</p>
        <h3 class="challenge-media-card__title font-bukra-bold">{{ labels.title }}</h3>
      </div>

      <section class="challenge-media-card__section">
        <div class="challenge-media-card__section-head">
          <span>{{ labels.hashtags }}</span>
          <v-chip size="x-small" color="primary" variant="tonal">{{ hashtags.length }}</v-chip>
        </div>

        <div v-if="hashtags.length" class="challenge-media-card__hashtags">
          <v-chip v-for="hashtag in hashtags" :key="hashtag" color="primary" variant="outlined" size="small">
            #{{ hashtag }}
          </v-chip>
        </div>
        <div v-else class="challenge-media-card__empty">{{ labels.noHashtags }}</div>
      </section>

      <section class="challenge-media-card__section">
        <div class="challenge-media-card__section-head">
          <span>{{ labels.attachments }}</span>
          <v-chip size="x-small" color="success" variant="tonal">{{ attachments.length }}</v-chip>
        </div>

        <div v-if="attachments.length" class="challenge-media-card__attachments">
          <article v-for="attachment in attachments" :key="attachment.id" class="challenge-media-card__attachment">
            <div v-if="attachment.previewUrl" class="challenge-media-card__attachment-preview">
              <img :src="attachment.previewUrl" :alt="attachment.name" />
            </div>
            <div v-else class="challenge-media-card__attachment-icon" :class="`tone-${attachment.color}`">
              <v-icon>{{ attachment.icon }}</v-icon>
            </div>

            <div class="challenge-media-card__attachment-main">
              <strong class="challenge-media-card__attachment-name">{{ attachment.name }}</strong>
              <span class="challenge-media-card__attachment-meta">
                {{ attachment.extension.toUpperCase() }}
                <template v-if="attachment.sizeLabel"> • {{ attachment.sizeLabel }}</template></span
              >
            </div>

            <v-btn
              :disabled="!attachment.downloadable"
              icon
              variant="tonal"
              color="primary"
              @click="downloadAttachment(attachment)"
            >
              <v-icon>mdi-download</v-icon>
              <v-tooltip activator="parent" location="top">{{ labels.download }}</v-tooltip>
            </v-btn>
          </article>
        </div>
        <div v-else class="challenge-media-card__empty">{{ labels.noAttachments }}</div>
      </section>

      <section class="challenge-media-card__section">
        <div class="challenge-media-card__section-head">
          <span>{{ labels.videos }}</span>
          <v-chip size="x-small" color="info" variant="tonal">{{ videos.length }}</v-chip>
        </div>

        <div v-if="videos.length" class="challenge-media-card__videos">
          <article v-for="video in videos" :key="video.id" class="challenge-media-card__video">
            <button
              type="button"
              class="challenge-media-card__video-preview"
              :aria-label="video.label"
              @click="emit('open-video', video)"
            >
              <img
                v-if="video.thumbnailUrl"
                class="challenge-media-card__video-image"
                :src="video.thumbnailUrl"
                :alt="video.label"
                crossorigin="anonymous"
                loading="lazy"
                decoding="async"
                referrerpolicy="strict-origin-when-cross-origin"
              />

              <div v-else class="challenge-media-card__video-fallback">
                <v-icon size="40">mdi-youtube</v-icon>
              </div>

              <div class="challenge-media-card__video-badge">YouTube</div>
              <div class="challenge-media-card__video-play">
                <v-icon size="28">mdi-play</v-icon>
              </div>
            </button>

            <div class="challenge-media-card__video-footer">
              <span class="challenge-media-card__video-label">{{ video.label }}</span>
              <div class="challenge-media-card__video-actions">
                <v-btn variant="flat" color="primary" @click="emit('open-video', video)">
                  {{ labels.openVideo }}
                </v-btn>
                <v-btn :href="video.link" target="_blank" rel="noopener noreferrer" variant="text" color="primary">
                  {{ labels.openExternally }}
                </v-btn>
              </div>
            </div>
          </article>
        </div>
        <div v-else class="challenge-media-card__empty">{{ labels.noVideos }}</div>
      </section>
    </v-card-text>
  </v-card>
</template>

<style scoped lang="scss">
.challenge-media-card {
  border: 1px solid rgba(13, 82, 62, 0.1);
  background: #fff;
}

.challenge-media-card__body {
  display: grid;
  gap: 20px;
  padding: 22px;
}

.challenge-media-card__eyebrow,
.challenge-media-card__attachment-meta {
  color: #73837d;
}

.challenge-media-card__eyebrow {
  margin: 0;
  font-size: 0.8rem;
}

.challenge-media-card__title {
  margin: 8px 0 0;
  color: #16362d;
}

.challenge-media-card__section {
  display: grid;
  gap: 12px;
}

.challenge-media-card__section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #314843;
  font-weight: 700;
}

.challenge-media-card__videos,
.challenge-media-card__attachments {
  display: grid;
  gap: 12px;
}

.challenge-media-card__video,
.challenge-media-card__attachment {
  border: 1px solid rgba(13, 82, 62, 0.08);
  border-radius: 20px;
  overflow: hidden;
  background: #fbfdfc;
}

.challenge-media-card__iframe {
  display: none;
}

.challenge-media-card__video-preview {
  position: relative;
  display: block;
  width: 100%;
  min-height: 220px;
  overflow: hidden;
  border: 0;
  padding: 0;
  cursor: pointer;
  text-align: start;
  background: linear-gradient(180deg, #dff4ec 0%, #f6fbf9 100%);
}

.challenge-media-card__video-preview::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(8, 47, 37, 0.04) 0%, rgba(8, 47, 37, 0.22) 100%);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.challenge-media-card__video-preview:hover::after,
.challenge-media-card__video-preview:focus-visible::after {
  opacity: 1;
}

.challenge-media-card__video-image,
.challenge-media-card__video-fallback {
  width: 100%;
  min-height: 220px;
  display: block;
}

.challenge-media-card__video-image {
  object-fit: cover;
}

.challenge-media-card__video-fallback {
  display: grid;
  place-items: center;
  color: #0d6b4d;
}

.challenge-media-card__video-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  z-index: 1;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(11, 107, 78, 0.9);
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
}

.challenge-media-card__video-play {
  position: absolute;
  inset-inline-start: 50%;
  top: 50%;
  z-index: 1;
  transform: translate(-50%, -50%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  color: #0d6b4d;
  box-shadow: 0 10px 24px rgba(9, 35, 29, 0.14);
}

.challenge-media-card__video-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
}

.challenge-media-card__video-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.challenge-media-card__video-label,
.challenge-media-card__attachment-name {
  color: #16362d;
  overflow-wrap: anywhere;
}

.challenge-media-card__hashtags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.challenge-media-card__attachment {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 12px 14px;
}

.challenge-media-card__attachment-preview,
.challenge-media-card__attachment-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;
}

.challenge-media-card__attachment-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.challenge-media-card__attachment-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(13, 82, 62, 0.08);
  color: #0d6b4d;
}

.challenge-media-card__attachment-icon.tone-error {
  background: rgba(240, 68, 56, 0.12);
  color: #d9362c;
}

.challenge-media-card__attachment-icon.tone-success {
  background: rgba(18, 183, 106, 0.12);
  color: #129f63;
}

.challenge-media-card__attachment-icon.tone-primary {
  background: rgba(21, 112, 239, 0.12);
  color: #1570ef;
}

.challenge-media-card__attachment-main {
  min-width: 0;
  display: grid;
  gap: 4px;
}

.challenge-media-card__empty {
  padding: 16px;
  border-radius: 18px;
  background: #f8fbfa;
  color: #6f817a;
  text-align: center;
}

@media (max-width: 600px) {
  .challenge-media-card__body {
    padding: 18px;
  }

  .challenge-media-card__video-footer,
  .challenge-media-card__video-actions,
  .challenge-media-card__attachment {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: flex-start;
  }

  .challenge-media-card__video-actions {
    justify-content: flex-start;
  }
}
</style>
