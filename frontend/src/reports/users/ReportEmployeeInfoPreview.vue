<template>
  <v-card>
    <v-card-title class="custom-employee-info-table-title font-bukra-bold">
      <h1
        class="custom-header-common-color font-dubai-bold custom-pdf-report-table-content-title custom-header-common-color"
      >
        {{ $t('sdssSurvey.report.employeeDetails') }}
      </h1>
    </v-card-title>
    <v-card-text>
      <!-- Check if employeeList is empty -->
      <EmptyDataTable
        v-if="employeeList.length === 0"
        :image-size="128"
        :message="$t('Messages.messageForEmptyEmployeeList')"
        text-color="red"
      />
      <!-- Show the table if employeeList is not empty -->
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
        <tr v-for="(item, index) in employeeList" :key="index" class="v-data-table-row">
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.id }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.fullName }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.jobName }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.emailAddress }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.departmentName }}
            </h1>
          </td>
          <td>
            <h1
              class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
            >
              {{ item.reportUserResponse.employeeInfo.gradeName }}
            </h1>
          </td>
          <td :align="headers[headers.length - 1].align">
            <v-chip :color="item.hasAnswered ? 'green' : 'red'" small>
              <h1
                class="custom-question-report-view-user-answers-card-sub-title-span custom-pdf-report-table-content-body"
              >
                {{ item.hasAnswered ? $t('common.answered') : $t('common.notAnswered') }}
              </h1>
            </v-chip>
          </td>
        </tr>
        </tbody>
      </table>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { SurveyUser } from '@/dto/report/ReportSdssSurveyRoundResponse'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'
import { translate } from '@/utils/TranslatorHelper'

export default defineComponent({
  name: 'ReportEmployeeInfoPreview',
  components: {
    EmptyDataTable // Register the EmptyDataTable component
  },
  props: {
    employeeList: {
      type: Array as PropType<SurveyUser[]>,
      required: true,
      default: () => []
    }
  },
  setup() {
    const headers = [
      { title: translate('common.grpNumber'), key: 'id', align: 'start' },
      { title: translate('common.fullName'), key: 'fullName', align: 'start' },
      { title: translate('common.jobTitle'), key: 'jobName', align: 'start' },
      { title: translate('common.email'), key: 'emailAddress', align: 'start' },
      { title: translate('common.department'), key: 'departmentName', align: 'start' },
      { title: translate('common.grade'), key: 'gradeName', align: 'start' },
      { title: translate('common.answeredStatus'), key: 'hasAnswered', align: 'center' }
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
