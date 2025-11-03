export type DocumentRow = {
    id: string;
    date: string;
    title: string;
    tags: string[];
    owner: string;
    access: "Private" | "Public";
    comments: string[];
}

const base = (import.meta as any).env?.VITE_API_BASE_URL ?? (import.meta as any).env?.VITE_API_BASE ?? '/api';

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const url = `${base}${path}`;
  const isFormData = typeof FormData !== 'undefined' && init?.body instanceof FormData;

  // Only set JSON headers when we're not sending FormData.
  const mergedHeaders: HeadersInit | undefined = isFormData
    ? (init?.headers as HeadersInit | undefined)
    : { 'Content-Type': 'application/json', ...(init?.headers as HeadersInit | undefined) };

  const res = await fetch(url, {
    credentials: 'include', // allow cookie-based auth across the app
    ...init,
    headers: mergedHeaders,
  });

  if (!res.ok) {
    // Try to surface server error body if present
    let message = `HTTP error! status: ${res.status}`;
    try {
      const err = await res.json();
      if (err?.message) message += ` - ${err.message}`;
    } catch {}
    throw new Error(message);
  }

  if (res.status === 204) {
    // No Content
    return undefined as unknown as T;
  }

  return res.json() as Promise<T>;
}

export const PaperlessAPI = { //FLAG: Doublecheck this for ==>FIXME.
    login: (u: string, p: string) =>
      api<{ token?: string; user?: { name: string } }>(`/auth/login`, {
        method: 'POST',
        body: JSON.stringify({ username: u, password: p }),
      }),
    me: () => api<{ user: { name: string } }>(`/auth/me`),
    stats: () =>
        api<{
            savedTrees: number
            documents: number;
            characters: number;
            comments: number;
            types: number;
            tags: number;
        }
        >
        (`/stats`),
    listDocs: () =>
        api<DocumentRow[]>(`/documents`),

    upload: (form: FormData) =>
      fetch(`${base}/documents`, {
        method: 'POST',
        body: form,
        credentials: 'include',
      }).then(async (resp) => {
        if (!resp.ok) {
          let msg = 'Upload failed';
          try { const j = await resp.json();        // Tries to parse the response body as JSON to extract a more specific error message
              if (j?.message) msg += ` - ${j.message}`;    // If parsing fails, it silently ignores the error.
          } catch {}
          throw new Error(msg);
        }
        if (resp.status === 204) return null as any;
        return resp.json();
      }),
};