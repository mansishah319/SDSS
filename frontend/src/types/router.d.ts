import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    fullScreen?: boolean
  }
}
