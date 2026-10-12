// store/sidebar.ts
import { defineStore } from 'pinia'

const SIDEBAR_VISIBILITY_KEY = 'currentUserSideBarVisibility'
const SIDEBAR_ACTIVE_PANEL_KEY = 'currentUserSideBarActivePanel'

export const useSidebarStore = defineStore('sidebar', {
  state: () => ({
    isVisible: localStorage.getItem(SIDEBAR_VISIBILITY_KEY) === 'true',
    activePanel: localStorage.getItem(SIDEBAR_ACTIVE_PANEL_KEY) || null
  }),
  actions: {
    toggleVisibility() {
      this.isVisible = !this.isVisible
      localStorage.setItem(SIDEBAR_VISIBILITY_KEY, this.isVisible.toString())
    },
    show() {
      this.isVisible = true
      localStorage.setItem(SIDEBAR_VISIBILITY_KEY, 'true')
    },
    disable() {
      this.isVisible = false
      localStorage.setItem(SIDEBAR_VISIBILITY_KEY, 'false')
    },
    setActivePanel(panel: string | null) {
      this.activePanel = panel

      if (panel) {
        localStorage.setItem(SIDEBAR_ACTIVE_PANEL_KEY, panel)
      } else {
        localStorage.removeItem(SIDEBAR_ACTIVE_PANEL_KEY)
      }
    }
  }
})
