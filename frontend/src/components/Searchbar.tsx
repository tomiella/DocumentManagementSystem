// placement: B - Topbar (column 2, row 1)
import { useState } from "react";

export default function Searchbar() {
  const [q, setQ] = useState("");

  function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    // TODO: api.ts -> searchDocuments({ query: q })
    console.log("search:", q);
  }

  return (
    <form className="flex items-center gap-2" onSubmit={onSubmit}>
      <input
        className="border rounded px-3 py-2 text-sm w-64"
        placeholder="Search"
        value={q}
        onChange={(e) => setQ(e.target.value)}
      />
      <button className="border rounded px-3 py-2 text-sm" type="submit">
        Search
      </button>
    </form>
  );
}

