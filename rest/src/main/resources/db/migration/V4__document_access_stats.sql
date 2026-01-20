-- V4: Add document access statistics table
CREATE TABLE IF NOT EXISTS document_access_stats (
    id            SERIAL PRIMARY KEY,
    document_id   UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    access_date   DATE NOT NULL,
    access_count  INT NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_doc_date UNIQUE (document_id, access_date)
);

CREATE INDEX IF NOT EXISTS ix_access_stats_doc ON document_access_stats(document_id);
CREATE INDEX IF NOT EXISTS ix_access_stats_date ON document_access_stats(access_date);
