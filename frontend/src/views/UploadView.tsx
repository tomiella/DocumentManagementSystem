// frontend/src/views/UploadView.tsx
import React from "react";
import { useUploadVM } from "../viewmodels/useUploadVM";

export default function UploadView() {
    const { submitting, error, success, fileName, onFileChange, onSubmit } = useUploadVM();

    return (
        <div className="space-y-4">
            <div className="flex items-center gap-3 pb-2">
                <button className="bg-btn1 rounded px-3 py-1 border text-text">Upload</button>
                <button className="bg-btn1 rounded px-3 py-1 border">Documents</button>
                <button className="bg-btn1 rounded px-3 py-1 border" disabled>
                    Disabled
                </button>
            </div>

            <h2 className="text-lg font-medium">Upload Document</h2>

            <form
                onSubmit={onSubmit}
                noValidate
                className="bg-bg border rounded p-4 space-y-3 max-w-3xl"
            >
                {/* Row: file + actions */}
                <div className="flex gap-2">
                    <input
                        className="border rounded px-3 py-2 flex-1"
                        placeholder="Choose file"
                        readOnly
                        value={fileName}
                    />
                    <input
                        id="file"
                        name="file"
                        type="file"
                        className="hidden"
                        onChange={onFileChange}
                    />
                    <label
                        htmlFor="file"
                        className="bg-btn2 border rounded px-3 py-2 cursor-pointer inline-flex items-center"
                    >
                        Browse
                    </label>

                    <button
                        className="bg-btn2 border rounded px-3 py-2"
                        type="submit"
                        disabled={submitting}
                    >
                        {submitting ? "Uploading…" : "Upload"}
                    </button>
                </div>

                {/* Metadata fields */}
                <div className="grid grid-cols-2 gap-3">
                    <input
                        className="bg-bg border rounded px-3 py-2 col-span-2"
                        name="title"
                        placeholder="Title"
                    />
                    <textarea
                        className="bg-bg border rounded px-3 py-2 col-span-2"
                        name="summary"
                        placeholder="Summary (optional)"
                        rows={3}
                    />
                </div>

                {error && (
                    <div className="text-red-700 text-sm" role="alert" aria-live="assertive">
                        {error}
                    </div>
                )}
                {success && (
                    <div className="text-green-700 text-sm" role="status" aria-live="polite">
                        {success}
                    </div>
                )}
            </form>
        </div>
    );
}