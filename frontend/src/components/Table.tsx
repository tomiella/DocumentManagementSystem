import { useState } from "react";

interface TableProps {
  rows: any[];
  onDownload?: (row: any) => void;
  onDelete?: (row: any) => void;
  onEdit?: (row: any) => void;
  onViewOcr?: (row: any) => void;
}

export default function Table({
  rows,
  onDownload,
  onDelete,
  onEdit,
  onViewOcr,
}: TableProps) {
  // Define which columns to show and their display names
  const columns = [
    { key: "title", label: "Title", className: "min-w-[150px]" },
    { key: "filename", label: "Filename", className: "min-w-[120px] hidden md:table-cell" },
    { key: "contentType", label: "Type", className: "min-w-[100px] hidden lg:table-cell" },
    { key: "size", label: "Size", className: "hidden lg:table-cell" },
    { key: "updatedAt", label: "Updated", className: "min-w-[100px] hidden sm:table-cell" },
    { key: "summary", label: "Summary", className: "min-w-[150px] hidden md:table-cell" },
    { key: "ocrText", label: "OCR Text", className: "min-w-[200px]" },
  ];

  const formatValue = (key: string, value: any) => {
    if (value === null || value === undefined || value === "") {
      return <span className="text-gray-500 italic text-xs">N/A</span>;
    }

    // Format size
    if (key === "size" && typeof value === "number") {
      if (value < 1024) return `${value} B`;
      if (value < 1024 * 1024) return `${(value / 1024).toFixed(1)} KB`;
      return `${(value / (1024 * 1024)).toFixed(1)} MB`;
    }

    // Format date
    if (key === "updatedAt") {
      try {
        const date = new Date(value);
        return date.toLocaleDateString();
      } catch {
        return String(value);
      }
    }

    // Truncate long text fields
    if (key === "summary" || key === "ocrText") {
      const text = String(value);
      if (!text) {
        return (
          <span className="text-gray-500 italic text-xs">
            {key === "ocrText" ? "No OCR text" : "No summary"}
          </span>
        );
      }
      return (
        <div className="max-w-xs">
          <div className="line-clamp-2 text-xs" title={text}>
            {text}
          </div>
        </div>
      );
    }

    return String(value);
  };

  if (rows.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No documents found
      </div>
    );
  }

  return (
    <div className="overflow-x-auto rounded-lg border border-gray-700 bg-gray-900">
      <table className="min-w-full text-sm">
        <thead className="bg-gray-800 text-left sticky top-0">
          <tr>
            {columns.map((col) => (
              <th
                key={col.key}
                className={`px-4 py-3 font-medium text-gray-200 border-b border-gray-700 ${col.className}`}
              >
                {col.label}
              </th>
            ))}
            {(onDownload || onDelete || onEdit || onViewOcr) && (
              <th className="px-4 py-3 font-medium text-gray-200 border-b border-gray-700 sticky right-0 bg-gray-800">
                Actions
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr
              key={row.id || i}
              className="hover:bg-gray-800 transition-colors border-b border-gray-800 last:border-0"
            >
              {columns.map((col) => (
                <td
                  key={col.key}
                  className={`px-4 py-3 text-gray-300 ${col.className}`}
                >
                  {formatValue(col.key, row[col.key])}
                </td>
              ))}
              {(onDownload || onDelete || onEdit || onViewOcr) && (
                <td className="px-4 py-3 sticky right-0 bg-gray-900">
                  <div className="flex gap-2 justify-end">
                    {row.ocrText && onViewOcr && (
                      <button
                        onClick={() => onViewOcr(row)}
                        className="p-1.5 text-purple-400 hover:text-purple-300 hover:bg-gray-800 rounded transition-colors"
                        title="View OCR Text"
                      >
                        👁️
                      </button>
                    )}
                    {onEdit && (
                      <button
                        onClick={() => onEdit(row)}
                        className="p-1.5 text-yellow-400 hover:text-yellow-300 hover:bg-gray-800 rounded transition-colors"
                        title="Edit"
                      >
                        ✏️
                      </button>
                    )}
                    {onDownload && (
                      <button
                        onClick={() => onDownload(row)}
                        className="p-1.5 text-blue-400 hover:text-blue-300 hover:bg-gray-800 rounded transition-colors"
                        title="Download"
                      >
                        ⬇️
                      </button>
                    )}
                    {onDelete && (
                      <button
                        onClick={() => onDelete(row)}
                        className="p-1.5 text-red-400 hover:text-red-300 hover:bg-gray-800 rounded transition-colors"
                        title="Delete"
                      >
                        🗑️
                      </button>
                    )}
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
