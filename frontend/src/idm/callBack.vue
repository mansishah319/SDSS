<template src="./callback.html" lang="html"></template>
<script lang="ts">
import Wso2Service, { WSO2_AUTH_REDIRECT_KEY } from '@/idm/Wso2Service'
import { defineComponent, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  setup() {
    const router = useRouter()

    /** MOUNTED HOOK */
    onMounted(async () => {
      const savedRedirect = sessionStorage.getItem(WSO2_AUTH_REDIRECT_KEY)
      const redirectTarget = savedRedirect?.startsWith('/') && !savedRedirect.startsWith('//') ? savedRedirect : '/'
      sessionStorage.removeItem(WSO2_AUTH_REDIRECT_KEY)

      const success = await Wso2Service.handleCallback()
      await router.replace(success ? redirectTarget : { name: 'LoginPage' })
    })

    /** RETURNING BLOCK */
    return {}
  }
})
</script>

<style scoped>
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #097e5c;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  pointer-events: all;
}

.fade-in {
  font-size: 1.5rem;
  color: #fff;
  animation: fadeIn 1.5s ease-in-out;
}

.loader {
  width: 50px;
  height: 50px;
  border: 5px solid #ccc;
  border-top: 5px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 10px auto;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
