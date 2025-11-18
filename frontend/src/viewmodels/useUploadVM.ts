// frontend/src/viewmodels/useUploadVM.ts

import { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { paperless } from "../api/paperless";

export function useUploadVM() {
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [fileName, setFileName] = useState<string>("");
    const [file, setFile] = useState<File | null>(null);

    /*Flag: incoming File*/
    /*Question: why use a state variable for the file instead of the file input itself?/why not from form:
      Answer: file state , for from form.get("file") because....*/
    function onFileChange(e: ChangeEvent<HTMLInputElement>) {
        const f = e.target.files?.[0] ?? null;
        setFile(f);
        setFileName(f ? f.name : "");
    }

    async function onSubmit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        const form = new FormData(e.currentTarget);

        const title = String(form.get("title") || "").trim();
        const summaryRaw = String(form.get("summary") || "").trim();
        const summary = summaryRaw || undefined;

        /*Question: Ommit choices for fileType and contentType?*/
        const fileTypeRaw = String(form.get("fileType") || "").trim();
        const contentTypeRaw = String(form.get("contentType") || "").trim();

        const fileType = fileTypeRaw || undefined;
        const contentType = contentTypeRaw || undefined;

        /*Flag: Opration for OCR, AI Summary  and Public Access*/
        const ocr = form.get("ocr") === "true";
        const aiSummary = form.get("aiSummary") === "true";
        const publicAccess = form.get("publicAccess") === "true";

        if (!file || file.size === 0) {
            setError("No file selected - please select a file to upload.");
            return;
        }

        if (!title) {
            setError("Please enter a title.");
            return;
        }

        /*Info: Submitting the form calls the upload function*/
        setSubmitting(true);
        try {
            await paperless.upload({
                title,
                summary,
                file,
                fileType,
                contentType,
                ocr,
                aiSummary,
                publicAccess,
            });

            setSuccess("File uploaded successfully.");
            e.currentTarget.reset();

            setFileName("");
            setFile(null);
        } catch (err) {
            console.error("Upload failed:", err);
            setError("Upload failed. Please try again.");
        } finally {
            setSubmitting(false);
        }
    }

    return {
        submitting,
        error,
        success,
        fileName,
        onFileChange,
        onSubmit,
    };
}
