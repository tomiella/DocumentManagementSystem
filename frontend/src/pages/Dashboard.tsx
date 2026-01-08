import Table from "../components/Table";
import EditModal from "../components/EditModal";
import OcrTextModal from "../components/OcrTextModal";
import { useEffect, useState } from "react";
import { DocumentDto } from "../models/DocumentDto";
import { paperless } from "../api/paperless";

export default function Dashboard() {
  const [userName, setUserName] = useState<string | null>(() => {
    if (typeof window === "undefined") return null;
    return sessionStorage.getItem("userName");
  });

  const [rows, setRows] = useState<DocumentDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [editingDocument, setEditingDocument] = useState<DocumentDto | null>(null);
  const [viewingOcrDocument, setViewingOcrDocument] = useState<DocumentDto | null>(null);

  useEffect(() => {
    const updateFromSession = () =>
      setUserName(sessionStorage.getItem("userName"));

    window.addEventListener("auth:changed", updateFromSession);

    const onStorage = (e: StorageEvent) => {
      if (e.storageArea === sessionStorage) updateFromSession();
    };
    window.addEventListener("storage", onStorage);

    return () => {
      window.removeEventListener("auth:changed", updateFromSession);
      window.removeEventListener("storage", onStorage);
    };
  }, []);

  const [searchQuery, setSearchQuery] = useState("");
  const [debouncedQuery, setDebouncedQuery] = useState(searchQuery);

  useEffect(() => {
    const handler = setTimeout(() => setDebouncedQuery(searchQuery), 500);
    return () => clearTimeout(handler);
  }, [searchQuery]);

  useEffect(() => {
    const ac = new AbortController();

    const fetchRows = async () => {
      try {
        setLoading(true);
        setError(null);
        let data;
        if (debouncedQuery.trim()) {
          data = await paperless.search(debouncedQuery);
        } else {
          data = await paperless.list();
        }
        if (!ac.signal.aborted) setRows(data);
      } catch (e: any) {
        if (!ac.signal.aborted)
          setError(e?.message ?? "Failed to load documents");
      } finally {
        if (!ac.signal.aborted) setLoading(false);
      }
    };

    fetchRows();
    return () => ac.abort();
  }, [debouncedQuery]);

  const handleDownload = async (doc: DocumentDto) => {
    try {
      const url = paperless.downloadURL(doc.id);
      const token = sessionStorage.getItem("token");

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token || ""}`,
        },
      });

      if (!response.ok) {
        throw new Error(`Download failed: ${response.statusText}`);
      }

      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = downloadUrl;
      a.download = doc.filename || doc.title || "document";
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(downloadUrl);
    } catch (e: any) {
      alert(`Failed to download document: ${e?.message ?? "Unknown error"}`);
    }
  };

  const handleDelete = async (doc: DocumentDto) => {
    if (!confirm(`Are you sure you want to delete "${doc.title}"?`)) {
      return;
    }

    try {
      await paperless.delete(doc.id);
      setRows((prev) => prev.filter((r) => r.id !== doc.id));
    } catch (e: any) {
      alert(`Failed to delete document: ${e?.message ?? "Unknown error"}`);
    }
  };

  const handleEdit = (doc: DocumentDto) => {
    setEditingDocument(doc);
  };

  const handleViewOcr = (doc: DocumentDto) => {
    setViewingOcrDocument(doc);
  };

  const handleSaveEdit = async (
    id: string,
    data: { title: string; summary: string }
  ) => {
    try {
      const updated = await paperless.update(id, data);
      setRows((prev) => prev.map((r) => (r.id === id ? updated : r)));
      setEditingDocument(null);
    } catch (e: any) {
      alert(`Failed to update document: ${e?.message ?? "Unknown error"}`);
    }
  };

  return (
    <div className="space-y-6 p-4 md:p-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h2 className="text-2xl md:text-3xl font-semibold text-gray-100">
          {userName ? `Hello, ${userName}!` : `Document Dashboard`}
        </h2>
        <div className="flex items-center gap-2 text-sm text-gray-400">
          <span>{rows.length} document{rows.length !== 1 ? 's' : ''}</span>
        </div>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <input
          type="text"
          placeholder="Search documents..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full bg-gray-800 border border-gray-700 rounded-lg px-4 py-2 text-gray-200 focus:outline-none focus:border-blue-500 transition-colors"
        />
        {searchQuery && (
          <button
            onClick={() => setSearchQuery("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
          >
            ✕
          </button>
        )}
      </div>

      {/* Stats Cards - Optional, can be removed if not needed */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-gradient-to-br from-blue-900 to-blue-800 border border-blue-700 rounded-lg p-4">
          <div className="text-sm text-blue-200">Total Documents</div>
          <div className="text-2xl font-bold text-white mt-1">{rows.length}</div>
        </div>
        <div className="bg-gradient-to-br from-green-900 to-green-800 border border-green-700 rounded-lg p-4">
          <div className="text-sm text-green-200">With OCR</div>
          <div className="text-2xl font-bold text-white mt-1">
            {rows.filter(r => r.ocrText).length}
          </div>
        </div>
        <div className="bg-gradient-to-br from-purple-900 to-purple-800 border border-purple-700 rounded-lg p-4 hidden sm:block">
          <div className="text-sm text-purple-200">With Summary</div>
          <div className="text-2xl font-bold text-white mt-1">
            {rows.filter(r => r.summary).length}
          </div>
        </div>
        <div className="bg-gradient-to-br from-orange-900 to-orange-800 border border-orange-700 rounded-lg p-4 hidden lg:block">
          <div className="text-sm text-orange-200">Total Size</div>
          <div className="text-2xl font-bold text-white mt-1">
            {(rows.reduce((sum, r) => sum + (r.size || 0), 0) / (1024 * 1024)).toFixed(1)} MB
          </div>
        </div>
      </div>

      {/* Documents Table */}
      <div className="w-full">
        {loading && (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
            <p className="mt-2 text-sm text-gray-400">Loading documents…</p>
          </div>
        )}
        {error && (
          <div className="bg-red-900/20 border border-red-700 rounded-lg p-4 text-red-400">
            {error}
          </div>
        )}
        {!loading && !error && (
          <Table
            rows={rows}
            onDownload={handleDownload}
            onDelete={handleDelete}
            onEdit={handleEdit}
            onViewOcr={handleViewOcr}
          />
        )}
      </div>

      {/* Modals */}
      <EditModal
        document={editingDocument}
        onClose={() => setEditingDocument(null)}
        onSave={handleSaveEdit}
      />

      <OcrTextModal
        document={viewingOcrDocument}
        onClose={() => setViewingOcrDocument(null)}
      />
    </div>
  );
}
