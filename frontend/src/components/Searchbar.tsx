export default function Searchbar() {
    return (
        <div className="flex items-center gap-2">
            <input className="border rounded px-3 py-2 text-sm w-64" placeholder="Search" />
            <button className="border rounded px-3 py-2 text-sm">Search</button>
        </div>
    );
}