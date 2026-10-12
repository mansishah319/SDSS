<template>
  <v-card class="report-data-card" :class="{ 'rtl-layout': isRTL, 'report-data-card--pdf': exportMode }">
    <div class="report-data-card__header">
      <div class="report-data-card__header-content">
        <div class="report-data-card__title-wrap">
          <h3 class="report-data-card__title font-bukra-bold">
            {{ $t('sdssSurvey.report.employeeDetails') }}
          </h3>
        </div>
        <div class="report-data-card__stats">
          <div
            v-if="exportMode"
            class="report-data-card__stat-badge"
          >
            <v-icon size="20" class="stat-icon">mdi-account-group</v-icon>
            <span class="report-data-card__stat-number">{{ employeeList.length }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.total') }}</span>
          </div>
          <div
            v-else
            class="report-data-card__stat-badge"
            v-tooltip="{
              content: $t('sdssSurvey.report.totalEmployeesTooltip'),
              placement: 'bottom'
            }"
          >
            <v-icon size="20" class="stat-icon">mdi-account-group</v-icon>
            <span class="report-data-card__stat-number">{{ employeeList.length }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.total') }}</span>
          </div>

          <div
            v-if="exportMode"
            class="report-data-card__stat-badge report-data-card__stat-badge--answered"
          >
            <v-icon size="20" class="stat-icon">mdi-check-circle</v-icon>
            <span class="report-data-card__stat-number">{{ answeredCount }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.answered') }}</span>
          </div>
          <div
            v-else
            class="report-data-card__stat-badge report-data-card__stat-badge--answered"
            v-tooltip="{
              content: $t('sdssSurvey.report.answeredCountTooltip'),
              placement: 'bottom'
            }"
          >
            <v-icon size="20" class="stat-icon">mdi-check-circle</v-icon>
            <span class="report-data-card__stat-number">{{ answeredCount }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.answered') }}</span>
          </div>

          <div
            v-if="exportMode"
            class="report-data-card__stat-badge report-data-card__stat-badge--pending"
          >
            <v-icon size="20" class="stat-icon">mdi-clock-outline</v-icon>
            <span class="report-data-card__stat-number">{{ pendingCount }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.pending') }}</span>
          </div>
          <div
            v-else
            class="report-data-card__stat-badge report-data-card__stat-badge--pending"
            v-tooltip="{
              content: $t('sdssSurvey.report.pendingCountTooltip'),
              placement: 'bottom'
            }"
          >
            <v-icon size="20" class="stat-icon">mdi-clock-outline</v-icon>
            <span class="report-data-card__stat-number">{{ pendingCount }}</span>
            <span class="report-data-card__stat-label">{{ $t('common.pending') }}</span>
          </div>
        </div>
      </div>
    </div>

    <v-card-text class="report-data-card__body">
      <EmptyDataTable v-if="!employeeList.length" />
      <template v-else>
        <div v-if="exportMode" class="pdf-employee-table-wrapper">
          <table class="pdf-employee-table">
            <thead>
            <tr>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.grpNumber') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.fullName') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.jobTitle') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.email') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.department') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.grade') }}</span></th>
              <th class="pdf-employee-table__header-cell"><span
                class="pdf-employee-table__header-label">{{ $t('common.answeredStatus') }}</span></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in employeeList"
                :key="item.reportUserResponse.id || item.reportUserResponse.employeeInfo.id">
              <td>{{ item.reportUserResponse.employeeInfo.id || '-' }}</td>
              <td class="pdf-employee-table__name">{{ item.reportUserResponse.employeeInfo.fullName || '-' }}</td>
              <td>{{ item.reportUserResponse.employeeInfo.jobName || '-' }}</td>
              <td>{{ item.reportUserResponse.employeeInfo.emailAddress || '-' }}</td>
              <td>{{ item.reportUserResponse.employeeInfo.departmentName || '-' }}</td>
              <td>
                <span class="pdf-grade-pill">{{ item.reportUserResponse.employeeInfo.gradeName || '-' }}</span>
              </td>
              <td>
                  <span
                    class="pdf-status-pill"
                    :class="item.hasAnswered ? 'pdf-status-pill--success' : 'pdf-status-pill--warning'"
                  >
                    {{ item.hasAnswered ? $t('common.answered') : $t('common.notAnswered') }}
                  </span>
              </td>
            </tr>
            </tbody>
          </table>
        </div>

        <div v-else-if="isMobile" class="responsive-card-list">
          <article
            v-for="item in employeeList"
            :key="item.reportUserResponse.employeeInfo.id"
            class="responsive-card-item"
          >
            <div class="responsive-card-item__top">
              <div>
                <p class="responsive-card-item__eyebrow">{{ $t('common.grpNumber') }}</p>
                <h4 class="responsive-card-item__title font-dubai-bold">
                  {{ item.reportUserResponse.employeeInfo.fullName }}
                </h4>
              </div>
              <v-chip :color="item.hasAnswered ? 'green' : 'red'" size="small" variant="flat">
                <v-icon v-if="isRTL && !item.hasAnswered" left size="14" class="ml-1">mdi-clock-outline</v-icon>
                {{ item.hasAnswered ? $t('common.answered') : $t('common.notAnswered') }}
                <v-icon v-if="!isRTL && item.hasAnswered" right size="14" class="ml-1">mdi-check-circle</v-icon>
              </v-chip>
            </div>

            <div class="responsive-card-item__grid">
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.grpNumber') }}</span>
                <span class="responsive-field__value">{{ item.reportUserResponse.employeeInfo.id }}</span>
              </div>
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.jobTitle') }}</span>
                <span class="responsive-field__value">{{ item.reportUserResponse.employeeInfo.jobName }}</span>
              </div>
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.email') }}</span>
                <span class="responsive-field__value">{{ item.reportUserResponse.employeeInfo.emailAddress }}</span>
              </div>
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.department') }}</span>
                <span class="responsive-field__value">{{ item.reportUserResponse.employeeInfo.departmentName }}</span>
              </div>
              <div class="responsive-field">
                <span class="responsive-field__label">{{ $t('common.grade') }}</span>
                <span class="responsive-field__value">{{ item.reportUserResponse.employeeInfo.gradeName }}</span>
              </div>
            </div>
          </article>
        </div>

        <div v-else class="employee-table-wrapper">
          <v-data-table
            :headers="headers"
            :items="employeeList"
            :items-per-page="-1"
            :search="search"
            hide-default-footer
            density="comfortable"
            fixed-header
            height="500"
            class="employee-data-table"
            hover
          >
            <template #top>
              <div class="employee-table__toolbar">
                <div class="employee-table__search">
                  <v-text-field
                    v-model="search"
                    :placeholder="$t('common.search')"
                    density="compact"
                    variant="outlined"
                    :prepend-inner-icon="isRTL ? undefined : 'mdi-magnify'"
                    :append-inner-icon="isRTL ? 'mdi-magnify' : undefined"
                    hide-details
                    class="employee-search-field"
                  />
                </div>
                <div class="employee-table__actions">
                  <v-btn
                    :hidden="true"
                    variant="text"
                    size="small"
                    @click="exportToCSV"
                    v-tooltip="$t('common.exportToCSV')"
                  >
                    <v-icon :left="!isRTL" :right="isRTL" size="18">mdi-download</v-icon>
                    {{ $t('common.export') }}
                  </v-btn>
                </div>
              </div>
            </template>

            <template #[`item.id`]="{ item }">
              <div class="table-cell-id">
                <span class="cell-text cell-text--id">{{ item.reportUserResponse.employeeInfo.id }}</span>
              </div>
            </template>

            <template #[`item.fullName`]="{ item }">
              <div class="table-cell-name">
                <div class="avatar-placeholder">
                  {{ getInitials(item.reportUserResponse.employeeInfo.fullName) }}
                </div>
                <div>
                  <span class="cell-text cell-text--strong">{{ item.reportUserResponse.employeeInfo.fullName }}</span>
                </div>
              </div>
            </template>

            <template #[`item.jobName`]="{ item }">
              <span class="cell-text cell-text--job">{{ item.reportUserResponse.employeeInfo.jobName }}</span>
            </template>

            <template #[`item.emailAddress`]="{ item }">
              <div class="table-cell-email">
                <v-icon :class="isRTL ? 'ml-1' : 'mr-1'" size="16" color="#667085">mdi-email-outline</v-icon>
                <span class="cell-text cell-text--email">{{ item.reportUserResponse.employeeInfo.emailAddress }}</span>
              </div>
            </template>

            <template #[`item.departmentName`]="{ item }">
              <div class="table-cell-department">
                <v-icon :class="isRTL ? 'ml-1' : 'mr-1'" size="16" color="#667085">mdi-domain</v-icon>
                <span class="cell-text">{{ item.reportUserResponse.employeeInfo.departmentName }}</span>
              </div>
            </template>

            <template #[`item.gradeName`]="{ item }">
              <v-chip size="small" variant="outlined" class="grade-chip">
                {{ item.reportUserResponse.employeeInfo.gradeName }}
              </v-chip>
            </template>

            <template #[`item.hasAnswered`]="{ item }">
              <v-chip
                :color="item.hasAnswered ? 'success' : 'warning'"
                size="small"
                variant="flat"
                class="status-chip"
              >
                <v-icon :class="isRTL ? 'ml-1' : 'mr-1'" size="14">
                  {{ item.hasAnswered ? 'mdi-check-circle' : 'mdi-clock-outline' }}
                </v-icon>
                {{ item.hasAnswered ? $t('common.answered') : $t('common.notAnswered') }}
              </v-chip>
            </template>
          </v-data-table>
        </div>
      </template>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref } from 'vue'
