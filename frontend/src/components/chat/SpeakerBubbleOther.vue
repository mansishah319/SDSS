<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<!-- SpeakerBubbleOther.vue -->
<template>
  <div class="bubble bubble-bottom-left">
    <slot></slot>
  </div>
</template>

<script setup></script>

<style scoped>
.bubble {
  position: relative;
  line-height: 24px;
  max-width: 300px;
  background: #afe7ff;
  border-radius: 25px;
  color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  text-align: center;
  margin: 0;
}

.bubble-bottom-left {
  margin-right: auto;
}

.bubble-bottom-left:before {
  content: '';
  width: 0px;
  height: 0px;
  position: absolute;
  border-right: 24px solid transparent;
  border-left: 20px solid transparent;
  border-top: 12px solid transparent;
  border-bottom: 20px solid #afe7ff;
  left: 10px;
  bottom: -10px;
  transform: rotate(-45deg);
}
</style>
