<script setup lang="ts">
import { computed, onBeforeUnmount, watch } from 'vue'
import { EditorContent, useEditor } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Link from '@tiptap/extension-link'
import Placeholder from '@tiptap/extension-placeholder'
import TextAlign from '@tiptap/extension-text-align'
import Underline from '@tiptap/extension-underline'

type ToolbarAction = {
  icon: string
  title: string
  isActive?: () => boolean
  isDisabled?: () => boolean
  run: () => void
}

const props = withDefaults(
  defineProps<{
    modelValue: string
    placeholder?: string
    rtl?: boolean
    disabled?: boolean
  }>(),
  {
    placeholder: '',
    rtl: false,
    disabled: false
  }
)

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
}>()

const normalizeHtml = (value: string) => {
  const trimmed = value.trim()
  return trimmed === '<p></p>' ? '' : value
}

const editor = useEditor({
  content: props.modelValue || '',
  editable: !props.disabled,
  extensions: [
    StarterKit.configure({
      heading: {
        levels: [2, 3]
      }
    }),
    Underline,
    Link.configure({
      openOnClick: false,
      autolink: true,
      defaultProtocol: 'https'
    }),
    Placeholder.configure({
      placeholder: props.placeholder
    }),
    TextAlign.configure({
      types: ['heading', 'paragraph']
    })
  ],
  onUpdate: ({ editor: currentEditor }) => {
    emit('update:modelValue', normalizeHtml(currentEditor.getHTML()))
  }
})

watch(
  () => props.modelValue,
  (value) => {
    if (!editor.value) {
      return
    }

    const nextValue = value || ''
    if (nextValue === normalizeHtml(editor.value.getHTML())) {
      return
    }

    editor.value.commands.setContent(nextValue, false)
  }
)

watch(
  () => props.disabled,
  (value) => {
    editor.value?.setEditable(!value)
  }
)

const setLink = () => {
  if (!editor.value) {
    return
  }

  const previousUrl = editor.value.getAttributes('link').href || ''
  const url = window.prompt('Enter URL', previousUrl)

  if (url === null) {
    return
  }

  const trimmedUrl = url.trim()
  if (!trimmedUrl) {
    editor.value.chain().focus().unsetLink().run()
    return
  }

  editor.value.chain().focus().extendMarkRange('link').setLink({ href: trimmedUrl }).run()
}

const toolbarActions = computed<ToolbarAction[]>(() => {
  if (!editor.value) {
    return []
  }

  return [
    {
      icon: 'mdi-format-header-2',
      title: 'Heading',
      isActive: () => editor.value?.isActive('heading', { level: 2 }) ?? false,
      run: () => editor.value?.chain().focus().toggleHeading({ level: 2 }).run()
    },
    {
      icon: 'mdi-format-paragraph',
      title: 'Paragraph',
      isActive: () => editor.value?.isActive('paragraph') ?? false,
      run: () => editor.value?.chain().focus().setParagraph().run()
    },
    {
      icon: 'mdi-format-bold',
      title: 'Bold',
      isActive: () => editor.value?.isActive('bold') ?? false,
      run: () => editor.value?.chain().focus().toggleBold().run()
    },
    {
      icon: 'mdi-format-italic',
      title: 'Italic',
      isActive: () => editor.value?.isActive('italic') ?? false,
      run: () => editor.value?.chain().focus().toggleItalic().run()
    },
    {
      icon: 'mdi-format-underline',
      title: 'Underline',
      isActive: () => editor.value?.isActive('underline') ?? false,
      run: () => editor.value?.chain().focus().toggleUnderline().run()
    },
    {
      icon: 'mdi-format-list-bulleted',
      title: 'Bulleted list',
      isActive: () => editor.value?.isActive('bulletList') ?? false,
      run: () => editor.value?.chain().focus().toggleBulletList().run()
    },
    {
      icon: 'mdi-format-list-numbered',
      title: 'Numbered list',
      isActive: () => editor.value?.isActive('orderedList') ?? false,
      run: () => editor.value?.chain().focus().toggleOrderedList().run()
    },
    {
      icon: 'mdi-format-quote-close',
      title: 'Blockquote',
      isActive: () => editor.value?.isActive('blockquote') ?? false,
      run: () => editor.value?.chain().focus().toggleBlockquote().run()
    },
    {
      icon: 'mdi-link-variant',
      title: 'Link',
      isActive: () => editor.value?.isActive('link') ?? false,
      run: setLink
    },
    {
      icon: 'mdi-link-variant-off',
      title: 'Remove link',
      isDisabled: () => !(editor.value?.isActive('link') ?? false),
      run: () => editor.value?.chain().focus().unsetLink().run()
    },
    {
      icon: 'mdi-format-align-left',
      title: 'Align left',
      isActive: () => editor.value?.isActive({ textAlign: 'left' }) ?? false,
      run: () => editor.value?.chain().focus().setTextAlign('left').run()
    },
    {
      icon: 'mdi-format-align-center',
      title: 'Align center',
      isActive: () => editor.value?.isActive({ textAlign: 'center' }) ?? false,
      run: () => editor.value?.chain().focus().setTextAlign('center').run()
    },
    {
      icon: 'mdi-format-align-right',
      title: 'Align right',
      isActive: () => editor.value?.isActive({ textAlign: 'right' }) ?? false,
      run: () => editor.value?.chain().focus().setTextAlign('right').run()
    },
    {
      icon: 'mdi-undo',
      title: 'Undo',
      isDisabled: () => !(editor.value?.can().chain().focus().undo().run() ?? false),
      run: () => editor.value?.chain().focus().undo().run()
    },
    {
      icon: 'mdi-redo',
      title: 'Redo',
      isDisabled: () => !(editor.value?.can().chain().focus().redo().run() ?? false),
      run: () => editor.value?.chain().focus().redo().run()
    }
  ]
})

