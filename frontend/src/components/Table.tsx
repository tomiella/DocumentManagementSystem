export default function Table({ rows }: { rows: any[] }) {
  return (
    <div className="overflow-auto rounded border bg-bg">
      <table className="min-w-full text-sm">
        <thead className="bg-bg text-left">
          <tr>
            {Object.keys(
              rows[0] ?? {
                id: "",
                title: "",
                filename: "",
                contentType: "",
                size: "",
                updatedAt: "",
                summary: "",
                ocrText: "",
              },
            ).map((h) => (
              <th
                key={h}
                className="px-3 py-2 font-medium text-gray-200 border-b"
              >
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr
              key={i}
              className="odd:bg-emerald-950 text-gray-100 even:bg-gray-950 text-gray-400"
            >
              {Object.entries(r).map(([key, v], j) => (
                <td key={j} className="px-3 py-2 border-b align-top">
                  {key === 'ocrText' || key === 'summary' ? (
                    <div className="max-w-md">
                      <div className="line-clamp-3 text-xs" title={String(v || '')}>
                        {v ? String(v) : (
                          <span className="text-gray-500 italic">
                            {key === 'ocrText' ? 'No OCR text yet' : 'No summary'}
                          </span>
                        )}
                      </div>
                    </div>
                  ) : (
                    Array.isArray(v) ? v.join(", ") : String(v)
                  )}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

