// frontend/src/app/routes.tsx
import {Navigate, Route, Routes} from "react-router-dom";
import AppLayout from "../layouts/AppLayout";
import Dashboard from "../pages/Dashboard";
import Documents from "../pages/Documents";
import Upload from "../pages/Upload";
import Signup from "../pages/Signup";
import Profile from "../pages/Profile";



export default function AppRoutes() {
    return (

        <Routes>
            <Route element={<AppLayout/>}>
                <Route index element={<Navigate to="/dashboard" replace />} />
                <Route path="dashboard" element={<Dashboard />} />
                <Route path="documents" element={<Documents />} />
                <Route path="upload" element={<Upload />} />
                <Route path="signup" element={<Signup />} />
                <Route path="profile" element={<Profile />} />
           </Route>
        </Routes>
    )

}
