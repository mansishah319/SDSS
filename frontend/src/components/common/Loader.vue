<template>
  <v-overlay v-model="visible" class="align-center justify-center" :absolute="false" :opacity="0.38" :z-index="5">
    <ContentLoader
      :variant="variant"
      :compact-badge="compactBadge"
      :message="resolvedMessage"
      :heading="resolvedHeading"
      :description="resolvedDescription"
    />
  </v-overlay>
</template>

<script lang="ts">
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import ContentLoader from '@/components/loader/ContentLoader.vue'

export default {
  name: 'DubaiPoliceLoader',
  components: {
    ContentLoader
  },
  props: {
    isLoading: {
      type: Boolean,
      required: true
    },
    variant: {
      type: String,
      default: 'badge'
    },
    compactBadge: {
      type: Boolean,
      default: true
    },
    message: {
      type: String,
      default: ''
    },
    heading: {
      type: String,
      default: ''
    },
    description: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const { t } = useI18n()
    const visible = ref(props.isLoading)
    const resolvedMessage = computed(() => props.message || t('common.loader.content'))
    const resolvedHeading = computed(() => props.heading || '')
    const resolvedDescription = computed(() => props.description || '')

    watch(
      () => props.isLoading,
      (newVal) => {
        visible.value = newVal
      }
    )

    return {
      resolvedDescription,
      resolvedHeading,
      resolvedMessage,
      visible
    }
  }
}
</script>
