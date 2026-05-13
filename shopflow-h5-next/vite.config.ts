import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

const uniPlugin = (uni as unknown as { default?: () => unknown }).default ?? (uni as unknown as () => unknown)

export default defineConfig({
  base: './',
  plugins: [uniPlugin()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '0.0.0.0',
    port: 6257,
    proxy: {
      '/wx': {
        target: 'http://localhost:6914',
        changeOrigin: true,
      },
    },
  },
})
