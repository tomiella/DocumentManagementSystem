import {NavLink, Routes, Route, Navigate} from "react-router-dom";
import DocTable from "./DocTable";    /*TODO*/
import Upload from "../Upload";
import Function3 from "./Function3";    /*TODO*/
import FiltersBar from "./FiltersBar";  /*TODO*/

export default function DocTopBar() {
    const tabs = [
        {to: "documents", label: 'Documents'},
        {to: "upload", label: 'Upload'},
        {to: "extra", label: 'Function 3'},
    ];

    /*//Question: why do it like this? ANSWER: We will ask the questions!!*/
    return (
        <div className ="flex flex-col gap-3">
            {/*F: Tabs*/}
            <div className="flex gap-2">
                {tabs.map(t => (
                    <NavLink
                        key={t.to}
                        to={t.to}
                        className={({ isActive }) =>
                            `px-3 py-2 rounded border text-sm ${
                            isActive ? "bg-blue-400" : "bg-blue-100"
                            }`
                        }
                    >
                        {t.label}
                    </NavLink>
                ))}
            </div>

            {/*G: Placeholder for FiltersBar*/}
            <Routes>
                <Route path="table" element={
                    <div className="space-y-3">
                        <FiltersBar />
                        <DocTable />
                    </div>
                }
               />

                <Route path="upload" element={<Upload />} />
                <Route path="extra" element={<Function3/>} />
                <Route index element={<Navigate to="table" replace />} />
            </Routes>
        </div>
    );
}