onBeforeUnmount(() => {
  editor.value?.destroy()
})
</script>

<template>
  <div
    class="app-rich-text-editor"
    :class="{ 'app-rich-text-editor--rtl': rtl, 'app-rich-text-editor--disabled': disabled }"
  >
    <div class="app-rich-text-editor__toolbar">
      <button
        v-for="action in toolbarActions"
        :key="action.title"
        class="app-rich-text-editor__tool"
        :class="{ 'is-active': action.isActive?.(), 'is-disabled': action.isDisabled?.() }"
        :title="action.title"
        type="button"
        :disabled="disabled || action.isDisabled?.()"
        @click="action.run"
      >
        <span class="mdi" :class="action.icon"></span>
      </button>
    </div>

    <EditorContent v-if="editor" :editor="editor" class="app-rich-text-editor__content" />
  </div>
</template>

<style scoped lang="scss">
.app-rich-text-editor {
  border: 1px solid rgba(15, 23, 42, 0.14);
  border-radius: 18px;
  background: #ffffff;
  overflow: hidden;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.app-rich-text-editor__toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc 0%, #eef4f8 100%);
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.app-rich-text-editor__tool {
  width: 38px;
  height: 38px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 12px;
  background: #ffffff;
  color: #334155;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover:not(:disabled) {
    border-color: rgba(21, 112, 239, 0.28);
    color: #1570ef;
  }

  &.is-active {
    background: rgba(21, 112, 239, 0.08);
    border-color: rgba(21, 112, 239, 0.32);
    color: #1570ef;
  }

  &:disabled,
  &.is-disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.app-rich-text-editor__content :deep(.tiptap) {
  min-height: 220px;
  padding: 18px;
  color: #111827;
  line-height: 1.7;
  outline: none;
  word-break: break-word;

  p {
    margin: 0 0 12px;
  }

  h2,
  h3 {
    margin: 0 0 12px;
    line-height: 1.4;
  }

  ul,
  ol {
    padding-inline-start: 24px;
    margin: 0 0 12px;
  }

  blockquote {
    border-inline-start: 4px solid rgba(21, 112, 239, 0.3);
    margin: 0 0 12px;
    padding-inline-start: 14px;
    color: #475467;
  }

  a {
    color: #1570ef;
    text-decoration: underline;
  }

  &.ProseMirror-focused {
    background: linear-gradient(180deg, rgba(21, 112, 239, 0.02) 0%, rgba(255, 255, 255, 0) 100%);
  }

  p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    color: #98a2b3;
    float: left;
    height: 0;
    pointer-events: none;
  }
}

.app-rich-text-editor--rtl .app-rich-text-editor__content :deep(.tiptap) {
  direction: rtl;
  text-align: right;
}

.app-rich-text-editor--disabled {
  opacity: 0.78;
}

@media (max-width: 600px) {
  .app-rich-text-editor__toolbar {
    gap: 6px;
    padding: 10px;
  }

  .app-rich-text-editor__tool {
    width: 34px;
    height: 34px;
  }
}
</style>
