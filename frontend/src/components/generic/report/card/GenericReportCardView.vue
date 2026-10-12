<template>
  <v-card variant="outlined" class="generic-report-card-view-card">
    <v-card-title v-if="showTitle" v-show="showTitle" class="generic-report-card-view-card-title font-bukra-bold">
      {{ title }}
      <div v-show="showBackButton" v-if="showBackButton" class="search-container">
        <div class="add-button custom-generic-back-button" @click="goBack">
          <img
            class="custom-generic-back-image"
            src="@/assets/images/actions/custom-white-icon-back.svg"
            alt="custom-white-back-icon"
          />
          {{ $t('common.back') }}
        </div>
      </div>
      <div v-show="showCloseButton" v-if="showCloseButton" class="search-container">
        <div class="add-button custom-generic-back-button" @click="goClose">
          <img class="custom-generic-back-image" src="@/assets/images/actions/close-white.svg" alt="close-white-icon" />
        </div>
      </div>
    </v-card-title>
    <v-card-text class="generic-report-card-view-card-text justify-space-between">
      <template v-for="(row, rowIndex) in config" :key="rowIndex">
        <v-row>
          <template v-for="(col, colIndex) in row.columns" :key="colIndex">
            <v-col v-if="shouldShowColumn(col)" :cols="getColumnWidth(row.columns, col)">
              <v-col cols="12">
                <span class="font-dubai-bold"
                ><strong>{{ col.title }}</strong></span
                >
              </v-col>
              <v-col cols="12">
                <span class="font-dubai-regular" :style="col.style">
                  {{ getColumnValue(col) }}
                </span>
              </v-col>
            </v-col>
          </template>
        </v-row>
        <v-divider v-if="rowIndex < config.length - 1" :thickness="3" color="info"></v-divider>
      </template>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { useRouter } from 'vue-router'
import { CardColumnConfig, CardConfig } from '@/model/generic/CardConfig'

export default defineComponent({
  name: 'GenericReportCardView',
  props: {
    title: {
      type: String,
      required: true
    },
    config: {
      type: Array as () => CardConfig[],
      required: true
    },
    routePage: {
      type: [String, Object] as PropType<string | { name: string; params?: Record<string, any> }>,
      required: false,
      default: '/'
    },
    showBackButton: {
      type: Boolean,
      required: false,
      default: true
    },
    showCloseButton: {
      type: Boolean,
      required: false,
      default: false
    },
    showOnlyNonEmptyValues: {
      type: Boolean,
      required: false,
      default: false
    },
    showTitle: {
      type: Boolean,
      required: false,
      default: true
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const router = useRouter()

    const goBack = () => {
      if (typeof props.routePage === 'string') {
        router.push({ name: props.routePage })
      } else if (typeof props.routePage === 'object') {
        console.log(props.routePage)
        router.push({
          name: props.routePage.name,
          params: props.routePage.params
        })
      }
    }

    const goClose = () => {
      emit('close')
    }

    const getColumnWidth = (columns: any[], col?: CardColumnConfig) => {
      // If the column has the fullWidth property, return 12 (full width)
      if (col?.fullWidth) {
        return 12
      }
      // Otherwise, calculate the width based on the number of columns
      const maxColumns = props.config[0].columns.length
      return 12 / maxColumns
    }

    const shouldShowColumn = (col: CardColumnConfig) => {
      // If showNullValues is explicitly set to false for this column, hide it if the value is null/undefined
      if (col.showNullValues === false && (col.value === null || col.value === undefined)) {
        return false // Hide the column
      }
      // If showOnlyNonEmptyValues is true, hide the column if the value is null/undefined
      if (props.showOnlyNonEmptyValues && (col.value === null || col.value === undefined)) {
        return false // Hide the column
      }
      // Otherwise, show the column
      return true
    }

    const getColumnValue = (col: any) => {
      // If showNullValues is explicitly set to false, return an empty string (hide the value)
      if (col.showNullValues === false && (col.value === null || col.value === undefined)) {
        return ''
      }
      // If showNullValues is true or showOnlyNonEmptyValues is true, return the value or an empty string
      if (col.showNullValues || props.showOnlyNonEmptyValues) {
        return col.value ?? ''
      }
      // Otherwise, return the value as-is
      return col.value
    }

    return {
      goBack,
      goClose,
      getColumnWidth,
      getColumnValue,
      shouldShowColumn
    }
  }
})
</script>

<style scoped lang="scss">
@use '@/assets/css/variables' as *;

.generic-report-card-view-card {
  margin: 3%;
  border-radius: 20px !important;
  min-height: 150px;
}

.generic-report-card-view-card-title {
  background-color: $color-green-dark;
  color: $white-color;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-generic-back-button {
  cursor: pointer;
}
</style>
