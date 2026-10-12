/*
 *  ***************************************************************
 *  Copyright ©1956-2023 DubaiPolice. All rights reserved
 *  ——————————————————————————————————
 *  NOTICE: All information contained herein is a property of DubaiPolice.
 *  ***************************************************************
 */

import { ConfigEnv, defineConfig, loadEnv, Plugin, UserConfig } from 'vite'
import { resolve } from 'node:path'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import autoprefixer from 'autoprefixer'
import tailwindcss from 'tailwindcss'
import { visualizer } from 'rollup-plugin-visualizer'
import fs from 'node:fs'

type ChunkRule = {
  match: string
  chunk: string
}

const vendorChunkRules: ChunkRule[] = [
  { match: '@vue/runtime', chunk: 'vue-runtime' },
  { match: '@vue/reactivity', chunk: 'vue-reactivity' },
  { match: '@vue/shared', chunk: 'vue-shared' },
  { match: 'vue-router', chunk: 'vue-router' },
  { match: 'vue-i18n', chunk: 'vue-i18n' },
  { match: 'vuetify/lib/components', chunk: 'vuetify-components' },
  { match: 'vuetify/lib/directives', chunk: 'vuetify-directives' },
  { match: 'vuetify/lib/styles', chunk: 'vuetify-styles' },
  { match: 'vuetify', chunk: 'vuetify-core' },
  { match: '@mdi/', chunk: 'vendor-icons' },
  { match: 'chart.js', chunk: 'vendor-charts' },
  { match: '@floating-ui', chunk: 'vendor-floating-ui' },
  { match: 'pinia', chunk: 'vendor-pinia' },
  { match: 'axios', chunk: 'vendor-axios' },
  { match: 'date-fns', chunk: 'vendor-date-fns' },
  { match: 'lodash', chunk: 'vendor-lodash' }
]

const componentChunkRules: ChunkRule[] = [
  { match: '/common/', chunk: 'ui-common' },
  { match: '/generic/', chunk: 'ui-generic' },
  { match: '/charts/', chunk: 'ui-charts' },
  { match: '/dialog/', chunk: 'ui-dialogs' },
  { match: '/template/', chunk: 'ui-templates' },
  { match: '/account/', chunk: 'ui-account' },
  { match: '/challenge/', chunk: 'ui-challenge' }
]

const challengeViewChunkRules: ChunkRule[] = [
  { match: '/list/', chunk: 'feature-challenges-list' },
  { match: '/creation/', chunk: 'feature-challenges-create' },
  { match: '/dashboard/', chunk: 'feature-challenges-dashboard' }
]

const surveyViewChunkRules: ChunkRule[] = [
  { match: '/list/', chunk: 'feature-surveys-list' },
  { match: '/creation/', chunk: 'feature-surveys-create' },
  { match: '/dashboard/', chunk: 'feature-surveys-dashboard' }
]

const coreChunkRules: ChunkRule[] = [
  { match: '/services/', chunk: 'core-services' },
  { match: '/store/', chunk: 'core-store' },
  { match: '/utils/', chunk: 'core-utils' },
  { match: '/plugins/', chunk: 'core-plugins' },
  { match: '/constants/', chunk: 'core-constants' },
  { match: '/dto/', chunk: 'core-dto' }
]

function findChunkByRules(id: string, rules: ChunkRule[]): string | undefined {
  return rules.find((rule) => id.includes(rule.match))?.chunk
}

function resolveVendorChunk(id: string): string {
  const matchedChunk = findChunkByRules(id, vendorChunkRules)
  if (matchedChunk) {
    return matchedChunk
  }

  const fileName = id.split('/node_modules/').pop()
  if (!fileName) {
    return 'vendor-misc'
  }

  return `vendor-${fileName.charAt(0).toLowerCase()}`
}

function resolveComponentsChunk(id: string): string {
  return findChunkByRules(id, componentChunkRules) ?? 'ui-misc'
}

function resolveViewsChunk(id: string): string {
  if (id.includes('/challenges/')) {
    return findChunkByRules(id, challengeViewChunkRules) ?? 'feature-challenges-misc'
  }

  if (id.includes('/surveys/')) {
    return findChunkByRules(id, surveyViewChunkRules) ?? 'feature-surveys-misc'
  }

  if (id.includes('/Users/')) {
    return 'feature-users'
  }

  if (id.includes('/dashboard/')) {
    return 'feature-dashboard'
  }

  return 'feature-misc'
}

function resolveSourceChunk(id: string): string | undefined {
  if (!id.includes('src/')) {
    return undefined
  }

  if (id.includes('src/components/')) {
    return resolveComponentsChunk(id)
  }

  if (id.includes('src/views/')) {
    return resolveViewsChunk(id)
  }

  return findChunkByRules(id, coreChunkRules)
}

function resolveManualChunk(id: string): string | undefined {
  if (id.includes('node_modules')) {
    return resolveVendorChunk(id)
  }

  return resolveSourceChunk(id)
}

function pathResolve(dir: string) {
  return resolve(process.cwd(), '.', dir)
}

