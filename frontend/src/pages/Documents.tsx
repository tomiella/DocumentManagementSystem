// placement: E - Main Content

import Table from '../components/Table';
import {useEffect, useState} from "react";


const rows = [
    { date:'28.09.24', title:'Hematologie', tags:['scanned','info'], owner:'Max M.', access:'Private', comments:'…' },
    { date:'15.12.24', title:'ESTA USA 2025', tags:['party'], owner:'Silvia M.', access:'Public', comments:'…' },
];


export default function Documents() {
    const [userName,setUserName] = useState<string | null>(null);

    useEffect(() => {
        const updateFromSession = () => setUserName(sessionStorage.getItem('userName'));
        updateFromSession();
        window.addEventListener('auth:changed', updateFromSession);
        window.addEventListener('storage', updateFromSession);
        return () => {
            window.removeEventListener('auth:changed', updateFromSession);
            window.removeEventListener('storage', updateFromSession);
        };
    }, []);


    return (
            <div className="space-y-4">
                <h2 className="text-2xl font-semibold">
                    {userName? `Hello, ${userName}!`:
                        `Hello Paperfriendly World!!`}
                </h2>
                <div className="grid grid-cols-2 gap-6">
                    <div className="bg-red-300 border rounded h-20" />
                    <div className="bg-blue-300 border rounded h-20" />
                </div>
                <div className="w-full overflow-x-auto">
                    <Table rows={rows} />
                </div>
            </div>
    );
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/