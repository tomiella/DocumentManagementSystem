//frontend/src/api/http.ts
export class HttpError extends Error {
  status: number;
  body?: any;

  constructor(status: number, message: string, body?: any) {
    super(message);
    this.status = status;
    this.body = body;
  }
}

const BASE = "http://localhost:8080";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

/**
 * Generic HTTP helper.
 *
 * Usage:
 *  - http<T>('/documents');                         // GET
 *  - http<T>('/documents', 'POST', payload);        // POST JSON
 *  - http<T>('/upload', { method: 'POST', body });  // Full RequestInit (supports FormData)
 */
export async function http<T>(
  path: string,
  methodOrInit?:
    | HttpMethod
    | RequestInit /*Question: Why is this optional? and why is it not a union type? and why req.init?*/,
  body?: unknown,
): Promise<T> {
  const url = `${BASE}${path}`;

  let init: RequestInit;

  if (typeof methodOrInit === "string") {
    // Signature: http(path, 'POST', body)
    init = {
      method: methodOrInit,
      body: body != null ? JSON.stringify(body) : undefined,
    };
  } else {
    // Signature: http(path, init?)
    init = methodOrInit ?? {};
  }

  const isFormData = init.body instanceof FormData;

  const headers = new Headers(init.headers || undefined);
  headers.set(
    "Authorization",
    `Bearer ${sessionStorage.getItem("token") || ""}`,
  );

  // Only set JSON content-type if we are not sending FormData and there's a body
  if (!isFormData && init.body != null && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(url, {
    ...init,
    method: init.method ?? "GET",
    headers,
  });

  const contentType = response.headers.get("Content-Type") || "";
  const isJson = contentType.includes("application/json");

  const data = isJson
    ? await response.json().catch(() => undefined)
    : undefined;

  if (!response.ok) {
    throw new HttpError(
      response.status,
      (data as any)?.title ?? (data as any)?.message ?? response.statusText,
      data,
    );
  }

  return data as T;
}

