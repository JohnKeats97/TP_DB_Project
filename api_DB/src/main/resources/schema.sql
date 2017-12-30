CREATE EXTENSION IF NOT EXISTS CITEXT;

DROP TABLE IF EXISTS forum_users;
DROP TABLE IF EXISTS forums;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS threads;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS votes;

DROP INDEX IF EXISTS forums_user_id;
DROP INDEX IF EXISTS forum_users_user_id;
DROP INDEX IF EXISTS forum_users_forum_id;
DROP INDEX IF EXISTS forum_users_thread_id;
DROP INDEX IF EXISTS forum_users_post_id;
DROP INDEX IF EXISTS forum_users_author;
DROP INDEX IF EXISTS forums_slug;
DROP INDEX IF EXISTS users_nickname;
DROP INDEX IF EXISTS posts_id;
DROP INDEX IF EXISTS posts_user_id;
DROP INDEX IF EXISTS posts_forum_id;
DROP INDEX IF EXISTS posts_thread_id;
DROP INDEX IF EXISTS threads_user_id;
DROP INDEX IF EXISTS threads_forum_id;
DROP INDEX IF EXISTS threads_user_id_forum_id;
DROP INDEX IF EXISTS threads_forum_id_created;
DROP INDEX IF EXISTS forums_slug_id;
DROP INDEX IF EXISTS users_nickname_id;
DROP INDEX IF EXISTS treads_slug_id;
DROP INDEX IF EXISTS post_id_path;
DROP INDEX IF EXISTS posts_path_thread_id;
DROP INDEX IF EXISTS posts_path_root_id;
DROP INDEX IF EXISTS posts_flat_idx;
DROP INDEX IF EXISTS posts_multi_idx;

DROP FUNCTION IF EXISTS thread_insert( CITEXT, TIMESTAMPTZ, CITEXT, CITEXT, CITEXT, CITEXT );
DROP FUNCTION IF EXISTS post_insert( CITEXT, TIMESTAMPTZ, INTEGER, INTEGER, CITEXT, INTEGER, INTEGER );
DROP FUNCTION IF EXISTS update_or_insert_votes( INTEGER, INTEGER, INTEGER );



CREATE TABLE IF NOT EXISTS users (
  id       SERIAL       PRIMARY KEY,
  about    CITEXT       DEFAULT NULL,
  email    CITEXT       DEFAULT NULL,
  fullname CITEXT       DEFAULT NULL,
  nickname CITEXT       COLLATE ucs_basic
);

ALTER TABLE users ADD
  CONSTRAINT users_email UNIQUE (email);

ALTER TABLE users ADD
  CONSTRAINT users_nickname UNIQUE (nickname);


CREATE TABLE IF NOT EXISTS forums (
  id          SERIAL       PRIMARY KEY,
  user_id     INTEGER      NOT NULL,
  author      CITEXT       DEFAULT NULL,
  posts       INTEGER      DEFAULT 0,
  threads     INTEGER      DEFAULT 0,
  slug        CITEXT       NOT NULL,
  title       CITEXT       NOT NULL
);

ALTER TABLE forums ADD
  CONSTRAINT forums_slug UNIQUE (slug);

CREATE TABLE IF NOT EXISTS threads (
  id       SERIAL       PRIMARY KEY,
  user_id  INTEGER      NOT NULL,
  author   CITEXT       DEFAULT NULL,
  forum_id INTEGER      NOT NULL,
  created  TIMESTAMPTZ  DEFAULT NOW(),
  message  CITEXT       DEFAULT NULL,
  slug     CITEXT       DEFAULT NULL,
  title    CITEXT       NOT NULL,
  votes    INTEGER      DEFAULT 0
);

ALTER TABLE threads ADD
  CONSTRAINT threads_slug UNIQUE (slug);

