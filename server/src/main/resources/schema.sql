DROP TABLE IF EXISTS comments, requests, bookings, items, users CASCADE;

CREATE TABLE IF NOT EXISTS users(
    id    bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(100)                                        NOT NULL,
    email VARCHAR(100) UNIQUE                                 NOT NULL);

create table if not EXISTS requests
( id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR (255),
    request_id BIGINT REFERENCES users (id) ON delete CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );

create table if not EXISTS items
( id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR (255) NOT NULL,
    description VARCHAR (1000) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT REFERENCES users (id),
    request_id BIGINT REFERENCES requests (id),
    CONSTRAINT space_name_item CHECK (name NOT LIKE ' ' and name NOT LIKE '')
    );

create table if not EXISTS bookings
( id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_booking TIMESTAMP,
    end_booking TIMESTAMP,
    item_id BIGINT REFERENCES items (id) ON delete CASCADE,
    booker_id BIGINT REFERENCES users (id) ON delete CASCADE,
    status VARCHAR (50));

create table if not EXISTS comments
( id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR (1000) NOT NULL,
    item_id BIGINT REFERENCES items (id) ON delete CASCADE,
    author_id BIGINT REFERENCES items (id) ON delete CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL);