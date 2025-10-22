export type DocumentRow = {
    id: string;
    date: string;
    title: string;
    tags: string[];
    owner: string;
    access: "Private" | "Public";
    comments: string[];
}

const base = import.meta.env.VITE_API_BASE ?? '/api';

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(`${base}${path}`,{
        headers: { 'Content-Type': 'application/json' },
        ...init
    });
    if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
    }
    return res.json();
}

export const PaperlessAPI = { //FLAG: Doublecheck this for ==>FIXME.
    login: (u: string, p: string) => api<{ token: string }>(`/auth/login`, { method: 'POST', body: JSON.stringify({ u, p }) }),
    // me: () => api<{ user: { name: string } }>(`/auth/me`),
    stats: () => api<{ documents: number; types: number; tags: number; unscanned: number; characters: number; savedTrees: number }>(`/stats`),
    listDocs: () => api<DocumentRow[]>(`/documents`),
    upload: (form: FormData) => fetch(`${base}/documents`, { method: 'POST', body: form }).then(r => { if(!r.ok) throw new Error('Upload failed'); return r.json(); }),
};
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/