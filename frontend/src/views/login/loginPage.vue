<script setup lang="ts">
import logo from '@/assets/images/logo/AppLogo.png'
import { ref } from 'vue'

const form = ref({
  email: '',
  password: '',
  remember: false
})

const isPasswordVisible = ref(false)
</script>

<template>
  <v-col cols="3"></v-col>
  <v-col cols="6">
    <div class="auth-wrapper">
      <VCard class="auth-card" max-width="800">
        <VCardItem class="justify-center">
          <RouterLink to="/" class="d-flex align-center gap-3">
            <img :src="logo" alt="Logo" class="logo" />
          </RouterLink>
        </VCardItem>

        <VCardText class="pt-2">
          <h4 class="text-h4 mb-1">Welcome to Materio! 👋🏻</h4>
          <p class="mb-0">Please sign-in to your account and start the adventure</p>
        </VCardText>

        <VCardText>
          <VForm @submit.prevent="() => {}">
            <VRow>
              <VCol cols="12">
                <VTextField v-model="form.email" label="Email" type="email" />
              </VCol>

              <VCol cols="12">
                <VTextField
                  v-model="form.password"
                  label="Password"
                  placeholder="············"
                  :type="isPasswordVisible ? 'text' : 'password'"
                  :append-inner-icon="isPasswordVisible ? 'ri-eye-off-line' : 'ri-eye-line'"
                  @click:append-inner="isPasswordVisible = !isPasswordVisible"
                />

                <div class="d-flex align-center justify-space-between flex-wrap my-6">
                  <VCheckbox v-model="form.remember" label="Remember me" />
                  <a class="text-primary" href="javascript:void(0)">Forgot Password?</a>
                </div>

                <VBtn block type="submit" to="/">Login</VBtn>
              </VCol>

              <VCol cols="12" class="text-center text-base">
                <span>New on our platform?</span>
                <RouterLink class="text-primary ms-2" to="/register">Create an account</RouterLink>
              </VCol>

              <VCol cols="12" class="d-flex align-center">
                <VDivider />
                <span class="mx-4">or</span>
                <VDivider />
              </VCol>

              <VCol cols="12" class="text-center"></VCol>
            </VRow>
          </VForm>
        </VCardText>
      </VCard>

      <VImg class="auth-footer-mask d-none d-md-block" :src="authThemeMask" />
    </div>
  </v-col>
  <v-col cols="3"></v-col>
</template>

<style lang="scss">
* {
  box-sizing: border-box; /* Ensure padding is included in width calculations */
}

.auth-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh; /* Full viewport height */
  width: 100%; /* Full width of the viewport */
  padding: 16px; /* Optional padding */
}

.auth-card {
  padding: 32px; /* Increased padding */
  width: 100%; /* Make the card responsive */
  max-width: 800px; /* Limit the card width */
}
</style>
