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


    if (!file || file.size === 0) {
      setError("No file selected - please select a file to upload.");
      return;
    }

    if (!title) {
      setError("Please enter a title.");
      return;
    }

    const formElement = e.currentTarget;

    setSubmitting(true);
    try {
      await paperless.upload({
        title,
        file,
      });

      setSuccess("File uploaded successfully.");
      formElement.reset();

      setFileName("");
      setFile(null);
    } catch (err) {
      console.error("Upload failed:", err);
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Upload failed. Please try again.");
      }
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
