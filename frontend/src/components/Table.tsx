export default function Table({ rows }: { rows: any[] }) {
    return (
        <div className="overflow-auto rounded border bg-white">
            <table className="min-w-full text-sm">
                <thead className="bg-gray-50 text-left">
                <tr>
                    {Object.keys(rows[0] ?? { date:'', title:'', tags:'', owner:'', access:'', comments:'' }).map(h => (
                        <th key={h} className="px-3 py-2 font-medium text-gray-600 border-b">{h}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {rows.map((r, i) => (
                    <tr key={i} className="odd:bg-white even:bg-gray-50">
                        {Object.values(r).map((v, j) => (
                            <td key={j} className="px-3 py-2 border-b align-top">{Array.isArray(v) ? v.join(', ') : String(v)}</td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/