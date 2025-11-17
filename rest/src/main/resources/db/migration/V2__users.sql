create table if not exists users
(
    id            uuid primary key,
    username      varchar(100) not null unique,
    password_hash varchar(100) not null,
    enabled       boolean      not null default true,
    created_at    timestamptz  not null default now()
);

create table if not exists roles
(
    id   uuid primary key,
    name varchar(50) not null unique
);

create table if not exists users_roles
(
    user_id uuid not null references users (id) on delete cascade,
    role_id uuid not null references roles (id) on delete cascade,
    primary key (user_id, role_id)
);
