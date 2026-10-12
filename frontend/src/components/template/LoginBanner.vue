<template>
  <div class="application-banner-div">
    <img src="@/assets/images/login/login-banner-bg.png" class="bg-img" alt="A Login Banner" />
  </div>
</template>

<style lang="scss" scoped>
.application-banner-div {
  min-height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin: 0 auto 0 auto;

  .bg-img {
    position: absolute;
    z-index: 0;
    width: 100%;
    object-fit: cover;
    top: 0;
  }
}
</style>
