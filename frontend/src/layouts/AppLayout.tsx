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
            className="
        min-h-screen
        grid
        grid-cols-[240px_1fr_280px]
        gap-3 p-3
      "
        >
            {/* Column 1: A over D */}
            <section className="col-start-1 col-end-2 grid grid-rows-[auto_1fr] gap-3">
                <header>
                    <Logo />
                </header>
                <aside className="overflow-auto">
                    <TocNav />
                </aside>
            </section>

            {/* Column 2: B over E (+ F/G inside Documents) */}
            <section className="col-start-2 col-end-3 grid grid-rows-[auto_1fr] gap-3">
                <div>
                    <SearchBar />
                </div>
                <main className="overflow-auto">
                    <Outlet />
                </main>
            </section>

            {/* Column 3: C over H */}
            <section className="col-start-3 col-end-4 grid grid-rows-[auto_1fr] gap-3">
                <div>
                    <UserMenu />
                </div>
                <aside className="overflow-auto">
                    <RightStats />
                </aside>
            </section>
        </div>
    )
}