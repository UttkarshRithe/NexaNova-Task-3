/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        canvas: '#fff8f6',
        primary: {
          DEFAULT: '#cc785c',
          dark: '#ad5f45',
          light: '#ffb59d',
        },
        chrome: '#181715',
        surface: {
          DEFAULT: '#efe9de',
          container: '#faebe6',
          dim: '#e6d7d3',
          bright: '#fff8f6',
        },
        status: {
          success: '#3d5a45',
          warning: '#b3762b',
          error: '#a64432',
        }
      },
      fontFamily: {
        serif: ['Cormorant Garamond', 'serif'],
        sans: ['Inter', 'sans-serif'],
      },
      borderRadius: {
        DEFAULT: '0.25rem',
        sm: '0.125rem',
        md: '0.375rem',
        lg: '0.5rem',
        xl: '0.75rem',
      },
      spacing: {
        'section': '96px',
        'gutter': '24px',
        'edge': '40px',
      }
    },
  },
  plugins: [],
}