import { useDisplay } from 'vuetify'
import { useI18n } from 'vue-i18n'
import { SurveyUser } from '@/dto/report/ReportSdssSurveyRoundResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { translate } from '@/utils/TranslatorHelper'

export default defineComponent({
  name: 'EmployeeInfoPreview',
  components: {
    EmptyDataTable
  },
  props: {
    employeeList: {
      type: Array as PropType<SurveyUser[]>,
      required: true,
      default: () => []
    },
    exportMode: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const { smAndDown } = useDisplay()
    const { locale } = useI18n()
    const search = ref('')

    const isRTL = computed(() => locale.value === 'ar')
    const tableAlign = 'start'
    const isMobile = computed(() => smAndDown.value)

    const answeredCount = computed(() =>
      props.employeeList.filter(emp => emp.hasAnswered).length
    )

    const pendingCount = computed(() =>
      props.employeeList.filter(emp => !emp.hasAnswered).length
    )

    const getInitials = (fullName: string): string => {
      const names = fullName.split(' ')
      if (names.length === 1) return names[0].charAt(0).toUpperCase()
      return (names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase()
    }

    const exportToCSV = () => {
      const headers = [
        translate('common.grpNumber'),
        translate('common.fullName'),
        translate('common.jobTitle'),
        translate('common.email'),
        translate('common.department'),
        translate('common.grade'),
        translate('common.answeredStatus')
      ]

      const rows = props.employeeList.map(emp => [
        emp.reportUserResponse.employeeInfo.id,
        emp.reportUserResponse.employeeInfo.fullName,
        emp.reportUserResponse.employeeInfo.jobName,
        emp.reportUserResponse.employeeInfo.emailAddress,
        emp.reportUserResponse.employeeInfo.departmentName,
        emp.reportUserResponse.employeeInfo.gradeName,
        emp.hasAnswered ? translate('common.answered') : translate('common.notAnswered')
      ])

      const csvContent = [headers, ...rows]
        .map(row => row.join(','))
        .join('\n')

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.href = url
      link.setAttribute('download', `employee_report_${new Date().toISOString()}.csv`)
      document.body.appendChild(link)
      link.click()
      link.remove()
      URL.revokeObjectURL(url)
    }

    const headers = computed(() => [
      {
        title: translate('common.grpNumber'),
        key: 'id',
        align: tableAlign.value,
        width: '100px'
      },
      {
        title: translate('common.fullName'),
        key: 'fullName',
        align: tableAlign.value,
        width: '200px'
      },
      {
        title: translate('common.jobTitle'),
        key: 'jobName',
        align: tableAlign.value,
        width: '150px'
      },
      {
        title: translate('common.email'),
        key: 'emailAddress',
        align: tableAlign.value,
        width: '200px'
      },
      {
        title: translate('common.department'),
        key: 'departmentName',
        align: tableAlign.value,
        width: '150px'
      },
      { title: translate('common.grade'), key: 'gradeName', align: 'center' as const, width: '100px' },
      { title: translate('common.answeredStatus'), key: 'hasAnswered', align: 'center' as const, width: '130px' }
    ])

    return {
      headers,
      isMobile,
      isRTL,
      search,
      answeredCount,
      pendingCount,
      getInitials,
      exportToCSV
    }
  }
})
</script>

<style scoped lang="scss">
.report-data-card {
  border: 1px solid #e4e7ec;
  border-radius: 20px;
  box-shadow: 0 10px 28px rgba(16, 24, 40, 0.06);
  overflow: hidden;

  &.rtl-layout {
    direction: rtl;

    .report-data-card__header-content,
    .employee-table__toolbar,
    .employee-table__footer,
    .table-cell-name,
    .table-cell-email,
    .table-cell-department {
      text-align: right;
    }

    .avatar-placeholder {
      margin-left: 10px;
      margin-right: 0;
    }

    .responsive-card-item__top {
      direction: rtl;
    }
  }
}

.report-data-card__header {
  background: linear-gradient(135deg, #006c44 0%, #0b8a58 100%);
  padding: 20px 24px;
}

.report-data-card__header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.report-data-card__title-wrap {
  display: flex;
  align-items: center;
  min-height: 100%;
}

.report-data-card__title {
  margin: 0;
  font-size: 1.2rem;
  color: #fff;
}

.report-data-card__subtitle {
  margin: 6px 0 0;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.85);
}

.report-data-card__stats {
  display: flex;
  gap: 12px;
}

.report-data-card__stat-badge {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  cursor: help;
  transition: all 0.2s ease;

  .stat-icon {
    margin-right: 6px;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.25);
    transform: translateY(-2px);
  }

  &--answered {
    background: rgba(76, 175, 80, 0.2);
  }

  &--pending {
    background: rgba(255, 152, 0, 0.2);
  }
}

