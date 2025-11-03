import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';

createRoot(document.getElementById('root')!).render(
    <React.StrictMode>

    <BrowserRouter>
        <App>

        </App>
    </BrowserRouter>);
    </React.StrictMode>

);

/*Note: if you find something that is not working or is questionable, please just let me know
Lg Mike*/