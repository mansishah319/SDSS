<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    sectionTitle: string
    sectionSubtitle?: string
    metaLabel?: string
    metaValue?: string | number | null
  }>(),
  {
    sectionSubtitle: '',
    metaLabel: '',
    metaValue: null
  }
)
</script>

<template>
  <div class="settings-dialog-form-shell">
    <div
      v-if="metaLabel && metaValue !== null && metaValue !== undefined && metaValue !== ''"
      class="settings-dialog-form-shell__meta"
    >
      <span class="settings-dialog-form-shell__meta-label">{{ metaLabel }}</span>
      <strong class="settings-dialog-form-shell__meta-value">{{ metaValue }}</strong>
    </div>

    <section class="settings-dialog-form-shell__section">
      <div class="settings-dialog-form-shell__section-head">
        <h4 class="settings-dialog-form-shell__section-title">{{ sectionTitle }}</h4>
        <p v-if="sectionSubtitle" class="settings-dialog-form-shell__section-copy">{{ sectionSubtitle }}</p>
      </div>

      <div class="settings-dialog-form-shell__body">
        <slot />
      </div>
    </section>

    <div class="settings-dialog-form-shell__actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.settings-dialog-form-shell {
  display: grid;
  gap: 18px;
}

.settings-dialog-form-shell__meta {
  align-items: center;
  background: linear-gradient(180deg, rgba(13, 82, 62, 0.06) 0%, rgba(13, 82, 62, 0.1) 100%);
  border: 1px solid rgba(13, 82, 62, 0.12);
  border-radius: 999px;
  color: #17473a;
  display: inline-flex;
  gap: 8px;
  justify-self: flex-start;
  padding: 8px 14px;
}

.settings-dialog-form-shell__meta-label {
  color: #5f756d;
  font-size: 0.82rem;
}

.settings-dialog-form-shell__meta-value {
  font-family: 'bukrabold', serif;
}

.settings-dialog-form-shell__section {
  background: linear-gradient(180deg, #ffffff 0%, #fbfdfc 100%);
  border: 1px solid rgba(13, 82, 62, 0.12);
  border-radius: 22px;
  padding: 22px;
}

.settings-dialog-form-shell__section-head {
  margin-bottom: 14px;
}

.settings-dialog-form-shell__section-title {
  color: #16392f;
  font-size: 1rem;
  font-weight: 700;
  margin: 0;
}

.settings-dialog-form-shell__section-copy {
  color: #667d75;
  font-size: 0.92rem;
  line-height: 1.6;
  margin: 6px 0 0;
}

.settings-dialog-form-shell__body {
  display: grid;
  gap: 12px;
}

.settings-dialog-form-shell__actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

@media (max-width: 600px) {
  .settings-dialog-form-shell__section {
    padding: 16px;
  }

  .settings-dialog-form-shell__actions {
    flex-direction: column-reverse;
  }

  .settings-dialog-form-shell__actions :deep(.v-btn) {
    width: 100%;
  }
}
</style>
