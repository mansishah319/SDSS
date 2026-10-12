<template>
  <v-row>
    <v-col cols="12">
      <div class="table-container">
        <table
          :class="{
            rtl: isRtl,
            'v-table-solution-details-border': true,
          }"
        >
          <caption></caption>
          <thead class="sticky-solution-details-header">
          <tr>
            <th scope="col" class="text-start font-dubai-bold">
              {{ $t('common.srNo') }}
            </th>
            <th scope="col" class="text-start font-dubai-bold">
              {{ $t('challenge.viewInfo.solution') }}
            </th>
            <th scope="col" class="text-start font-dubai-bold">
              {{ $t('common.createdBy') }}
            </th>
          </tr>
          </thead>
          <tbody class="scrollable-solution-details-tbody">
          <tr v-for="(item, index) in solutions" :key="index" :class="{ rtl: isRtl }">
            <td class="font-dubai-regular">{{ index + 1 }}</td>
            <td class="font-dubai-regular">
              {{ item.solutionText }}
            </td>
            <td class="font-dubai-regular">
              {{ item.createdBy }}
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </v-col>
  </v-row>
  <v-card-text v-if="!hasSolutions" class="text-center">
    <EmptyDataTable :image-size="128" :message="$t('common.empty')" />
  </v-card-text>
</template>

<script>
import EmptyDataTable from '@/components/common/EmptyDataTable.vue'

export default {
  components: {
    EmptyDataTable
  },
  props: {
    solutions: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      isRtl: false // Dynamic RTL flag, set based on language
    }
  },
  computed: {
    hasSolutions() {
      return this.solutions && this.solutions.length > 0
    }
  },
  watch: {
    // Watch for changes in language direction
    '$i18n.locale'() {
      this.detectRtl()
    }
  },
  mounted() {
    this.detectRtl()
  },
  methods: {
    detectRtl() {
      this.isRtl = document.documentElement.dir === 'rtl'
    }
  }
}
</script>

<style scoped>
.table-container {
  max-height: 300px;
  overflow-y: hidden;
}

.v-table-border {
  width: 100%;
  border-collapse: collapse;
}

.sticky-solution-details-header {
  position: sticky;
  top: 0;
  z-index: 1;
  color: white;
}

.scrollable-solution-details-tbody {
  display: block;
  height: 250px;
  overflow-y: auto;
}

thead.sticky-solution-details-header,
.scrollable-solution-details-tbody tr {
  display: table;
  width: 100%;
  table-layout: fixed;
}

.rtl {
  direction: rtl;
}

.rtl td {
  text-align: right;
}

th,
td {
  padding: 8px;
  border: 1px solid #ddd;
  text-align: left;
}

.v-table-solution-details-border {
  border-bottom: 2px solid #c1c1c1;
  margin-bottom: 12px;

  .v-table__wrapper {
    overflow-x: hidden;
  }
}

thead,
tbody tr {
  display: table;
  width: 100%;
  table-layout: fixed; /* even columns width , fix width of table too*/
}
</style>
