<template>
  <div
    class="content-loader"
    :class="[
      `content-loader--${variant}`,
      {
        'content-loader--overlay': overlay,
        'content-loader--compact-badge': compactBadge,
        'content-loader--with-intro': showIntro,
      },
    ]"
  >
    <div class="content-loader__badge">
      <img :src="loaderGifUrl" :alt="loadingAlt" />
    </div>

    <div v-if="showIntro" class="content-loader__intro">
      <div v-if="heading" class="content-loader__heading">{{ heading }}</div>
      <div v-if="message" class="content-loader__message">{{ message }}</div>
      <div v-if="description" class="content-loader__description">{{ description }}</div>
    </div>

    <div v-if="hasContentSlot" class="content-loader__stack">
      <slot name="content" />
    </div>

    <div v-else-if="variant === 'badge'" class="content-loader__stack content-loader__stack--badge-only">
      <div v-if="message" class="content-loader__message">{{ message }}</div>
    </div>

    <div v-else-if="variant === 'dashboard'" class="content-loader__stack">
      <div class="content-loader__grid content-loader__grid--hero">
        <v-skeleton-loader
          type="heading, text, text, actions"
          class="content-loader__card content-loader__card--metric"
        />
        <v-skeleton-loader
          type="heading, text, text, actions"
          class="content-loader__card content-loader__card--metric"
        />
        <v-skeleton-loader
          type="heading, text, text, actions"
          class="content-loader__card content-loader__card--metric"
        />
      </div>

      <div class="content-loader__grid content-loader__grid--dashboard-main">
        <v-skeleton-loader type="heading, image" class="content-loader__card content-loader__card--summary" />
        <div class="content-loader__stack content-loader__stack--tight">
          <v-skeleton-loader type="heading, article" class="content-loader__card content-loader__card--list" />
          <v-skeleton-loader type="heading, article" class="content-loader__card content-loader__card--list" />
        </div>
      </div>

      <div class="content-loader__grid content-loader__grid--dashboard-bottom">
        <v-skeleton-loader type="heading, image" class="content-loader__card content-loader__card--chart" />
        <v-skeleton-loader type="heading, image" class="content-loader__card content-loader__card--chart" />
      </div>
    </div>

    <div v-else-if="variant === 'details'" class="content-loader__stack">
      <v-skeleton-loader
        type="heading, paragraph, actions"
        class="content-loader__card content-loader__card--details-hero"
      />

      <div class="content-loader__grid content-loader__grid--details-halves">
        <v-skeleton-loader type="heading, article" class="content-loader__card content-loader__card--details-half" />
        <v-skeleton-loader type="heading, article" class="content-loader__card content-loader__card--details-half" />
      </div>

      <v-skeleton-loader
        type="heading, article, article"
        class="content-loader__card content-loader__card--details-wide"
      />
      <v-skeleton-loader
        type="heading, table-thead, table-row-divider@5"
        class="content-loader__card content-loader__card--details-table"
      />
    </div>

    <div v-else-if="variant === 'analytics'" class="content-loader__stack">
      <div class="content-loader__grid content-loader__grid--analytics-kpis">
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--kpi" />
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--kpi" />
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--kpi" />
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--kpi" />
      </div>

      <div class="content-loader__grid content-loader__grid--analytics-main">
        <v-skeleton-loader type="heading, image" class="content-loader__card content-loader__card--analytics-chart" />
        <v-skeleton-loader
          type="heading, list-item-three-line@4"
          class="content-loader__card content-loader__card--analytics-side"
        />
      </div>
    </div>

    <div v-else-if="variant === 'form'" class="content-loader__stack">
      <v-skeleton-loader type="heading, paragraph" class="content-loader__card content-loader__card--form-hero" />

      <div class="content-loader__grid content-loader__grid--form-fields">
        <v-skeleton-loader type="text, text" class="content-loader__card content-loader__card--form-field" />
        <v-skeleton-loader type="text, text" class="content-loader__card content-loader__card--form-field" />
        <v-skeleton-loader type="text, text" class="content-loader__card content-loader__card--form-field" />
        <v-skeleton-loader type="text, text" class="content-loader__card content-loader__card--form-field" />
      </div>

      <v-skeleton-loader
        type="heading, paragraph, paragraph, actions"
        class="content-loader__card content-loader__card--form-wide"
      />
    </div>

    <div v-else-if="variant === 'table-mobile'" class="content-loader__stack">
      <v-skeleton-loader
        type="list-item-three-line@5"
        class="content-loader__card content-loader__card--mobile-table"
      />
    </div>

    <div v-else-if="variant === 'inline'" class="content-loader__stack content-loader__stack--inline">
      <v-skeleton-loader type="article, text" class="content-loader__card content-loader__card--inline" />
      <div v-if="message" class="content-loader__message">{{ message }}</div>
    </div>

    <div v-else class="content-loader__stack">
      <div class="content-loader__grid content-loader__grid--table-toolbar">
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--toolbar" />
        <v-skeleton-loader type="heading, text" class="content-loader__card content-loader__card--toolbar" />
      </div>

      <div class="content-loader__grid content-loader__grid--table-filters">
        <v-skeleton-loader type="text" class="content-loader__card content-loader__card--filter" />
        <v-skeleton-loader type="text" class="content-loader__card content-loader__card--filter" />
        <v-skeleton-loader type="text" class="content-loader__card content-loader__card--filter" />
        <v-skeleton-loader type="text" class="content-loader__card content-loader__card--filter" />
      </div>

      <v-skeleton-loader
        type="table-thead, table-row-divider@6"
        class="content-loader__card content-loader__card--table"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue'
