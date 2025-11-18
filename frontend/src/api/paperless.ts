//frontend/src/api/paperless.ts
import { http } from "./http";
import type { DocumentDto } from "../models/DocumentDto";

export type UploadParams = {
  title: string;
  summary?: string;
  file: File;
  fileType?: string;
  contentType?: string;
  ocr?: boolean;
  aiSummary?: boolean;
  publicAccess?: boolean;
};

export type CreateParams = {
  title: string;
  summary: string;
  filename: string;
  size: number;
  contentType: string;
};

export const paperless = {
  async create(params: CreateParams): Promise<DocumentDto> {
    // This will resolve to `${VITE_API_BASE || '/api'}/documents/upload`
    return http<DocumentDto>("/documents", {
      method: "POST",
      body: JSON.stringify(params),
    });
  },

  async upload(params: UploadParams): Promise<DocumentDto> {
    const form = new FormData();
    form.set("title", params.title);

    if (params.summary) form.set("summary", params.summary);
    if (params.fileType) form.set("fileType", params.fileType);
    if (params.contentType) form.set("contentType", params.contentType);
    if (params.ocr) form.set("ocr", "true");
    if (params.aiSummary) form.set("aiSummary", "true");
    if (params.publicAccess) form.set("publicAccess", "true");
    form.set("file", params.file);

    // This will resolve to `${VITE_API_BASE || '/api'}/documents/upload`
    return http<DocumentDto>("/documents", {
      method: "POST",
      body: form,
    });
  },

  async get(id: string): Promise<DocumentDto> {
    return http<DocumentDto>(`/documents/${id}`);
  },

  async list(): Promise<DocumentDto[]> {
    // const q = title ? `?title=${encodeURIComponent(title)}` : "";
    // return http<DocumentDto[]>(`/documents${q}`);
    return http<DocumentDto[]>(`/documents`);
  },

  downloadURL(id: string): string {
    const base = "http://localhost:8080";
    return `${base}/documents/${id}/file`;
  },
};

