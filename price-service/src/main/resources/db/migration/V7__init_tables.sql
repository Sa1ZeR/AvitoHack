    create table category (
        id bigserial not null,
        parent_id bigint,
        name varchar(64) not null,
        primary key (id)
    );

    create table discount_segments (
        id bigserial not null,
        segment bigint not null,
        user_id bigint not null,
        primary key (id)
    );

    create table locations (
        id bigserial not null,
        parent_id bigint,
        name varchar(255) not null,
        primary key (id)
    );

    create table matrix (
        id bigserial not null,
        name varchar(18) not null unique,
        primary key (id)
    );

    create table storage (
        id bigserial not null,
        file TEXT not null,
        primary key (id)
    );

    create table users (
        id bigserial not null,
        name varchar(32) not null,
        primary key (id)
    );

    alter table if exists category 
       add constraint FK2y94svpmqttx80mshyny85wqr 
       foreign key (parent_id) 
       references category;

    alter table if exists discount_segments 
       add constraint FKk6gqn2ii1fmylmfea5dw20e4j 
       foreign key (user_id) 
       references users;

    alter table if exists locations 
       add constraint FKhjdkpuoptx1cd04r3atchkpi0 
       foreign key (parent_id) 
       references locations;