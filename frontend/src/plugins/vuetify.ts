// Composable
import { createVuetify } from 'vuetify'
import { createVueI18nAdapter } from 'vuetify/locale/adapters/vue-i18n'
import { useI18n } from 'vue-i18n'

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Components and Directives
import * as components from 'vuetify/components'
import { VNumberInput } from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { VDateInput } from 'vuetify/labs/VDateInput'
// import { VTimePicker } from 'vuetify/labs/VTimePicker'
// Icons
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import i18n from './i18n'

const localizedDropdownDefaults = {
  menuProps: {
    contentClass: 'app-select-menu'
  }
}

const vuetify = createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi
    }
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#009c62',
          green: '#009c62',
          green_dark: '#005845',
          green_light: '#0da06e',
          black: '#000000',
          black_light: '#030000',
          white: '#ffffff',
          sub_menu_bg: '#f3f3f3',
          light: '#f2f4f8',
          scroller: '#e5e5e5',
          light_border: '#eaeaea'
        }
      },
      dark: {
        colors: {
          green: '#009c62',
          green_dark: '#022016',
          green_light: '#033021',
          black: '#ffffff',
          black_light: '#ffffff',
          white: '#212121',
          sub_menu_bg: '#212121',
          light: '#48494a',
          scroller: '#000000',
          light_border: '#000000'
        }
      }
    }
  },
  locale: {
    adapter: createVueI18nAdapter({
      i18n,
      useI18n
    }),
    rtl: {
      ar: true,
      en: false
    }
  },
  defaults: {
    VAutocomplete: localizedDropdownDefaults,
    VCombobox: localizedDropdownDefaults,
    VSelect: localizedDropdownDefaults
  },
  components: {
    ...components,
    VNumberInput,
    VDateInput
    // VTimePicker,
  },
  directives
})

export default vuetify
