CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS snapshots CASCADE;
DROP TABLE IF EXISTS outbox_events CASCADE;


CREATE TABLE IF NOT EXISTS events
(
    event_id       UUID                     DEFAULT uuid_generate_v4(),
    aggregate_id   VARCHAR(250) NOT NULL CHECK ( aggregate_id <> '' ),
    aggregate_type VARCHAR(250) NOT NULL CHECK ( aggregate_type <> '' ),
    event_type     VARCHAR(250) NOT NULL CHECK ( event_type <> '' ),
    data           BYTEA,
    metadata       BYTEA,
    version        bigint        NOT NULL,
    timestamp      timestamp(6) not null default now(),
                                 UNIQUE (aggregate_id, version)
    ) PARTITION BY HASH (aggregate_id);


CREATE INDEX IF NOT EXISTS aggregate_id_aggregate_version_idx ON events USING btree (aggregate_id, version ASC);

CREATE TABLE IF NOT EXISTS events_partition_hash_1 PARTITION OF events
    FOR VALUES WITH (MODULUS 3, REMAINDER 0);

CREATE TABLE IF NOT EXISTS events_partition_hash_2 PARTITION OF events
    FOR VALUES WITH (MODULUS 3, REMAINDER 1);

CREATE TABLE IF NOT EXISTS events_partition_hash_3 PARTITION OF events
    FOR VALUES WITH (MODULUS 3, REMAINDER 2);

CREATE TABLE IF NOT EXISTS snapshots
(
    snapshot_id    UUID PRIMARY KEY         DEFAULT uuid_generate_v4(),
    aggregate_id   VARCHAR(250) UNIQUE NOT NULL CHECK ( aggregate_id <> '' ),
    aggregate_type VARCHAR(250)        NOT NULL CHECK ( aggregate_type <> '' ),
    data           BYTEA,
    metadata       BYTEA,
    version        SERIAL              NOT NULL,
    timestamp      timestamp(6) not null DEFAULT now(),
                                 UNIQUE (aggregate_id)
    );

CREATE INDEX IF NOT EXISTS aggregate_id_aggregate_version_idx ON snapshots USING btree (aggregate_id, version);

create table outbox_events (id uuid PRIMARY KEY         DEFAULT uuid_generate_v4(), aggregate_id VARCHAR(250), aggregate_type varchar(255), type varchar(255) not null, payload jsonb, created_at timestamp(6) not null DEFAULT now(), version bigint not null);