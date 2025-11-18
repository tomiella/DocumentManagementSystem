import Table from "../components/Table";
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

  useEffect(() => {
    const ac = new AbortController();

    const fetchRows = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await paperless.list();
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
  }, []);

  return (
    <div className="space-y-4 bg-bg">
      <h2 className="text-2xl font-semibold">
        {userName ? `Hello, ${userName}!` : `Hello Paperfriendly World!!`}
      </h2>

      <div className="grid grid-cols-2 gap-4">
        <div className="bg-green-600 border rounded h-20" />
        <div className="bg-green-900 border rounded h-20" />
      </div>

      <div className="w-full overflow-x-auto">
        {loading && (
          <div className="p-4 text-sm text-gray-500">Loading documents…</div>
        )}
        {error && <div className="p-4 text-sm text-red-600">{error}</div>}
        {!loading && !error && <Table rows={rows} />}
      </div>
    </div>
  );
}
