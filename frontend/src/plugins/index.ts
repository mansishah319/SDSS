/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

/**
 * plugins/index.ts
 *
 * Automatically included in `./src/main.ts`
 */

import { App } from 'vue'
import { loadFonts } from './webfontloader'
import vuetify from './vuetify'
import pinia from '@/store'
import router from '@/router'
import i18n from './i18n'
import snotify from 'vue3-snotify'
import 'vue3-snotify/style'

export function registerPlugins(app: App) {
  loadFonts().then(() => console.log('All Fonts Loaded Successfully'))

  // Register i18n first
  app.use(i18n)

  // Register Vuetify
  app.use(vuetify)

  app.use(pinia).use(router).use(snotify)
}
