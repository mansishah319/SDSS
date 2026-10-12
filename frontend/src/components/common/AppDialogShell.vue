<script setup lang="ts">
import { computed } from 'vue'

type DialogTone = 'primary' | 'success' | 'warning' | 'error' | 'neutral'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    subtitle?: string
    icon?: string
    iconColor?: string
    maxWidth?: string | number
    persistent?: boolean
    showClose?: boolean
    tone?: DialogTone
    contentClass?: string
  }>(),
  {
    subtitle: '',
    icon: 'mdi-shape-outline',
    iconColor: 'primary',
    maxWidth: 760,
    persistent: false,
    showClose: true,
    tone: 'primary',
    contentClass: ''
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const toneClass = computed(() => `app-dialog-shell--${props.tone}`)
const normalizedSubtitle = computed(() => props.subtitle?.trim() || '')
const titlesClass = computed(() =>
  normalizedSubtitle.value ? 'app-dialog-shell__titles' : 'app-dialog-shell__titles app-dialog-shell__titles--centered'
)

const closeDialog = () => {
  emit('update:modelValue', false)
  emit('close')
}

const handleModelChange = (value: boolean) => {
  emit('update:modelValue', value)

  if (!value) {
    emit('close')
  }
}
</script>

<template>
  <v-dialog
    class="app-dialog-shell"
    :model-value="modelValue"
    :max-width="maxWidth"
    :persistent="persistent"
    scrollable
    @update:model-value="handleModelChange"
  >
    <v-card class="app-dialog-shell__card" :class="toneClass" rounded="xl" elevation="0">
      <div class="app-dialog-shell__hero">
        <div class="app-dialog-shell__mesh" />

        <div class="app-dialog-shell__hero-content">
          <div class="app-dialog-shell__top-row">
            <div class="app-dialog-shell__identity">
              <div class="app-dialog-shell__icon-wrap">
                <v-icon :color="iconColor">{{ icon }}</v-icon>
              </div>

              <div :class="titlesClass">
                <h3 class="app-dialog-shell__title font-bukra-bold">{{ title }}</h3>
                <p v-if="normalizedSubtitle" class="app-dialog-shell__subtitle">{{ normalizedSubtitle }}</p>
              </div>
            </div>

            <v-btn
              v-if="showClose"
              class="app-dialog-shell__close"
              icon="mdi-close"
              size="small"
              variant="text"
              @click="closeDialog"
            />
          </div>

          <div v-if="$slots.meta" class="app-dialog-shell__meta">
            <slot name="meta" />
          </div>
        </div>
      </div>

      <v-card-text v-if="$slots.default" class="app-dialog-shell__content" :class="contentClass">
        <slot />
      </v-card-text>

      <v-divider v-if="$slots.actions" />

      <v-card-actions v-if="$slots.actions" class="app-dialog-shell__actions">
        <slot name="actions" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="scss">
.app-dialog-shell {
  :deep(.v-overlay__content) {
    padding: 16px;
    width: min(100%, calc(100vw - 24px));
    max-width: min(100%, calc(100vw - 24px));
  }
}

.app-dialog-shell__card {
  display: flex;
  flex-direction: column;
  max-height: calc(100dvh - 32px);
  overflow: hidden;
  border: 1px solid rgba(13, 82, 62, 0.12);
  background: linear-gradient(180deg, #ffffff 0%, #f8fcfa 100%);
  box-shadow: 0 28px 60px rgba(9, 39, 31, 0.14);
}

.app-dialog-shell__hero {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0d6b4d 0%, #14805e 48%, #0d5c45 100%);
}

.app-dialog-shell--success .app-dialog-shell__hero {
  background: linear-gradient(135deg, #0b6b4e 0%, #14805e 48%, #0d5c45 100%);
}

.app-dialog-shell--warning .app-dialog-shell__hero {
  background: linear-gradient(135deg, #8a5a05 0%, #b97706 48%, #8f5c00 100%);
}

.app-dialog-shell--error .app-dialog-shell__hero {
  background: linear-gradient(135deg, #9f2a23 0%, #c24134 48%, #842a23 100%);
}

.app-dialog-shell--neutral .app-dialog-shell__hero {
  background: linear-gradient(135deg, #23343a 0%, #334155 48%, #1e293b 100%);
}

.app-dialog-shell__mesh {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
  linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 24px 24px;
  opacity: 0.45;
  pointer-events: none;
}

.app-dialog-shell__hero-content {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 14px;
  padding: 22px 24px 20px;
}

.app-dialog-shell__top-row,
.app-dialog-shell__identity {
  display: flex;
  align-items: flex-start;
}

.app-dialog-shell__top-row {
  justify-content: space-between;
  gap: 16px;
}

.app-dialog-shell__identity {
  gap: 14px;
  min-width: 0;
}

.app-dialog-shell__icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.14);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.14);
  flex-shrink: 0;
}

.app-dialog-shell__titles {
  min-width: 0;
  display: grid;
  gap: 6px;
}

.app-dialog-shell__titles--centered {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 48px;
}

.app-dialog-shell__title,
.app-dialog-shell__subtitle {
  margin: 0;
  color: #fff;
}

.app-dialog-shell__title {
  font-size: clamp(1.05rem, 2vw, 1.35rem);
  line-height: 1.3;
  word-break: break-word;
}

.app-dialog-shell__subtitle {
  color: rgba(255, 255, 255, 0.84);
  line-height: 1.55;
  max-width: 68ch;
}

.app-dialog-shell__close {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.app-dialog-shell__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.app-dialog-shell__content {
  padding: 22px;
  overflow: auto;
}

.app-dialog-shell__actions {
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 22px 18px;
  background: rgba(13, 82, 62, 0.02);
}

@media (max-width: 600px) {
  .app-dialog-shell :deep(.v-overlay__content) {
    padding: 10px;
  }

  .app-dialog-shell__hero-content,
  .app-dialog-shell__content,
  .app-dialog-shell__actions {
    padding-left: 16px;
    padding-right: 16px;
  }

  .app-dialog-shell__hero-content {
    padding-top: 18px;
    padding-bottom: 18px;
  }

  .app-dialog-shell__top-row,
  .app-dialog-shell__identity,
  .app-dialog-shell__actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .app-dialog-shell__close {
    align-self: flex-end;
  }

  .app-dialog-shell__subtitle {
    max-width: none;
  }

  .app-dialog-shell__actions :deep(.v-btn) {
    width: 100%;
  }
}
</style>
