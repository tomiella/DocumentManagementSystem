import {Routes, Route, Navigate} from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Topbar from './components/Topbar';
import Dashboard from './pages/Dashboard';
import Documents from './pages/Documents';
import Upload from './pages/Upload';
import Login from './pages/Login';
import Disabled from './pages/Disabled';



export default function App() {
    return (
        <div className="min-h-screen grid grid-cols-[240px_1fr]">
            <Sidebar/>
            <div className="flex flex-col">
                <Topbar />
                <main className="p-6">
                    <Routes>
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/documents" element={<Documents />} />
                        <Route path="/upload" element={<Upload />} />
                        <Route path="/disabled" element={<Disabled />} />
                    </Routes>
                </main>
            </div>
        </div>
    );
}
