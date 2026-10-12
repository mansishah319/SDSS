<!--
  - * ***************************************************************
  - * Copyleft ©1956-2024 DubaiPolice. All lefts reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<!-- SpeakerBubble.vue -->
<template>
  <div class="bubble bubble-bottom-right">
    <slot></slot>
  </div>
</template>

<script setup></script>

<style scoped>
.bubble {
  position: relative;
  line-height: 24px;
  max-width: 300px;
  background: #c8e6c9;
  border-radius: 25px;
  color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  text-align: center;
  margin: 0;
}

.bubble-bottom-right {
  margin-left: auto;
}

.bubble-bottom-right:before {
  content: '';
  width: 0px;
  height: 0px;
  position: absolute;
  border-right: 20px solid transparent;
  border-left: 24px solid transparent;
  border-top: 12px solid transparent;
  border-bottom: 20px solid #c8e6c9;
  right: 10px;
  bottom: -10px;
  transform: rotate(45deg);
}
</style>
