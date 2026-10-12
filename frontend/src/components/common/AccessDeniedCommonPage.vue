<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <div class="empty-data-container my-5 py-5">
    <div class="mt-3">
      <img src="../../assets/images/errors/unAuth.svg" :alt="$t('common.auth.accessDeniedMsg')" :width="imageSize" />
    </div>
    <div class="mt-3 pb-2">
      <span class="font-bukra-bold custom-access-denied-msg-font"> {{ $t('common.auth.accessDeniedMsg') }} </span>
    </div>
    <div class="mt-3">
      <span class="font-bukra-bold"> {{ $t('common.auth.accessDeniedNote') }} </span>
    </div>
    <div class="mt-3">
      <BackButton :redirect-path="backRedirectPath" :title="backTitle" :icon="backIcon" :color="backColor" />
    </div>
  </div>
</template>

<script>
import BackButton from '@/components/common/BackButton.vue'

export default {
  name: 'AccessDeniedCommonPage',
  components: { BackButton },
  props: {
    imageSize: {
      type: [Number, String],
      default: 128
    },
    backRedirectPath: {
      type: String,
      default: 'home'
    },
    backIcon: {
      type: String,
      default: ''
    },
    backTitle: {
      type: String,
      default: 'common.back'
    },
    backColor: {
      type: String,
      default: 'primary'
    }
  }
}
</script>

<style scoped lang="scss">
.empty-data-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.custom-access-denied-msg-font {
  font-size: 1rem;
  color: #e74c3c;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 2px;
}
</style>
