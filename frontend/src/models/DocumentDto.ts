export type DocumentDto = {
    id: string;
    title: string;
    filename: string;
    contentType: string;
    size: number;
    updatedAt: string
    summary?: string | null;
}
