import {Navlink} from 'react-router-dom';

const items = [
    {to:'/dashbaord', label: 'Dashboard'},
    {to:'/documents', label: 'Documents'},
    {to:'/types', label: 'Types'},
    {to: '/tags', label:'Tags'},
    {to:'/users', label: 'Users'}
];

export default function Sidebar(){
    return (
        <nav className="bg-white border-r p-4">
            <h1 className="text-xl font-bold mb-4">Paperless</h1>
            <nav className="space-y-1">
                {item.map(i => (
                    <NavLink
                        key={i.to}
                        to={i.to}
                        className={({ isActive }) =>
                            `block rounded px-3 py-2 text-sm ${isActive ? "bg-blue-500 text-white" : "text-gray-700"}`
                        }
                    >
                        {i.label}
                    </NavLink>
                ))}
            </nav>
        </nav>
    );
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/