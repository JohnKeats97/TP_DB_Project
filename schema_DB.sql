
CREATE EXTENSION IF NOT EXISTS citext;
_________________________________

DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  about    TEXT DEFAULT NULL,
  email    citext UNIQUE,
  fullname TEXT DEFAULT NULL,
  nickname citext COLLATE ucs_basic UNIQUE
);
_________________________________

DROP TABLE IF EXISTS forums CASCADE;

CREATE TABLE IF NOT EXISTS forums (
  "user"  citext REFERENCES users (nickname) ON DELETE CASCADE  NOT NULL,
  posts   INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0,
  slug    citext UNIQUE                                         NOT NULL,
  title   TEXT                                                  NOT NULL
);
_________________________________

DROP TABLE IF EXISTS threads CASCADE;

CREATE TABLE IF NOT EXISTS threads (
  author  citext REFERENCES users (nickname) ON DELETE CASCADE  NOT NULL,
  created TIMESTAMPTZ DEFAULT NOW(),
  forum   citext REFERENCES forums (slug) ON DELETE CASCADE     NOT NULL,
  id      SERIAL PRIMARY KEY,
  message TEXT        DEFAULT NULL,
  slug    citext UNIQUE,
  title   TEXT                                                  NOT NULL,
  votes   INTEGER     DEFAULT 0
);
_________________________________

DROP TABLE IF EXISTS votes CASCADE;

CREATE TABLE IF NOT EXISTS votes (
  nickname citext REFERENCES users (nickname) ON DELETE CASCADE,
  thread   INTEGER REFERENCES threads (id) ON DELETE CASCADE,
  voice    INTEGER DEFAULT 0,
  CONSTRAINT unique_pair UNIQUE (nickname, thread)
);
_________________________________

DROP TABLE IF EXISTS forum_users CASCADE;

CREATE TABLE IF NOT EXISTS forum_users (
  user_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
  forum   citext REFERENCES forums (slug) ON DELETE CASCADE
);
_________________________________

DROP TABLE IF EXISTS posts CASCADE;

CREATE TABLE IF NOT EXISTS posts (
  author   citext REFERENCES users (nickname) ON DELETE CASCADE      NOT NULL,
  created  TIMESTAMPTZ DEFAULT NOW(),
  forum    citext REFERENCES forums (slug) ON DELETE CASCADE         NOT NULL,
  id       SERIAL PRIMARY KEY,
  isEdited BOOLEAN     DEFAULT FALSE,
  message  TEXT        DEFAULT NULL,
  parent   INTEGER     DEFAULT 0,
  thread   INTEGER REFERENCES threads (id) ON DELETE CASCADE         NOT NULL,
  path     INTEGER [],
  root_id  INTEGER
);
_________________________________