CREATE TABLE IF NOT EXISTS posts (
  id        SERIAL      PRIMARY KEY,
  user_id   INTEGER     NOT NULL,
  author    CITEXT      DEFAULT NULL,
  forum_id  INTEGER     NOT NULL,
  thread_id INTEGER     NOT NULL,
  created   TIMESTAMPTZ DEFAULT NOW(),
  is_edited BOOLEAN     DEFAULT FALSE,
  message   CITEXT      DEFAULT NULL,
  parent    INTEGER     DEFAULT 0,
  path      INTEGER []  NOT NULL,
  root_id   INTEGER     DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS forum_users (
  user_id   INTEGER     DEFAULT NULL,
  forum_id  INTEGER     DEFAULT NULL,
  thread_id INTEGER     DEFAULT NULL,
  post_id   INTEGER     DEFAULT NULL,
  author    CITEXT      DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS votes (
  user_id   INTEGER     DEFAULT NULL,
  thread_id INTEGER     DEFAULT NULL,
  voice     INTEGER     DEFAULT 0
);


CREATE INDEX IF NOT EXISTS forums_user_id
  ON forums (user_id);
CREATE INDEX IF NOT EXISTS forums_slug
  ON forums (slug);
CREATE INDEX IF NOT EXISTS users_nickname
  ON users (nickname);
CREATE INDEX IF NOT EXISTS forum_users_user_id -- fill
  ON forum_users (user_id);
CREATE INDEX IF NOT EXISTS forum_users_forum_id -- fill
  ON forum_users (forum_id);
CREATE INDEX IF NOT EXISTS forum_users_thread_id -- fill
  ON forum_users (thread_id);
CREATE INDEX IF NOT EXISTS forum_users_post_id -- fill
  ON forum_users (post_id);
CREATE INDEX IF NOT EXISTS forum_users_author
  ON forum_users (author);
CREATE INDEX IF NOT EXISTS posts_id
  ON posts (id);
CREATE INDEX IF NOT EXISTS posts_user_id
  ON posts (user_id);
CREATE INDEX IF NOT EXISTS posts_forum_id
  ON posts (forum_id);
CREATE INDEX IF NOT EXISTS posts_thread_id
  ON posts (thread_id);
CREATE INDEX IF NOT EXISTS threads_user_id
  ON threads (user_id);
CREATE INDEX IF NOT EXISTS threads_forum_id
  ON threads (forum_id);
CREATE INDEX IF NOT EXISTS threads_user_id_forum_id
  ON threads (user_id, forum_id);
CREATE INDEX IF NOT EXISTS threads_forum_id_created
  ON threads (forum_id, created);
CREATE INDEX IF NOT EXISTS forums_slug_id
  ON forums (slug, id);
CREATE INDEX IF NOT EXISTS users_nickname_id
  ON users (nickname, id);
CREATE INDEX IF NOT EXISTS treads_slug_id
  ON threads (slug, id);
CREATE INDEX IF NOT EXISTS post_id_path
  ON posts (id, path);
CREATE INDEX IF NOT EXISTS tree_sort_posts
  ON posts (thread_id, path);
CREATE INDEX IF NOT EXISTS parent_tree_sort_posts
  ON posts (root_id, path);
CREATE INDEX IF NOT EXISTS sort_flat
  ON posts (thread_id, created, id);
CREATE INDEX IF NOT EXISTS parent_tree_sort_posts_2
  ON posts (thread_id, parent, id);



CREATE OR REPLACE FUNCTION thread_insert(thread_author  CITEXT, thread_created TIMESTAMPTZ, forum_slug CITEXT,
                                         thread_message CITEXT, thread_slug CITEXT, thread_title CITEXT)
  RETURNS INTEGER AS '
DECLARE
  thread_id       INTEGER;
  thread_user_id  INTEGER;
  thread_forum_id INTEGER;
BEGIN
  SELECT id
  FROM users
  WHERE nickname = thread_author
  INTO thread_user_id;
  --
  SELECT id
  FROM forums
  WHERE slug = forum_slug
  INTO thread_forum_id;
  --
  IF thread_created IS NULL
  THEN
    INSERT INTO threads (user_id, forum_id, message, slug, title)
    VALUES (thread_user_id, thread_forum_id, thread_message, thread_slug, thread_title)
    RETURNING id
      INTO thread_id;
  ELSE
    INSERT INTO threads (user_id, created, forum_id, message, slug, title)
    VALUES (thread_user_id, thread_created, thread_forum_id, thread_message, thread_slug, thread_title)
    RETURNING id
      INTO thread_id;
  END IF;
  --
  UPDATE forums
  SET threads = threads + 1
  WHERE id = thread_forum_id;
  --
  IF NOT EXISTS(
      SELECT *
      FROM forum_users
      WHERE forum_id = thread_forum_id AND user_id = thread_user_id)
  THEN
    INSERT INTO forum_users (user_id, forum_id, thread_id) VALUES (thread_user_id, thread_forum_id, thread_id);
  END IF;
  --
  RETURN thread_id;
END;
' LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION post_insert(post_author    CITEXT, post_created TIMESTAMPTZ, post_forum_id INTEGER,
                                       post_id        INTEGER, post_message CITEXT, post_parent INTEGER,
                                       post_thread_id INTEGER)
  RETURNS VOID AS '
DECLARE
  post_user_id INTEGER;
  mat_path     INTEGER [];
BEGIN
  SELECT id
  FROM users
  WHERE nickname = post_author
  INTO post_user_id;
  --
  SELECT path
  FROM posts
  WHERE id = post_parent
  INTO mat_path;
  --
  INSERT INTO posts (user_id, created, forum_id, id, message, parent, thread_id, path, root_id)
  VALUES (post_user_id, post_created, post_forum_id, post_id, post_message, post_parent, post_thread_id,
          array_append(mat_path, post_id), CASE WHEN post_parent = 0 THEN post_id ELSE mat_path[1] END);
  --
  IF NOT EXISTS(
      SELECT *
      FROM forum_users
      WHERE forum_id = post_forum_id AND user_id = post_user_id)
  THEN
    INSERT INTO forum_users (user_id, forum_id, thread_id, post_id) VALUES (post_user_id, post_forum_id, post_thread_id, post_id);
  END IF;
END;
' LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION update_or_insert_votes(vote_user_id INTEGER, vote_thread_it INTEGER, vote_value INTEGER)
  RETURNS VOID AS '
  DECLARE
  count INTEGER;
  BEGIN
    SELECT COUNT(*)
    FROM votes
    WHERE user_id = vote_user_id AND thread_id = vote_thread_it
    INTO count;
    IF count > 0
    THEN
      UPDATE votes
      SET voice = vote_value
      WHERE user_id = vote_user_id AND thread_id = vote_thread_it;
    ELSE
      INSERT INTO votes (user_id, thread_id, voice) VALUES (vote_user_id, vote_thread_it, vote_value);
    END IF;
    UPDATE threads
  SET votes = (SELECT SUM(voice)
               FROM votes
               WHERE thread_id = vote_thread_it)
  WHERE id = vote_thread_it;
END;
' LANGUAGE plpgsql