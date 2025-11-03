import { useState } from 'react';
import { PaperlessAPI } from "../lib/api";

export default function Upload() {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [fileName, setFileName] = useState('');

  function onFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const f = e.target.files?.[0] || null;
    setFileName(f ? f.name : '');
  }

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const raw = new FormData(e.currentTarget);
    const file = raw.get('file') as File | null;

    if (!file || file.size === 0) {
      setError('No file selected - please select a file to upload.');
      return;
    }

    // Build a clean payload (normalize names and booleans)
    const data = new FormData();
    data.append('file', file);
    data.append('fileType', String(raw.get('fileType') || ''));
    data.append('title', String(raw.get('title') || ''));

    // Frontend field name is contentType; backend key is content_type
    data.append('content_type', String(raw.get('contentType') || ''));
    data.append('summary', String(raw.get('summary') || ''));

    const asBool = (name: string) => String(raw.get(name) === 'true');
    // Checkbox input names match these three keys
    data.append('ocr', asBool('ocr'));
    data.append('ai_summary', asBool('ai_summary'));
    data.append('public', asBool('public'));

    setSubmitting(true);
    try {
      await PaperlessAPI.upload(data);
      setSuccess('File uploaded successfully.');
      (e.currentTarget as HTMLFormElement).reset();
      setFileName('');
    } catch (err) {
      setError('Upload failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3 pb-2">
        <button className="bg-emerald-200 rounded px-3 py-1 border">Upload</button>
        <button className="bg-emerald-200 rounded px-3 py-1 border">Documents</button>
        <button className="bg-emerald-200 rounded px-3 py-1 border">Disabled</button>
      </div>

      <h2 className="text-lg font-medium">Upload Document</h2>

      <form onSubmit={onSubmit} noValidate className="bg-white border rounded p-4 space-y-3 max-w-3xl">
        {/* Row: file + actions + file type */}
        <div className="flex gap-2">
          <input
            className="border rounded px-3 py-2 flex-1"
            placeholder="Choose file"
            readOnly
            value={fileName}
          />
          <input id="file" name="file" type="file" className="hidden" onChange={onFileChange} />
          <label htmlFor="file" className="bg-emerald-200 border rounded px-3 py-2 cursor-pointer inline-flex items-center">
            Browse
          </label>

          <button className="bg-emerald-200 border rounded px-3 py-2" type="submit" disabled={submitting}>
            {submitting ? 'Uploading…' : 'Upload'}
          </button>

          <select className="bg-emerald-200 rounded px-3 py-2" name="fileType" defaultValue="">
            <option value="" disabled hidden>
              Select File Type
            </option>
            <option value="image">Image</option>
            <option value="pdf">PDF</option>
            <option value="unknown">Unknown</option>
          </select>
        </div>

        {/* Metadata fields */}
        <div className="grid grid-cols-2 gap-3">
          <input className="border rounded px-3 py-2 col-span-2" name="title" placeholder="Title" />
          <input
            className="border rounded px-3 py-2 col-span-2"
            name="contentType"
            placeholder="Content Type (e.g., application/pdf)"
          />
          <textarea
            className="border rounded px-3 py-2 col-span-2"
            name="summary"
            placeholder="Summary"
            rows={3}
          />
        </div>

        {/* Options */}
        <div className="flex items-center gap-6 pt-2">
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" name="ocr" value="true" /> OCR Scan
          </label>
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" name="ai_summary" value="true" /> Generate AI Summary
          </label>
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" name="public" value="true" /> Public access
          </label>
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
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/