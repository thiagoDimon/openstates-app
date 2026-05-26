create table politician (
    id              varchar(255) primary key,
    name            varchar(255) not null,
    given_name      varchar(255),
    family_name     varchar(255),
    party           varchar(255),
    image_url       varchar(500),
    email           varchar(255),
    gender          varchar(50),
    birth_date      varchar(20),
    openstates_url  varchar(500),
    extra_data      jsonb,
    created_at      timestamp default now(),
    updated_at      timestamp default now()
);

create table politician_role (
    id                  uuid default gen_random_uuid() primary key,
    politician_id       varchar(255) not null,
    title               varchar(255) not null,
    org_classification  varchar(50),
    district            varchar(100),
    jurisdiction_name   varchar(255),
    jurisdiction_id     varchar(255),
    state_code          varchar(2),
    constraint fk_politician_role_politician foreign key (politician_id) references politician(id) on delete cascade
);

create index idx_politician_role_politician_id on politician_role(politician_id);
create index idx_politician_role_state_code on politician_role(state_code);

create table state_sync (
    state_code        varchar(2) primary key,
    last_page_fetched integer not null default 1,
    last_synced_at    timestamp not null
);