.rtl-layout .report-data-card__stat-badge {
  .stat-icon {
    margin-right: 0;
    margin-left: 6px;
  }
}

.report-data-card__stat-number {
  font-size: 1.3rem;
  font-weight: 700;
  color: #fff;
  margin-right: 4px;
}

.report-data-card__stat-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.9);
  margin-left: 4px;
}

.rtl-layout .report-data-card__stat-label {
  margin-left: 0;
  margin-right: 4px;
}

.report-data-card__body {
  padding: 0 !important;
}

.pdf-employee-table-wrapper {
  padding: 18px;
  overflow: visible;
  background: #fff;
}

.pdf-employee-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  background: #fff;

  th,
  td {
    padding: 12px 14px;
    border-bottom: 1px solid #eaecf0;
    vertical-align: top;
    text-align: start;
    word-break: break-word;
  }

  th {
    background: #f8fafc;
    color: #344054;
    font-size: 0.78rem;
    font-weight: 700;
    letter-spacing: 0.04em;
    text-transform: uppercase;
  }
}

.pdf-employee-table__header-cell {
  text-transform: none;
  letter-spacing: 0;
}

.pdf-employee-table__header-label {
  display: block;
  font-family: 'DubaiRegular', Arial, Helvetica, sans-serif;
  font-size: 0.78rem;
  font-weight: 400;
  letter-spacing: 0;
  line-height: 1.35;
  text-transform: none;
  unicode-bidi: plaintext;
}

