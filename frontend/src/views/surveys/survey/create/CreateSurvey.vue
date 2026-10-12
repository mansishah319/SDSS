<!--
  - * ***************************************************************
  - * Copyright ©1956-2026 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<script lang="ts">
import { defineComponent, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'CreateSurvey',
  emits: ['close'],
  setup(_, { emit }) {
    const router = useRouter()

    onMounted(async () => {
      emit('close')
      await router.push({ name: 'SdssSurveyCreate' })
    })

    return {}
  }
})
</script>

<template>
  <div></div>
</template>
