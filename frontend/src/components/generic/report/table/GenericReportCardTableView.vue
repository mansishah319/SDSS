<template>
  <v-card class="mb-5 generic-report-card-table-view">
    <v-card-title class="generic-report-card-table-view-card-title">
      {{ title }}
    </v-card-title>
    <v-data-table :headers="headers" :items="items" :loading="loading" hide-default-footer class="traffic-data-table">
      <template v-if="!loading && items.length === 0" #body>
        <tr>
          <td :colspan="headers.length" class="text-center">
            <EmptyDataTable :message="emptyMsg" :image-size="emptyImageSize" :text-color="emptyTextColor" />
          </td>
        </tr>
      </template>

      <!-- Custom slot for each column if needed -->
      <template v-for="(_, slot) in $slots" #[slot]="scope">
        <slot :name="slot" v-bind="scope" />
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { TableHeader } from '@/model/generic/TableConfig'
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default defineComponent({
  name: 'GenericReportCardTableView',
  components: { EmptyDataTable },
  props: {
    title: {
      type: String,
      required: true
    },
    headers: {
      type: Array as PropType<TableHeader[]>,
      required: true
    },
    items: {
      type: Array as PropType<any[]>,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    emptyMsg: {
      type: String,
      required: false,
      default: 'common.empty'
    },
    emptyImageSize: {
      type: [Number, String],
      required: false,
      default: 128
    },
    emptyTextColor: {
      type: String,
      required: false,
      default: 'black' // Default text color is black
    }
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.generic-report-card-table-view {
  margin: 3%;
  border-radius: 20px !important;
}

.generic-report-card-table-view-card-title {
  background-color: $color-green-dark;
  color: $white-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.traffic-data-table {
  width: 100%;
}
</style>
