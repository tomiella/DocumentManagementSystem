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
                bg:     '#0d1117',
                text:   '#a7ff83',
                btn1:   '#17b978',
                btn2:   '#086972',
            }
        }
    },
    plugins: [],
}

///*Note: if you find seomthing that is not working or is questionable, please just let me know
//     Lg Mike*/