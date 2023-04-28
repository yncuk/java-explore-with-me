create table if not exists endpoint_hit
(
    id bigserial
        primary key,
    name      varchar,
    uri       varchar,
    user_ip   varchar,
    timestamp timestamp
);