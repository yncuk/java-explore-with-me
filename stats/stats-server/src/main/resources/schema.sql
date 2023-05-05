create table if not exists endpoint_hit
(
    id        bigserial
    primary key,
    app       varchar,
    uri       varchar,
    ip        varchar,
    timestamp timestamp
);
create index on endpoint_hit(uri);
create index on endpoint_hit(timestamp);