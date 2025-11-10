export default function Table({ rows }: { rows: any[] }) {
    return (
        <div className="overflow-auto rounded border bg-bg">
            <table className="min-w-full text-sm">
                <thead className="bg-bg text-left">
                <tr>
                    {Object.keys(rows[0] ?? { date:'', title:'', tags:'', owner:'', access:'', comments:'' }).map(h => (
                        <th key={h} className="px-3 py-2 font-medium text-gray-200 border-b">{h}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {rows.map((r, i) => (
                    <tr key={i} className="odd:bg-emerald-950 text-gray-100 even:bg-gray-950 text-gray-400">
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