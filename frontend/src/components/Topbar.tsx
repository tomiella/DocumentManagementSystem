import SearchBar from './Searchbar';

export default function Topbar() {
    return (
        <header className="bg-white border-b h-14 flex items-center px-4">
            <SearchBar />
            <div className="flex items-center gap-3">
            </div>
                <span className="text-sm text-gray-600">Welcome, </span>
            <div className="size-9 rounded-full bg-gray"></div>
        </header>

    )
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/