function removeArtifacts(
  rootDir: string,
  matcher: (_entryPath: string, _entryName: string, _isDirectory: boolean) => boolean
) {
  if (!fs.existsSync(rootDir)) {
    return
  }

  const entries = fs.readdirSync(rootDir)

  entries.forEach((entry) => {
    const fullPath = resolve(rootDir, entry)
    const stat = fs.statSync(fullPath)
    const isDirectory = stat.isDirectory()

    if (matcher(fullPath, entry, isDirectory)) {
      if (isDirectory) {
        fs.rmSync(fullPath, { recursive: true, force: true })
        return
      }

      fs.unlinkSync(fullPath)
      return
    }

    if (isDirectory) {
      removeArtifacts(fullPath, matcher)
    }
  })
}

function cleanSassCachePlugin(): Plugin {
  return {
    name: 'clean-sass-cache',
    apply: 'build' as const,
    buildStart() {
      const srcDir = pathResolve('src')
      removeArtifacts(srcDir, (_entryPath, entryName, isDirectory) => isDirectory && entryName === '.sass-cache')
    }
  }
}

export default defineConfig(({ command, mode }: ConfigEnv): UserConfig => {
  // Load environment variables based on the mode
  const env = loadEnv(mode, process.cwd())
  process.env = { ...process.env, ...env }
  process.env.NODE_ENV = mode
  process.env.SASS_NO_CACHE = 'true'
  process.env.SASS_CACHE = 'false'

  // Log the current environment mode
  console.log(`Current environment mode: ${process.env.NODE_ENV} and command is: ${command}`)

  return {
    define: {
      global: 'globalThis',
      'process.env': {}
    },
    plugins: [
      vue(),
      vuetify({
        autoImport: true,
        styles: 'sass'
      }),
      cleanSassCachePlugin(),
      ...(process.env.NODE_ENV === 'production'
        ? [
          visualizer({
            filename: './dist/sdss-analysis-stats.html',
            open: true,
            brotliSize: true,
            gzipSize: true
          }) as any
        ]
        : [])
    ],
    build: {
      target: 'es2020',
      // Ensure CSS code splitting is disabled
      cssCodeSplit: false,
      // Disable source maps completely
      sourcemap: false,
      chunkSizeWarningLimit: 3200, // Increased limit
      rolldownOptions: {
        checks: {
          commonJsVariableInEsm: false,
          pluginTimings: false
        },
        output: {
          codeSplitting: {
            groups: [
              {
                name: (moduleId) => resolveManualChunk(moduleId) ?? null
              }
            ]
          },
          assetFileNames: 'assets/[name].[hash][extname]'
        }
      },
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true,
          pure_funcs: ['console.log', 'console.info', 'console.debug', 'console.trace']
        },
        mangle: {
          safari10: true
        }
      },
      assetsInlineLimit: 4096,
      cssTarget: 'chrome107'
    },
    base: '/sdss',
    css: {
      devSourcemap: false,
      postcss: {
        plugins: [tailwindcss(), autoprefixer()]
      },
      preprocessorOptions: {
        scss: {
          loadPaths: [pathResolve('src')],
          quietDeps: true,
          charset: false
        }
      }
    },
    resolve: {
      alias: [
        {
          find: /\/#\//,
          replacement: pathResolve('types') + '/'
        },
        {
          find: '@',
          replacement: pathResolve('src') + '/'
        },
        {
          find: /^buffer$/,
          replacement: 'buffer'
        },
        {
          find: /^process$/,
          replacement: 'process/browser.js'
        }
      ],
      dedupe: ['vue'],
      extensions: ['.ts', '.js', '.vue', '.json']
    },
    server: {
      port: 5173
    },
    optimizeDeps: {
      include: [
        'date-fns',
        'vuetify/components',
        'vuetify/directives',

        'buffer',
        'process',

        // Core components (from your existing config)
        'vuetify/components/VSkeletonLoader',
        'vuetify/components/VAlert',
        'vuetify/components/VRadio',
        'vuetify/components/VRadioGroup',
        'vuetify/components/VAutocomplete',
        'vuetify/components/VCheckbox',
        'vuetify/components/VPagination',
        'vuetify/components/VFileInput',
        'vuetify/components/VForm',
        'vuetify/components/VChipGroup',
        'vuetify/components/VRating',

        // New components from the optimization log
        'vuetify/components/VApp',
        'vuetify/components/VAppBar',
        'vuetify/components/VAvatar',
        'vuetify/components/VBtn',
        'vuetify/components/VCard',
        'vuetify/components/VChip',
        'vuetify/components/VDataTable',
        'vuetify/components/VDatePicker',
        'vuetify/components/VDialog',
        'vuetify/components/VDivider',
        'vuetify/components/VExpansionPanel',
        'vuetify/components/VFooter',
        'vuetify/components/VGrid',
        'vuetify/components/VHover',
        'vuetify/components/VIcon',
        'vuetify/components/VImg',
        'vuetify/components/VList',
        'vuetify/components/VMain',
        'vuetify/components/VMenu',
        'vuetify/components/VOverlay',
        'vuetify/components/VProgressCircular',
        'vuetify/components/VProgressLinear',
        'vuetify/components/VSelect',
        'vuetify/components/VSheet',
        'vuetify/components/VSnackbar',
        'vuetify/components/VTable',
        'vuetify/components/VTabs',
        'vuetify/components/VTextField',
        'vuetify/components/VTextarea',
        'vuetify/components/VTimePicker',
        'vuetify/components/VToolbar',
        'vuetify/components/VTooltip',
        'vuetify/components/VWindow',
        'vuetify/components/transitions',
        'vuetify/components/VTimeline',
        'vuetify/components/VSwitch'
      ],
      exclude: [
        'exceljs'
      ]
    }
  }
})
