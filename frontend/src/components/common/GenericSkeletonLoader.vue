<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <ContentLoader
    v-if="loading"
    :variant="resolvedVariant"
    :message="resolvedMessage"
    :description="resolvedDescription"
    :style="loaderStyle"
  />

  <div v-else>
    <slot />
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import ContentLoader from '@/components/loader/ContentLoader.vue'

export default defineComponent({
  name: 'GenericSkeletonLoader',
  components: {
    ContentLoader
  },
  props: {
    loading: {
      type: Boolean,
      required: true
    },
    height: {
      type: String,
      default: '20px'
    },
    width: {
      type: String,
      default: '100%'
    },
    type: {
      type: String as PropType<'text' | 'card' | 'list'>,
      default: 'text' // You can define other types as needed
    }
  },
  setup(props) {
    const { t } = useI18n()
    const resolvedVariant = computed(() => {
      if (props.type === 'card') {
        return 'details'
      }

      if (props.type === 'list') {
        return 'table-mobile'
      }

      return 'inline'
    })

    const resolvedMessage = computed(() => {
      if (props.type === 'card') {
        return t('common.loader.sectionContent')
      }

      if (props.type === 'list') {
        return t('common.loader.listItems')
      }

      return t('common.loader.content')
    })

    const resolvedDescription = computed(() => {
      return props.type === 'card' ? t('common.loader.sectionDescription') : ''
    })

    const loaderStyle = computed(() => ({
      minHeight: props.height,
      width: props.width
    }))

    return {
      loaderStyle,
      resolvedDescription,
      resolvedMessage,
      resolvedVariant
    }
  }
})
</script>

<style scoped></style>