import { useI18n } from 'vue-i18n'

const props = withDefaults(
  defineProps<{
    variant?: 'badge' | 'dashboard' | 'details' | 'analytics' | 'form' | 'table-mobile' | 'inline' | 'table'
    overlay?: boolean
    compactBadge?: boolean
    message?: string
    heading?: string
    description?: string
  }>(),
  {
    variant: 'dashboard',
    message: '',
    heading: '',
    description: ''
  }
)

const slots = useSlots()
const { t } = useI18n()
const loaderGifUrl = new URL('../../assets/images/loader/loader.gif', import.meta.url).href
const variant = computed(() => props.variant)
const overlay = computed(() => props.overlay)
const compactBadge = computed(() => props.compactBadge)
const message = computed(() => props.message)
const heading = computed(() => props.heading)
const description = computed(() => props.description)
const hasContentSlot = computed(() => Boolean(slots.content))
const showIntro = computed(() => Boolean(heading.value || message.value || description.value))
const loadingAlt = computed(() => t('common.loading'))
</script>

<style lang="scss">
.content-loader {
  --content-loader-shell: rgba(255, 255, 255, 0.98);
  --content-loader-border: rgba(15, 23, 42, 0.06);
  --content-loader-shadow: 0 18px 40px rgba(16, 24, 40, 0.08);
  --content-loader-text: #12342a;
  --content-loader-muted: rgba(52, 64, 84, 0.76);

  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  isolation: isolate;

  &.content-loader--overlay {
    position: absolute;
    inset: 0;
    z-index: 3;
    padding: 16px;
    border-radius: 24px;
    background: linear-gradient(180deg, rgba(245, 248, 251, 0.72) 0%, rgba(244, 247, 249, 0.86) 100%);
    backdrop-filter: blur(6px);
    pointer-events: none;
  }
}

.content-loader__stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-loader__stack--tight {
  gap: 14px;
}

.content-loader__stack--inline,
.content-loader__stack--badge-only {
  align-items: center;
}

