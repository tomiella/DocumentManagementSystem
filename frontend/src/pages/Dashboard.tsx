// placement E - Main Content

import Table from '../components/Table';
import {useEffect, useState} from "react";


const rows = [ //NOTE: Currently just a placeholder
    { date:'28.09.24', title:'Hematologie', tags:['scanned','info'], owner:'Max M.', access:'Private', comments:'…' },
    { date:'15.12.24', title:'ESTA USA 2025', tags:['party'], owner:'Silvia M.', access:'Public', comments:'…' },
    { date: '12.11.24', title: 'Visa Interview Prep', tags: ['travel', 'checklist'], owner: 'Silvia M.', access: 'Private', comments: 'Checklist for embassy visit' },
    { date: '15.12.24', title: 'ESTA USA 2025', tags: ['party'], owner: 'Silvia M.', access: 'Public', comments: 'Confirmed with group, ready to go!' },
    { date: '20.01.25', title: 'Packing List USA', tags: ['travel', 'essentials'], owner: 'Marco T.', access: 'Shared', comments: 'Need to buy adapter and meds' },
    { date: '05.02.25', title: 'Flight Booking Details', tags: ['logistics'], owner: 'Silvia M.', access: 'Private', comments: 'Booked via Lufthansa, ref #A1234' },
    { date: '10.03.25', title: 'Party Playlist NYC', tags: ['party', 'music'], owner: 'Lena R.', access: 'Public', comments: 'Added reggaeton and EDM tracks' }

];


export default function Dashboard() {
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
        <div className="space-y-4 bg-bg ">
            <h2 className="text-2xl font-semibold">
                {userName? `Hello, ${userName}!`:
                    `Hello Paperfriendly World!!`}
            </h2>

            <div className="grid grid-cols-2 gap-4">
                <div className="bg-green-600 border rounded h-20" />
                <div className="bg-green-900 border rounded h-20" />
            </div>
            <div className="w-full overflow-x-auto">
                <Table rows={rows} />
            </div>
        </div>
    );
}
