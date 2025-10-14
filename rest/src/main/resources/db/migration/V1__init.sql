create table if not exists documents
(
    id           uuid primary key,
    title        varchar(300) not null,
    filename     varchar(255) not null,
    content_type varchar(150) not null,
    size         bigint       not null,
    uploaded_at  timestamptz  not null,
    summary      text
);

create index if not exists ix_documents_title on documents (title);
