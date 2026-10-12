<template>
  <v-btn icon class="custom-go-back-button" @click="goBack">
    <v-icon>{{ leftArrowIcon }}</v-icon>
  </v-btn>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import { useLeftArrowIcon } from '@/utils/rtlUtils'

export default defineComponent({
  name: 'CustomGoBackButton',
  props: {
    routeTo: {
      type: String,
      default: '' // Optional route to navigate to
    }
  },
  setup(props) {
    const router = useRouter()
    const leftArrowIcon = useLeftArrowIcon()

    const goBack = () => {
      if (props.routeTo) {
        // Navigate to the specific route provided as a prop
        router.push({ name: props.routeTo })
      } else {
        // Default behavior: go back to the previous page
        router.go(-1)
      }
    }

    return {
      goBack,
      leftArrowIcon
    }
  }
})
</script>

<style scoped lang="scss">
.custom-go-back-button {
  cursor: pointer;
}
</style>