.rtl-layout .pdf-employee-table {
  th,
  td {
    text-align: start;
  }
}

.rtl-layout .pdf-employee-table__header-cell {
  direction: rtl;
  text-align: right;
}

.rtl-layout .pdf-employee-table__header-label {
  direction: rtl;
  text-align: right;
}

.report-data-card--pdf .report-data-card__header {
  min-height: 106px;
}

.report-data-card--pdf .report-data-card__header-content {
  align-items: center;
  min-height: 66px;
}

.report-data-card--pdf .report-data-card__title-wrap {
  align-self: stretch;
}

.pdf-employee-table__name {
  font-weight: 700;
  color: #101828;
}

.pdf-grade-pill,
.pdf-status-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 28px;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
  line-height: 1.2;
  white-space: nowrap;
}

.pdf-grade-pill {
  background: #f2f4f7;
  border: 1px solid #d0d5dd;
  color: #344054;
}

.pdf-status-pill--success {
  background: rgba(18, 183, 106, 0.12);
  border: 1px solid rgba(18, 183, 106, 0.24);
  color: #027a48;
}

.pdf-status-pill--warning {
  background: rgba(247, 144, 9, 0.12);
  border: 1px solid rgba(247, 144, 9, 0.24);
  color: #b54708;
}

.employee-table-wrapper {
  border-radius: 0;
  background: #fff;
  overflow: hidden;
}

.employee-data-table {
  background: transparent;
}

.rtl-layout .employee-data-table {
  direction: rtl;
}

.employee-table__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #e4e7ec;
}

.rtl-layout .employee-table__toolbar {
  justify-content: start;
  flex-direction: row-reverse;
}

.rtl-layout .employee-table__search {
  margin-left: auto;
}

.employee-table__search {
  width: 300px;
}

.employee-search-field :deep(.v-field) {
  border-radius: 10px;
  background: #fff;
}

.rtl-layout .employee-search-field :deep(.v-field__input),
.rtl-layout .employee-search-field :deep(input) {
  direction: rtl;
  text-align: right;
}

.employee-table__actions {
  display: flex;
  gap: 8px;
}

.rtl-layout .employee-table__actions {
  .v-icon {
    margin-left: 6px;
    margin-right: 0;
  }
}

.employee-data-table :deep(.v-table__wrapper) {
  overflow: auto;
  max-height: 500px;
}

