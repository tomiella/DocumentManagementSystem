// placement: D - Sidebar (column 1)
import { NavLink } from 'react-router-dom';

const items = [
    { to: '/signup', label: 'Sign up' },
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/upload', label: 'Upload' },
    { to: '/types', label: 'Types' },
    { to: '/tags', label: 'Tags' },
    // { to: '/users', label: 'Users' }
];

export default function TocNav() {
    return (
        <nav className="bg-bg border-r p-4">
            <nav className="space-y-1">
                {items.map((i) => (
                    <NavLink
                        key={i.to}
                        to={i.to}
                        className={({ isActive }) =>
                            ` block rounded px-3 py-2 text-sm ${isActive ? 'bg-green-600 text-white' : 'bg-emerald-200 text-green-800'}`
                        }
                    >
                        {i.label}
                    </NavLink>
                ))}
            </nav>
        </nav>
    );
}