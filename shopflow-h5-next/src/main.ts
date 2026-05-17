import { createPinia } from 'pinia'
import { createSSRApp } from 'vue'
import uviewPlus from 'uview-plus'
import App from './App.vue'
import { installH5TabBarStyleCompat } from '@/shared/compat/h5-tabbar-style-guard'
import { resolveLegacyHashRoute } from '@/shared/utils/legacy-route-map'
import './uni.scss'

// Tailwind/Vant base styles are H5-only and contain CSS syntax not supported by mp-weixin WXSS.
// Keep them out of mini-program builds and rely on page-level SCSS there.
// #ifdef H5
import '@/shared/ui/base.css'
// #endif

bootstrapLegacyHashRoute()
installH5TabBarStyleCompat()

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  app.use(pinia)
  app.use(uviewPlus)

  app.config.errorHandler = (error) => {
    console.error(error)
    uni.showToast({
      title: '页面出现异常，请稍后再试',
      icon: 'none',
    })
  }

  return {
    app,
    pinia,
  }
}

function bootstrapLegacyHashRoute() {
  // #ifdef H5
  const currentHref = window.location.href
  const nextRoute = resolveLegacyHashRoute(currentHref)

  if (!nextRoute || currentHref.includes(`#/pages/`) || currentHref.includes(`##/pages/`)) {
    return
  }

  const hashIndex = currentHref.indexOf('#')
  if (hashIndex < 0) {
    return
  }

  const nextHash = `#${nextRoute}`
  const nextHref = `${currentHref.slice(0, hashIndex)}${nextHash}`

  if (nextHref !== currentHref) {
    window.location.replace(nextHref)
  }
  // #endif
}
