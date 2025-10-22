/** @type {import('tailwindcss').Config} */

export default {
    content: [
        "./src/**/*.{js,jsx,ts,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                'primary': '#0461CC',
                'secondary': '#60D2D2',
                'tertiary': '#6B2FFF',
                'quaternary': '#D600D6',
            }
        }
    },
    plugins: [],
}