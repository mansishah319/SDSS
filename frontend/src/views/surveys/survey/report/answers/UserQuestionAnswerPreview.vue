<template>
  <v-card class="report-data-card">
    <div class="report-data-card__header">
      <div>
        <h3 class="report-data-card__title font-bukra-bold">{{ $t('sdssSurvey.report.userQuestionAnswers') }}</h3>
        <p class="report-data-card__subtitle">{{ userQuestionAnswers.length }}</p>
      </div>
    </div>

    <v-card-text class="report-data-card__body">
      <EmptyDataTable
        v-if="userQuestionAnswers.length === 0"
        :image-size="128"
        :message="$t('Messages.messageForEmptyUserQuestionAnswers')"
        text-color="red"
      />

      <template v-else>
        <div v-if="isMobile" class="responsive-card-list">
          <article v-for="item in userQuestionAnswers" :key="item.id" class="responsive-card-item">
            <div class="responsive-card-item__top">
              <div>
                <p class="responsive-card-item__eyebrow">{{ $t('common.grpNumber') }}</p>
                <h4 class="responsive-card-item__title font-dubai-bold">{{ item.user?.employeeGrpNumber }}</h4>
              </div>
            </div>

            <div class="responsive-card-item__grid">
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.question') }}</span>
                <span class="responsive-field__value">{{ item.question?.localTitle }}</span>
              </div>
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.answer') }}</span>
                <span class="responsive-field__value">{{ item.responseText || '-' }}</span>
              </div>
            </div>
          </article>
        </div>

        <v-data-table
          v-else
          :headers="headers"
          :items="userQuestionAnswers"
          :items-per-page="10"
          density="compact"
          fixed-header
          height="420"
          class="responsive-data-table"
        >
          <template #[`item.employeeGrpNumber`]="{ item }">
            <span class="cell-text">{{ item.user?.employeeGrpNumber }}</span>
          </template>
          <template #[`item.localTitle`]="{ item }">
            <span class="cell-text cell-text--strong">{{ item.question?.localTitle }}</span>
          </template>
          <template #[`item.responseText`]="{ item }">
            <span class="cell-text">{{ item.responseText || '-' }}</span>
          </template>
        </v-data-table>
      </template>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { useDisplay } from 'vuetify'
import { UserSurveyAnswersReportResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { translate } from '@/utils/TranslatorHelper'

export default defineComponent({
  name: 'UserQuestionAnswerPreview',
  components: {
    EmptyDataTable
  },
  props: {
    userQuestionAnswers: {
      type: Array as PropType<UserSurveyAnswersReportResponse[]>,
      required: true,
      default: () => []
    }
  },
  setup() {
    const { smAndDown } = useDisplay()

    const headers = [
      { title: translate('common.grpNumber'), key: 'employeeGrpNumber', align: 'start' as const },
      { title: translate('common.question'), key: 'localTitle', align: 'start' as const },
      { title: translate('common.answer'), key: 'responseText', align: 'start' as const }
    ]

    return {
      headers,
      isMobile: smAndDown
    }
  }
})
</script>

<style scoped lang="scss">
.report-data-card {
  border: 1px solid #e4e7ec;
  border-radius: 20px;
  box-shadow: 0 10px 28px rgba(16, 24, 40, 0.06);
}

.report-data-card__header {
  padding: 16px;
  color: #fff;
  background: linear-gradient(135deg, #0b4f87 0%, #1570ef 100%);
}

.report-data-card__title {
  margin: 0;
  font-size: 1.05rem;
}

.report-data-card__subtitle {
  margin: 4px 0 0;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.82);
}

.report-data-card__body {
  padding: 16px !important;
}

.responsive-card-list {
  display: grid;
  gap: 12px;
}

.responsive-card-item {
  padding: 16px;
  border: 1px solid #e4e7ec;
  border-radius: 16px;
  background: #fff;
}

.responsive-card-item__top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.responsive-card-item__eyebrow {
  margin: 0 0 4px;
  font-size: 0.72rem;
  color: #667085;
}

.responsive-card-item__title {
  margin: 0;
  color: #101828;
  font-size: 1rem;
  word-break: break-word;
}

.responsive-card-item__grid {
  display: grid;
  gap: 10px;
}

.responsive-field {
  display: grid;
  gap: 4px;
}

.responsive-field__label {
  font-size: 0.72rem;
  color: #667085;
}

.responsive-field__value,
.cell-text {
  color: #101828;
  line-height: 1.5;
  word-break: break-word;
}

.cell-text--strong {
  font-weight: 700;
}

.responsive-data-table {
  border: 1px solid #e4e7ec;
  border-radius: 16px;
  overflow: hidden;
}

.responsive-data-table :deep(.v-table__wrapper) {
  overflow: auto;
}

.responsive-data-table :deep(thead th) {
  position: sticky;
  top: 0;
  z-index: 2;
  background: #f8fafc;
  color: #344054;
  font-weight: 700;
}

.responsive-data-table :deep(td),
.responsive-data-table :deep(th) {
  padding: 12px 14px;
  vertical-align: top;
}

@media (max-width: 600px) {
  .report-data-card__header,
  .report-data-card__body,
  .responsive-card-item {
    padding: 16px;
  }
}
</style>
