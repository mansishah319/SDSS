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
  name: 'UpdateSurvey',
  props: {
    gId: {
      type: Number,
      required: true
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const router = useRouter()

    onMounted(async () => {
      emit('close')
      await router.push({
        name: 'SdssSurveyUpdate',
        params: { surveyId: props.gId }
      })
    })

    return {}
  }
})
</script>

<template>
  <div></div>
</template>