.rtl-layout .employee-data-table :deep(table) {
  direction: rtl;
}

.employee-data-table :deep(thead th) {
  position: sticky;
  top: 0;
  z-index: 2;
  background: #f8fafc;
  color: #344054;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 2px solid #e4e7ec;
  padding: 14px 16px;
}

.employee-data-table :deep(.v-data-table-column--align-end) {
  text-align: inherit !important;
}

.employee-data-table :deep(thead th),
.employee-data-table :deep(td) {
  text-align: start;
}

.employee-data-table :deep(thead th .v-data-table-header__content) {
  justify-content: flex-start;
  text-align: start;
}

.rtl-layout .employee-data-table :deep(thead th) {
  text-align: start;
}

.rtl-layout .employee-data-table :deep(thead th .v-data-table-header__content) {
  width: 100%;
  justify-content: flex-start;
  direction: rtl;
  text-align: start;
}

.employee-data-table :deep(tbody tr) {
  transition: all 0.2s ease;

  &:hover {
    background: #f9fafb;
    transform: scale(1.01);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

.employee-data-table :deep(td) {
  padding: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.rtl-layout .employee-data-table :deep(td) {
  text-align: start;
}

.table-cell-id {
  .cell-text--id {
    font-family: monospace;
    font-size: 0.85rem;
    color: #667085;
    background: #f5f6f8;
    padding: 4px 8px;
    border-radius: 6px;
    display: inline-block;
  }
}

.table-cell-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rtl-layout .table-cell-name {
  float: inline-start;
}

.avatar-placeholder {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #006c44, #0b8a58);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  flex-shrink: 0;
}

.table-cell-email,
.table-cell-department {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .v-icon {
    flex-shrink: 0;
  }
}

.rtl-layout .table-cell-email,
.rtl-layout .table-cell-department {
  text-align: start;
}

.cell-text {
  color: #101828;
  line-height: 1.5;
  word-break: break-word;
  font-size: 0.9rem;

  &--strong {
    font-weight: 600;
    color: #1a2a3a;
  }

  &--email {
    color: #006c44;
  }
}

.grade-chip {
  font-weight: 500;
  background: #f0f2f5;
  border: 1px solid #e0e3e8;
  color: #344054;
}

.status-chip {
  font-weight: 500;
  text-transform: none;
}

.rtl-layout .status-chip :deep(.v-chip__content),
.rtl-layout .grade-chip :deep(.v-chip__content) {
  direction: rtl;
  justify-content: center;
}

.employee-table__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafbfc;
  border-top: 1px solid #e4e7ec;
}

.rtl-layout .employee-table__footer {
  flex-direction: row-reverse;
}

.employee-table__info {
  font-size: 0.85rem;
  color: #667085;
}

/* Responsive Card List - RTL Support */
.responsive-card-list {
  display: grid;
  gap: 12px;
  padding: 16px;
}

.responsive-card-item {
  padding: 16px;
  border: 1px solid #e4e7ec;
  border-radius: 16px;
  background: #fff;
  transition: all 0.2s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
}

.responsive-card-item__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.rtl-layout .responsive-card-item__top {
  flex-direction: row-reverse;
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

.rtl-layout .responsive-field {
  text-align: right;
}

.responsive-field__label {
  font-size: 0.72rem;
  color: #667085;
}

.responsive-field__value {
  color: #101828;
  line-height: 1.5;
  word-break: break-word;
}

/* Mobile RTL Specific Adjustments */
@media (max-width: 768px) {
  .report-data-card__header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .rtl-layout .report-data-card__header-content {
    align-items: flex-end;
  }

  .employee-table__toolbar {
    flex-direction: column;
    gap: 12px;
  }

  .rtl-layout .employee-table__toolbar {
    flex-direction: column;
  }

  .employee-table__search {
    width: 100%;
  }

  .employee-table__footer {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .rtl-layout .employee-table__footer {
    flex-direction: column;
  }
}

/* Scrollbar Styling for RTL */
.rtl-layout .employee-data-table :deep(.v-table__wrapper) {
  scrollbar-width: thin;
  scrollbar-color: #c1c1c1 #f1f1f1;
}

.rtl-layout .employee-data-table :deep(.v-table__wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

.rtl-layout .employee-data-table :deep(.v-table__wrapper::-webkit-scrollbar-track) {
  background: #f1f1f1;
}

.rtl-layout .employee-data-table :deep(.v-table__wrapper::-webkit-scrollbar-thumb) {
  background: #c1c1c1;
  border-radius: 4px;
}
</style>
