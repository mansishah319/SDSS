import globals from 'globals'
import pluginVue from 'eslint-plugin-vue'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'
import { configureVueProject, defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript'

configureVueProject({
  rootDir: import.meta.dirname,
  scriptLangs: ['ts']
})

export default defineConfigWithVueTs(
  {
    name: 'sdss/ignores',
    ignores: ['dist/**', 'coverage/**', 'node_modules/**', 'public/**']
  },
  {
    files: ['**/*.{js,mjs,cjs,jsx,ts,mts,cts,tsx,vue}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        ...globals.browser,
        ...globals.node
      }
    }
  },
  {
    files: ['**/*.{cjs,cts}'],
    languageOptions: {
      sourceType: 'commonjs'
    }
  },
  pluginVue.configs['flat/essential'],
  vueTsConfigs.base,
  {
    name: 'sdss/custom-rules',
    rules: {
      '@typescript-eslint/no-explicit-any': 'off',
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': [
        'error',
        {
          ignoreRestSiblings: true,
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_'
        }
      ],
      'vue/block-lang': 'off'
    }
  },
  skipFormatting
)
