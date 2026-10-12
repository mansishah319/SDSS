<template>
  <v-card>
    <v-card-title class="custom-employee-info-table-title font-bukra-bold">
      <h1
        class="custom-header-common-color font-dubai-bold custom-pdf-report-table-content-title custom-header-common-color"
      >
        {{ $t('sdssSurvey.report.userQuestionAnswers') }}
      </h1>
    </v-card-title>
    <v-card-text>
      <EmptyDataTable
        v-if="userQuestionAnswers.length === 0"
        :image-size="128"
        :message="$t('Messages.messageForEmptyUserQuestionAnswers')"
        text-color="red"
      />
      <table v-else class="custom-employee-info-table">
        <thead>
        <tr>
          <th v-for="header in headers" :key="header.key" :align="header.align">
            <h1 class="font-dubai-bold custom-pdf-report-table-content-title custom-header-common-color">
              {{ header.title }}
            </h1>
          </th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in userQuestionAnswers" :key="index" class="v-data-table-row">
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.user.employeeGrpNumber }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.question.localTitle }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.responseText }}
            </h1>
          </td>
        </tr>
        </tbody>
      </table>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { UserSurveyAnswersReportResponse } from '@/dto/report/ReportSdssSurveyRoundResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { translate } from '@/utils/TranslatorHelper'

export default defineComponent({
  name: 'ReportUserQuestionAnswerPreview',
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
    const headers = [
      { title: translate('common.grpNumber'), key: 'employeeGrpNumber', align: 'start' },
      { title: translate('common.question'), key: 'localTitle', align: 'start' },
      { title: translate('common.answer'), key: 'responseText', align: 'start' }
    ]

    return {
      headers
    }
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.custom-employee-info-table-title {
  background-color: #0da06e;
  color: $white-color;
  font-size: 1.2rem;
  padding: 16px;
}

.custom-employee-info-table {
  width: 100%;
  margin-top: 16px;
  border-collapse: collapse;

  thead {
    background-color: #f7f7fb;
    color: $white-color;

    th {
      font-weight: bold;
      padding: 12px;
      text-align: left;
    }
  }

  tbody {
    tr {
      &:nth-child(odd) {
        background-color: #f5f5f5;
      }

      &:hover {
        background-color: #e0e0e0;
      }

      td {
        padding: 12px;
        text-align: left;
      }
    }
  }
}
</style>
