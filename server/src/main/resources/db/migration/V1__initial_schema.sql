-- Lifestarter initial schema.
--
-- Mirrors the domain model: a registration owns its guests, contact options and activities, so
-- those are child tables with cascading deletes. Reference data (allergies, pledges, waves) and
-- the access lists (vips, bouncers) are standalone.
--
-- SQLite has no native DATE/DECIMAL types; dates are stored as ISO-8601 TEXT and prices as TEXT so
-- no precision is lost.

CREATE TABLE allergy
(
    id TEXT NOT NULL PRIMARY KEY
);

CREATE TABLE wave
(
    label    TEXT NOT NULL PRIMARY KEY,
    deadline TEXT NOT NULL
);

CREATE TABLE pledge
(
    name        TEXT    NOT NULL PRIMARY KEY,
    order_id    INTEGER NOT NULL,
    price       TEXT    NOT NULL DEFAULT '0',
    description TEXT    NOT NULL DEFAULT '',
    -- `limit` is a reserved word in SQL; 0 means "no cap".
    max_guests  INTEGER NOT NULL DEFAULT 0,
    available   INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE pledge_content
(
    pledge_name TEXT    NOT NULL REFERENCES pledge (name) ON DELETE CASCADE,
    position    INTEGER NOT NULL,
    content     TEXT    NOT NULL,
    PRIMARY KEY (pledge_name, position)
);

CREATE TABLE registration
(
    email         TEXT NOT NULL PRIMARY KEY,
    pledge_name   TEXT,
    registered_at TEXT NOT NULL
);

CREATE INDEX idx_registration_pledge_name ON registration (pledge_name);

CREATE TABLE registration_activity
(
    registration_email TEXT NOT NULL REFERENCES registration (email) ON DELETE CASCADE,
    activity           TEXT NOT NULL,
    PRIMARY KEY (registration_email, activity)
);

CREATE TABLE registration_guest
(
    registration_email TEXT    NOT NULL REFERENCES registration (email) ON DELETE CASCADE,
    position           INTEGER NOT NULL,
    first_name         TEXT    NOT NULL,
    last_name          TEXT    NOT NULL,
    role               TEXT,
    diet               TEXT,
    comment            TEXT,
    PRIMARY KEY (registration_email, position)
);

CREATE TABLE registration_guest_allergy
(
    registration_email TEXT    NOT NULL,
    guest_position     INTEGER NOT NULL,
    allergy_id         TEXT    NOT NULL,
    PRIMARY KEY (registration_email, guest_position, allergy_id),
    FOREIGN KEY (registration_email, guest_position)
        REFERENCES registration_guest (registration_email, position) ON DELETE CASCADE
);

CREATE TABLE registration_contact_option
(
    registration_email TEXT    NOT NULL REFERENCES registration (email) ON DELETE CASCADE,
    position           INTEGER NOT NULL,
    email              TEXT,
    address            TEXT,
    zip_code           TEXT,
    city               TEXT,
    country_iso3       TEXT,
    phone_number       TEXT,
    contact_method     TEXT    NOT NULL DEFAULT 'NONE',
    PRIMARY KEY (registration_email, position)
);

-- Names are stored normalised (trimmed, lower case) because look-ups are case-insensitive.
CREATE TABLE vip
(
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    PRIMARY KEY (first_name, last_name)
);

CREATE TABLE vip_role
(
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    role       TEXT NOT NULL,
    PRIMARY KEY (first_name, last_name, role),
    FOREIGN KEY (first_name, last_name) REFERENCES vip (first_name, last_name) ON DELETE CASCADE
);

CREATE TABLE bouncer
(
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    PRIMARY KEY (first_name, last_name)
);

CREATE TABLE bouncer_activity
(
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    activity   TEXT NOT NULL,
    PRIMARY KEY (first_name, last_name, activity),
    FOREIGN KEY (first_name, last_name) REFERENCES bouncer (first_name, last_name) ON DELETE CASCADE
);
