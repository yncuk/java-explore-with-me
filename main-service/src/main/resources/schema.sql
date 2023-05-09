create table if not exists users
(
    id    serial
        primary key,
    email varchar not null,
    name  varchar not null
        unique
);
create table if not exists categories
(
    id   serial
        primary key,
    name varchar
        unique
);
create table if not exists locations
(
    id serial
        primary key,
    lat   real not null,
    lon   real not null
);
create table if not exists events
(
    id                 serial
        primary key,
    annotation         varchar,
    category_id        integer
        constraint events_categories_id_fk
            references categories,
    confirmed_requests integer,
    created_on         varchar,
    description        varchar,
    event_date         timestamp,
    initiator_id       integer
        constraint events_users_id_fk
            references users,
    location_id        integer
        constraint events_locations_id_fk
            references locations,
    paid               boolean,
    participant_limit  integer default 0,
    published_on       varchar,
    request_moderation boolean,
    state              varchar,
    title              varchar,
    views              integer,
    comments           integer
);
create table if not exists requests
(
    created   varchar,
    event     integer
        constraint requests_events_id_fk
            references events,
    id        serial
        primary key,
    requester integer
        constraint requests_users_id_fk
            references users,
    status    varchar
);
create table if not exists compilations
(
    id     serial
        primary key,
    pinned boolean,
    title  varchar
);
create table if not exists compilations_events
(
    compilation_id integer
        constraint compilations_events_compilations_id_fk
            references compilations,
    event_id       integer
        constraint compilations_events_events_id_fk
            references events
);
create table if not exists comments
(
    id        serial
        primary key,
    text      varchar not null,
    event_id  integer
        constraint comments_events_id_fk
            references events,
    author_id integer
        constraint comments_users_id_fk
            references users,
    created   timestamp,
    status    varchar,
    updated   timestamp
);