/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Primary gradient colors
        primary: {
          500: '#667eea',
          600: '#764ba2',
        },
        // Income colors
        income: {
          400: '#10b981',
          500: '#10b981',
          600: '#059669',
        },
        // Expense colors
        expense: {
          400: '#ef4444',
          500: '#ef4444',
          600: '#dc2626',
        },
        // Accent colors
        accent: {
          400: '#3b82f6',
          500: '#3b82f6',
          600: '#2563eb',
        },
      },
      backgroundImage: {
        'gradient-main': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'gradient-income': 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
        'gradient-expense': 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)',
        'gradient-accent': 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
        'gradient-animated': 'linear-gradient(-45deg, #667eea, #764ba2, #f093fb, #4facfe)',
      },
      backdropBlur: {
        xs: '2px',
      },
      boxShadow: {
        'glass': '0 8px 32px 0 rgba(31, 38, 135, 0.37)',
        'glass-lg': '0 12px 48px 0 rgba(31, 38, 135, 0.5)',
        'glow': '0 0 20px rgba(102, 126, 234, 0.5)',
      },
      animation: {
        'gradient': 'gradient 15s ease infinite',
      },
      keyframes: {
        gradient: {
          '0%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
          '100%': { backgroundPosition: '0% 50%' },
        },
      },
    },
  },
  plugins: [],
}
