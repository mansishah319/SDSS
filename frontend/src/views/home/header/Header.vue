<script lang="ts">
import { computed, defineComponent, onUnmounted, ref } from 'vue'
import LanguageSwitcher from '@/components/locals/LocaleSwitcher.vue'
import { useI18n } from 'vue-i18n'
import { getEmployeeData, isHasAnyRole } from '@/utils/roleUtils.ts'
import UserProfileDropdown from '@/components/account/UserProfileDropdown.vue'
import Employee from '@/dto/Employee'
import futureForesightLogoUrl from '@/assets/images/logo/future-foresight-white.svg'

export default defineComponent({
  name: 'AppHeader',
  components: { UserProfileDropdown, LanguageSwitcher },
  props: {
    isSdssUserAuthorized: {
      type: Boolean,
      required: true
    }
  },
  setup() {
    const { locale } = useI18n()
    const isRtl = computed(() => locale.value === 'ar')
    const ShowAccessibilityBlock = ref(false)

    const toggleAccessibilityBlock = () => {
      ShowAccessibilityBlock.value = !ShowAccessibilityBlock.value
    }

    const ShowProfileBlock = ref(false)

    const toggleProfileBlock = () => {
      ShowProfileBlock.value = !ShowProfileBlock.value
    }

    const toggleFullScreen = () => {
      if (!document.fullscreenElement) {
        if (document.documentElement.requestFullscreen) {
          document.documentElement.requestFullscreen()
        }
      } else if (document.exitFullscreen) {
        document.exitFullscreen()
      }
    }

    const profileTab = ref(null)

    // Current date and time
    const currentDate = ref('')
    const currentTime = ref('')

    // Function to update the date and time
    const updateDateTime = () => {
      const now = new Date()
      currentDate.value = now.toLocaleDateString('en-GB') // Format: DD/MM/YYYY
      currentTime.value = now.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      }) // Format: HH:MM AM/PM
    }

    // Update the date and time immediately and every second
    updateDateTime()
    const interval = setInterval(updateDateTime, 1000)

    // Clear the interval when the component is unmounted
    onUnmounted(() => {
      clearInterval(interval)
    })

    return {
      // toast,
      isRtl,
      profileTab,
      toggleFullScreen,
      ShowAccessibilityBlock,
      toggleAccessibilityBlock,
      ShowProfileBlock,
      toggleProfileBlock,
      currentDate,
      currentTime,
      isHasAnyRole,
      futureForesightLogoUrl
    }
  },
  computed: {
    employee() {
      return getEmployeeData() as Employee
    }
  }
})
</script>

<template>
  <header class="header-block" :class="{ rtl: isRtl, ltr: !isRtl }">
    <div class="header-start-block">
      <img :src="futureForesightLogoUrl" alt="Future Foresight" class="future-foresight-logo-header" />
      <span aria-hidden="true" class="header-logo-divider" />
      <img alt="DP logo" class="dp-logo-header" src="@/assets/images/logo/dp-logo-header.svg" />
    </div>
    <div class="header-end-block">
      <div v-show="isHasAnyRole()" class="user-profile">
        <UserProfileDropdown :employee="employee" :rtl="isRtl" />
      </div>
      <LanguageSwitcher />
      <div class="wheelchair-block">
        <div class="wheelchair-block-icon" @click="toggleAccessibilityBlock">
          <img alt="" src="@/assets/images/icons/wheelchair-icon.svg" />
        </div>
        <div v-if="ShowAccessibilityBlock" class="accessibility-block">
          <div class="accessibility-block-header">
            <div class="accessibility-block-close" @click="toggleAccessibilityBlock">
              <i class="mdi mdi-close" />
            </div>
            <div class="accessibility-block-title"><i class="mdi mdi-information" /> سهولة التصفح</div>
          </div>
          <div class="accessibility-block-body">
            <div class="accessibility-block-card">
              <div class="accessibility-block-card-title"> إخفاء البانر الرئيسي</div>
              <div class="accessibility-block-card-subtitle">
                إذا واجهتك مشاكل في قراءة النص, بإمكانك تكبير أو تصغير حجم الخط
              </div>
              <div class="accessibility-block-card-buttons">
                <v-btn class="font-size-button font-12" size="x-small"> A-</v-btn>
                <v-btn class="font-size-button font-14 active" size="x-small"> A</v-btn>
                <v-btn class="font-size-button font-17" size="x-small"> A+</v-btn>
              </div>
            </div>
            <div class="accessibility-block-card">
              <div class="accessibility-block-card-title"> تغيير درجة التباين</div>
              <div class="accessibility-block-card-subtitle"> استخدم الأداة أدناه لتغيير درجة التباين</div>
              <v-radio-group color="primary" density="compact" hide-details>
                <v-radio label="ألوان النظام الافتراضية" value="one" />
                <v-radio label="عمى الألوان" value="two" />
                <v-radio label="ضعف البصر باللون الأخضر" value="three" />
                <v-radio label="ضعف البصر في اللون" value="four" />
              </v-radio-group>
            </div>
          </div>
          <div class="accessibility-block-footer">
            <v-btn block color="error" size="large"><i class="mdi mdi-floppy" /> حفظ</v-btn>
          </div>
        </div>
      </div>
      <div class="fullscreen-block" @click="toggleFullScreen()">
        <div class="fullscreen-block-icon">
          <img alt="" src="@/assets/images/actions/fullscreen-icon.svg" />
        </div>
      </div>
      <div class="date-block">
        <div class="date-block-date-time">
          <div class="date-block-date">{{ currentDate }}</div>
          <div class="date-block-time">{{ currentTime }}</div>
        </div>
        <div class="date-block-icon">
          <img alt="" src="@/assets/images/icons/calendar-icon.svg" />
        </div>
      </div>
    </div>
  </header>
</template>

<style src="./Header.scss" lang="scss" scoped></style>
