<template>
  <div class="footer-brand-card" dir="rtl">
    <div class="footer-brand-card__brand">
      <div class="footer-brand-card__badge" aria-hidden="true">DP</div>
      <div class="footer-brand-card__copy">
        <strong>{{ DEFAULT_REPORT_BRAND_TITLE }}</strong>
        <span>{{ DEFAULT_REPORT_BRAND_SUBTITLE }}</span>
      </div>
    </div>

    <div class="footer-brand-card__end">
      <img :src="futureForesightLogoUrl" alt="DubaiPoliceArtificialIntelligence" />
      <div class="footer-brand-card__meta">
        <span>{{ copyright }}</span>
        <span>{{ DEFAULT_REPORT_OWNER }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import futureForesightLogoUrl from '@/assets/images/logo/future-foresight-white.svg'
import {
  buildReportFooterCopyright,
  DEFAULT_REPORT_BRAND_SUBTITLE,
  DEFAULT_REPORT_BRAND_TITLE,
  DEFAULT_REPORT_OWNER
} from '@/utils/report/exportReportChrome'

const copyright = buildReportFooterCopyright()
</script>

<style scoped lang="scss">
.footer-brand-card {
  width: 100%;
  min-height: 76px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 12px 28px;
  border-radius: 16px;
  overflow: hidden;
  color: #fff;
  background: linear-gradient(135deg, rgba(1, 88, 69, 0.98) 0%, rgba(0, 135, 85, 0.94) 100%);
  box-shadow: 0 10px 30px rgba(1, 88, 69, 0.18);
  font-family: DubaiRegular, Arial, sans-serif;
}

.footer-brand-card__brand,
.footer-brand-card__end {
  display: flex;
  align-items: center;
  min-width: 0;
}

.footer-brand-card__brand {
  flex: 1 1 auto;
  gap: 14px;
  text-align: right;
}

.footer-brand-card__badge {
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.16);
  font-family: DubaiBold, DubaiRegular, Arial, sans-serif;
  font-size: 16px;
  font-weight: 700;
}

.footer-brand-card__copy,
.footer-brand-card__meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.footer-brand-card__copy {
  min-width: 0;
  align-items: flex-start;
  text-align: right;
}

.footer-brand-card__copy strong {
  font-family: DubaiBold, DubaiRegular, Arial, sans-serif;
  font-size: 20px;
  line-height: 1.3;
  white-space: nowrap;
}

.footer-brand-card__copy span {
  margin-top: 2px;
  color: rgba(255, 255, 255, 0.88);
  font-size: 13px;
  line-height: 1.3;
  white-space: nowrap;
}

.footer-brand-card__end {
  flex: 0 0 auto;
  gap: 20px;
  direction: ltr;
}

.footer-brand-card__end img {
  width: 132px;
  max-height: 34px;
  object-fit: contain;
}

.footer-brand-card__meta {
  align-items: flex-end;
  gap: 2px;
  direction: rtl;
  text-align: right;
  font-size: 12px;
  line-height: 1.4;
  white-space: nowrap;
}

@media (max-width: 760px) {
  .footer-brand-card {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 16px;
  }

  .footer-brand-card__end {
    width: 100%;
    justify-content: space-between;
  }

  .footer-brand-card__copy strong {
    font-size: 17px;
    white-space: normal;
  }

  .footer-brand-card__copy span,
  .footer-brand-card__meta {
    white-space: normal;
  }
}

@media (max-width: 460px) {
  .footer-brand-card__end {
    flex-direction: column-reverse;
    align-items: flex-end;
  }
}
</style>
