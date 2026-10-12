<!--
  - * ***************************************************************
  - * Copyright ©1956-2024 DubaiPolice. All rights reserved
  - * ——————————————————————————————————
  - * NOTICE: All information contained herein is a property of DubaiPolice.
  - * ***************************************************************
  -->

<template>
  <v-dialog v-model="dialog" width="auto" @click:outside="$emit('closeDialog')">
    <v-card>
      <v-card-text>
        <v-table class="dp-primary-table modal-table1 table-style-2">
          <thead>
          <tr>
            <th scope="col">{{ headers[0] }}</th>
            <th scope="col">{{ headers[1] }}</th>
            <th scope="col">{{ headers[2] }}</th>
            <th v-if="appearActions" scope="col">
              {{ $t('common.actions') }}
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in items" :key="item.name">
            <td>
              {{ item.head1 }}
            </td>
            <td>{{ item.head2 }}</td>
            <td>{{ item.head3 }}</td>
            <td v-if="appearActions">
              <v-row justify="center" align="center">
                <v-col cols="4">
                  <v-btn density="compact" class="accept-icon" icon="mdi-check" @click="approveItem(item)"></v-btn>
                </v-col>
                <v-col cols="4">
                  <v-btn density="compact" icon="mdi-close" class="cancel-icon" @click="rejectItem(item)"></v-btn>
                </v-col>
                <v-col cols="4">
                  <v-btn variant="text" icon="mdi-monitor-dashboard" color="blue" @click="rejectItem(item)"></v-btn>
                </v-col>
              </v-row>
            </td>
          </tr>
          </tbody>
        </v-table>
      </v-card-text>
      <template #actions>
        <v-btn
          class="ms-auto"
          :text="$t('common.close')"
          style="background-color: red"
          color="white"
          @click="closeDialog"
        ></v-btn>
      </template>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    dialogValue: {
      type: Boolean,
      default: false
    },
    appearActions: {
      type: Boolean,
      default: false
    },
    items: {
      type: Array,
      default: () => []
    },
    headers: { type: Array, default: () => [] }
  },
  emits: ['closeDialog'],
  data() {
    return {
      dialog: false
    }
  },
  watch: {
    dialogValue(value) {
      this.dialog = value
    }
  },
  methods: {
    closeDialog() {
      this.dialog = false
      this.$emit('closeDialog')
    }
  }
}
</script>

<style src="./AdminDashboard.scss" lang="scss" scoped></style>
