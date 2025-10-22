import StatsPanel from '../components/StatsPanel';
import Table from '../components/Table';


const rows = [
    { date:'28.09.24', title:'Hematologie', tags:['scanned','info'], owner:'Max M.', access:'Private', comments:'…' },
    { date:'15.12.24', title:'ESTA USA 2025', tags:['party'], owner:'Silvia M.', access:'Public', comments:'…' },
];


export default function Dashboard() {
    return (
        <div className="grid grid-cols-[1fr_256px] gap-6">
            <div className="space-y-4">
                <h2 className="text-2xl font-semibold">Hello, Max!</h2>
                <div className="grid grid-cols-2 gap-4">
                    <div className="bg-white border rounded h-48" />
                    <div className="bg-white border rounded h-48" />
                </div>
                <Table rows={rows} />
            </div>
            <StatsPanel />
        </div>
    );
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/