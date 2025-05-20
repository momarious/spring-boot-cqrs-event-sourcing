create table inbox_events (id uuid not null, created_at timestamptz not null default current_timestamp, error varchar(255), payload jsonb, processed_by varchar(255), source varchar(255), status varchar(255) not null default 'NEW', type varchar(255), updated_at timestamptz not null default current_timestamp, version integer, primary key (id));


create table inbox_unprocessed(
    id bigint primary key generated always as identity,
    message varchar not null,
    error varchar not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);


CREATE TABLE prets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id VARCHAR(255) NOT NULL,
    montant NUMERIC(19, 4) NOT NULL,
    duree_en_mois INTEGER NOT NULL,
    description TEXT,
    montant_rembourse NUMERIC(19, 4) DEFAULT 0,
    statut VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_pret_client_id ON prets(client_id);
CREATE INDEX idx_pret_statut ON prets(statut);
