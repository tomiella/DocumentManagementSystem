import { useState, useEffect } from "react";
import { DocumentDto } from "../models/DocumentDto";

interface EditModalProps {
    document: DocumentDto | null;
    onClose: () => void;
    onSave: (id: string, data: { title: string; summary: string }) => void;
}

export default function EditModal({ document, onClose, onSave }: EditModalProps) {
    const [title, setTitle] = useState("");
    const [summary, setSummary] = useState("");

    useEffect(() => {
        if (document) {
            setTitle(document.title || "");
            setSummary(document.summary || "");
        }
    }, [document]);

    if (!document) return null;

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSave(document.id, { title, summary });
    };

    return (
        <div
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
            onClick={onClose}
        >
            <div
                className="bg-gray-900 border border-gray-700 rounded-lg p-6 max-w-md w-full mx-4"
                onClick={(e) => e.stopPropagation()}
            >
                <h3 className="text-xl font-semibold mb-4 text-gray-100">
                    Edit Document
                </h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-300 mb-1">
                            Title
                        </label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            className="w-full bg-gray-800 border border-gray-600 rounded px-3 py-2 text-gray-100"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-300 mb-1">
                            Summary
                        </label>
                        <textarea
                            value={summary}
                            onChange={(e) => setSummary(e.target.value)}
                            className="w-full bg-gray-800 border border-gray-600 rounded px-3 py-2 text-gray-100"
                            rows={4}
                        />
                    </div>
                    <div className="flex gap-2 justify-end">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-gray-600 rounded text-gray-300 hover:bg-gray-800"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                        >
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
