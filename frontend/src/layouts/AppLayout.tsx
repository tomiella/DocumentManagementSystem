// WebUI placement --> see WebUi Skeleton.png
import {Outlet} from "react-router-dom"; //Flag
import Logo from "../components/Logo"
import SearchBar from "../components/SearchBar";
import UserMenu from "../components/UserMenu";
import TocNav from "../components/TocNav";
import RightStats from "../components/StatsPanel";

export default function AppLayout() {
    return (
        <div
            className="min-h-screen grid grid-cols-[16rem_1fr_16rem] grid-rows-[auto_1fr_auto] bg-bg text-text">
            {/* Row 1: Top bar (A+ B + C) */}
            <header className="col-span-3 flex items-center justify-between border-b p-3">
                <Logo />
                <SearchBar />
                <UserMenu />
            </header>


            {/* Row 2: Main body (D + E + F) */}

            <aside className="border-r p-2 bg-bg"> <TocNav /> </aside>
            <main className="p-4 overflow-y-auto"> <Outlet /> </main>
            <aside className="border-r p-2 bg-bg"> <RightStats /> </aside>

            {/* Row 3: Footer (G) */}
            <footer className="col-span-3 text-center text-sm py-2 border-t bg-emerald-950">
                © 2025 Paperless TM
            </footer>

        </div>
    )
}