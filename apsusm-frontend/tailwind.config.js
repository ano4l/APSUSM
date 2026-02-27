/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    fontFamily: {
      sans: ['Inter', 'sans-serif'],
    },
    extend: {
      colors: {
        brand: {
          red: '#D71920',
          blue: '#007ACC',
          green: '#8BC53F',
          purple: '#662D91',
          yellow: '#FFD700',
        },
        slate: {
          950: '#030712',
        }
      },
    },
  },
  plugins: [],
}
