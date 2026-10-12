<!--
  - * ***************************************************************
  - * Copyright ©1956-2023 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <div class="not-found-data-container my-5 py-5">
    <div class="mt-3">
      <img src="../../../../assets/images/errors/404.png" :alt="$t('common.empty')" :width="imageSize" />
    </div>
    <div class="font-bukra-bold custom-not-found-message-style" :style="{ color: textColor }">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'ServerDownScreen',
  props: {
    imageSize: {
      type: [Number, String],
      default: 128
    },
    message: {
      type: String,
      default: 'لا يوجد بيانات'
    },
    textColor: {
      type: String,
      default: 'black' // Default text color is black
    }
  }
}
</script>

<style scoped lang="scss">
.not-found-data-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.custom-not-found-message-style {
  font-size: 0.8rem;
  padding-top: 2%;
}
</style>
