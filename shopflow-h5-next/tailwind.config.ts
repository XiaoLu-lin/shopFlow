import type { Config } from 'tailwindcss'
import forms from '@tailwindcss/forms'

export default {
  content: ['./index.html', './src/**/*.{vue,ts,tsx}'],
  corePlugins: {
    preflight: false,
  },
  theme: {
    extend: {
      colors: {
        ink: 'rgb(var(--sf-color-ink) / <alpha-value>)',
        shell: 'rgb(var(--sf-color-shell) / <alpha-value>)',
        mist: 'rgb(var(--sf-color-mist) / <alpha-value>)',
        brand: {
          DEFAULT: 'rgb(var(--sf-color-brand) / <alpha-value>)',
          soft: 'rgb(var(--sf-color-brand-soft) / <alpha-value>)',
          deep: 'rgb(var(--sf-color-brand-deep) / <alpha-value>)',
        },
        accent: 'rgb(var(--sf-color-accent) / <alpha-value>)',
        sage: 'rgb(var(--sf-color-sage) / <alpha-value>)',
        lilac: 'rgb(var(--sf-color-lilac) / <alpha-value>)',
        sky: 'rgb(var(--sf-color-sky) / <alpha-value>)',
        peach: 'rgb(var(--sf-color-peach) / <alpha-value>)',
        success: 'rgb(var(--sf-color-success) / <alpha-value>)',
        danger: 'rgb(var(--sf-color-danger) / <alpha-value>)',
        line: 'rgb(var(--sf-color-line) / <alpha-value>)',
      },
      fontFamily: {
        display: ['"PingFang SC"', '"Hiragino Sans GB"', '"Microsoft YaHei"', 'sans-serif'],
        body: ['"PingFang SC"', '"Hiragino Sans GB"', '"Microsoft YaHei"', 'sans-serif'],
      },
      boxShadow: {
        card: 'var(--sf-shadow-card)',
        glow: 'var(--sf-shadow-brand)',
        soft: 'var(--sf-shadow-soft)',
      },
      borderRadius: {
        shell: 'var(--sf-radius-card)',
      },
      backgroundImage: {
        'hero-mesh':
          'linear-gradient(180deg, rgba(255,255,255,1), rgba(249,250,251,1))',
      },
    },
  },
  plugins: [forms],
} satisfies Config