.content-loader__badge {
  width: 108px;
  height: 108px;
  margin: 0 auto;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0px 16px 40px rgba(16, 24, 40, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 4;
  border: 1px solid rgba(13, 82, 62, 0.08);

  img {
    width: 78px;
    height: 78px;
    object-fit: contain;
  }
}

.content-loader--compact-badge {
  .content-loader__badge {
    width: 84px;
    height: 84px;

    img {
      width: 58px;
      height: 58px;
    }
  }
}

.content-loader__intro {
  display: grid;
  gap: 6px;
  text-align: center;
  max-width: min(100%, 720px);
  margin: 0 auto;
}

.content-loader__heading {
  font-family: Bukra, DubaiRegular, Arial, sans-serif;
  font-size: clamp(1rem, 2.4vw, 1.3rem);
  color: var(--content-loader-text);
  font-weight: 700;
}

.content-loader__message {
  font-family: DubaiRegular, Arial, sans-serif;
  font-size: 15px;
  color: var(--content-loader-muted);
  text-align: center;
}

.content-loader__description {
  font-family: DubaiRegular, Arial, sans-serif;
  font-size: 0.95rem;
  color: rgba(71, 84, 103, 0.78);
  text-align: center;
}

.content-loader__grid {
  display: grid;
  gap: 20px;

  &.content-loader__grid--hero {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  &.content-loader__grid--summary {
    grid-template-columns: 1.1fr 1.9fr;
  }

  &.content-loader__grid--dashboard-main,
  &.content-loader__grid--analytics-main {
    grid-template-columns: minmax(0, 1.5fr) minmax(280px, 0.9fr);
  }

  &.content-loader__grid--dashboard-bottom {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  &.content-loader__grid--details-halves {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  &.content-loader__grid--analytics-kpis {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  &.content-loader__grid--form-fields,
  &.content-loader__grid--table-filters {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  &.content-loader__grid--table-toolbar {
    grid-template-columns: 1.3fr 0.7fr;
  }
}

.content-loader__card {
  border-radius: 18px;
  overflow: hidden;
  border: 1px solid var(--content-loader-border);
  background: var(--content-loader-shell);
  box-shadow: var(--content-loader-shadow);

  :deep(.v-skeleton-loader__image) {
    height: 100%;
    min-height: inherit;
  }

  :deep(.v-skeleton-loader__bone) {
    background: linear-gradient(
      90deg,
      rgba(233, 239, 244, 0.92) 25%,
      rgba(245, 248, 250, 1) 37%,
      rgba(233, 239, 244, 0.92) 63%
    );
  }

  :deep(.v-skeleton-loader__ossein) {
    background: transparent;
  }

  &.content-loader__card--hero {
    min-height: 330px;
  }

  &.content-loader__card--metric,
  &.content-loader__card--kpi {
    min-height: 172px;
  }

  &.content-loader__card--summary {
    min-height: 300px;
  }

  &.content-loader__card--list,
  &.content-loader__card--analytics-side {
    min-height: 140px;
  }

  &.content-loader__card--chart {
    min-height: 320px;
  }

  &.content-loader__card--analytics-chart {
    min-height: 360px;
  }

  &.content-loader__card--toolbar {
    min-height: 112px;
  }

  &.content-loader__card--filter {
    min-height: 92px;
  }

  &.content-loader__card--table {
    min-height: 520px;
  }

  &.content-loader__card--mobile-table {
    min-height: 320px;
  }

  &.content-loader__card--details-hero {
    min-height: 180px;
  }

  &.content-loader__card--details-half {
    min-height: 280px;
  }

  &.content-loader__card--details-wide {
    min-height: 220px;
  }

  &.content-loader__card--details-table {
    min-height: 420px;
  }

  &.content-loader__card--form-hero {
    min-height: 152px;
  }

  &.content-loader__card--form-field {
    min-height: 112px;
  }

  &.content-loader__card--form-wide {
    min-height: 260px;
  }

  &.content-loader__card--inline {
    width: min(100%, 420px);
    min-height: 112px;
  }
}

.content-loader--badge {
  align-items: center;
  justify-content: center;
}

.content-loader--details {
  max-width: 100%;
}

.content-loader--inline {
  min-height: 180px;
  align-items: center;
  justify-content: center;
}

@media (max-width: 1260px) {
  .content-loader__grid {
    &.content-loader__grid--hero {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    &.content-loader__grid--summary {
      grid-template-columns: 1fr;
    }

    &.content-loader__grid--dashboard-main,
    &.content-loader__grid--dashboard-bottom,
    &.content-loader__grid--analytics-main,
    &.content-loader__grid--analytics-kpis,
    &.content-loader__grid--table-toolbar {
      grid-template-columns: 1fr;
    }

    &.content-loader__grid--details-halves {
      grid-template-columns: 1fr;
    }

    &.content-loader__grid--form-fields,
    &.content-loader__grid--table-filters {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 960px) {
  .content-loader__grid {
    &.content-loader__grid--hero {
      grid-template-columns: 1fr;
    }

    &.content-loader__grid--analytics-kpis {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}

@media (max-width: 600px) {
  .content-loader {
    &.content-loader--overlay {
      padding: 8px;
    }
  }

  .content-loader__stack,
  .content-loader__grid {
    gap: 14px;
  }

  .content-loader__badge {
    width: 92px;
    height: 92px;

    img {
      width: 66px;
      height: 66px;
    }
  }

  .content-loader__grid {
    &.content-loader__grid--analytics-kpis {
      grid-template-columns: 1fr;
    }
  }
}
</style>
