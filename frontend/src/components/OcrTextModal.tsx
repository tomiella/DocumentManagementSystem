import { DocumentDto } from "../models/DocumentDto";

interface OcrTextModalProps {
    document: DocumentDto | null;
    onClose: () => void;
}

export default function OcrTextModal({ document, onClose }: OcrTextModalProps) {
    if (!document) return null;

    return (
        <div
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
            onClick={onClose}
        >
            <div
                className="bg-gray-900 border border-gray-700 rounded-lg p-6 max-w-4xl w-full max-h-[90vh] flex flex-col"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="flex justify-between items-start mb-4">
                    <div>
                        <h3 className="text-xl font-semibold text-gray-100">
                            OCR Text
                        </h3>
                        <p className="text-sm text-gray-400 mt-1">{document.title}</p>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-gray-400 hover:text-gray-200 text-2xl leading-none"
                    >
                        ×
                    </button>
                </div>

                <div className="flex-1 overflow-auto bg-gray-800 rounded p-4 border border-gray-700">
                    {document.ocrText ? (
                        <pre className="text-sm text-gray-200 whitespace-pre-wrap font-mono">
                            {document.ocrText}
                        </pre>
                    ) : (
                        <p className="text-gray-500 italic">No OCR text available for this document.</p>
                    )}
                </div>

                <div className="mt-4 flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-600"
                    >
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
}
