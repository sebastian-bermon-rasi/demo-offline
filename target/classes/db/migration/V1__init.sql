create extension if not exists pgcrypto;

-- PACIENTES
create table if not exists patients (
    id_global uuid primary key default gen_random_uuid(),
    document_number varchar(50) not null,
    name varchar(150) not null,
    birth_date date,
    updated_at timestamptz not null default now(),
    updated_by varchar(80),
    version int not null default 0,
    branch_id varchar(32) not null,
    deleted boolean not null default false
    );
create unique index if not exists ux_patients_doc
    on patients(document_number) where deleted = false;

-- OUTBOX (sede)
create table if not exists outbox (
    id uuid primary key default gen_random_uuid(),           -- id del evento
    aggregate_type varchar(64) not null,                     -- 'PATIENT'
    aggregate_id uuid not null,                              -- id_global del paciente
    op varchar(16) not null,                                 -- 'UPSERT' | 'DELETE'
    payload jsonb,                                           -- estado completo
    branch_id varchar(32) not null,
    occurred_at timestamptz not null default now(),
    status varchar(16) not null default 'PENDING',           -- PENDING|ACK
    attempts int not null default 0
    );
create index if not exists idx_outbox_status on outbox(status, occurred_at);

-- INBOX (central)
create table if not exists inbox (
    id uuid primary key,                                     -- id del evento (outbox.id)
    branch_id varchar(32) not null,
    received_at timestamptz not null default now(),
    processed boolean not null default false,
    error text
    );
