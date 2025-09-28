SET
search_path = user_schema;

-- functions:
--
CREATE
OR REPLACE FUNCTION prevent_update_and_delete_fn()
RETURNS trigger AS $$
BEGIN
  RAISE
EXCEPTION 'Modifying or deleting from this table is forbidden';
END;
$$
LANGUAGE plpgsql;

--
CREATE
OR REPLACE FUNCTION updated_at_prevent_update_fn()
RETURNS trigger AS $$
BEGIN
  IF
NEW.updated_at IS DISTINCT FROM OLD.updated_at THEN
    RAISE EXCEPTION 'Cannot modify column ''updated_at'' on this table';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

--
CREATE
OR REPLACE FUNCTION users_create_at_no_update_fn()
RETURNS trigger AS $$
BEGIN
  IF
NEW.created_at IS DISTINCT FROM OLD.created_at THEN
    RAISE EXCEPTION 'Cannot modify column ''create_at'' on ''users'' table';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

--
CREATE
OR REPLACE FUNCTION users_update_at_update_fn()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at
:= now();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;



-- tables:
CREATE TABLE users
(
    user_id      UUID PRIMARY KEY,
    name         VARCHAR(30)  NOT NULL,
    surname      VARCHAR(30)  NOT NULL,
    nickname     VARCHAR(50)  NOT NULL
        CONSTRAINT unique_nickname UNIQUE,
    phone_number VARCHAR(30)  NOT NULL
        CONSTRAINT unique_phone_number UNIQUE,
    email        VARCHAR(254) NOT NULL
        CONSTRAINT unique_email UNIQUE,
    password     VARCHAR(60)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);


-- triggers:
--
CREATE TRIGGER users_create_at_no_update_trg
    BEFORE UPDATE
    ON users
    FOR EACH ROW EXECUTE FUNCTION users_create_at_no_update_fn();

--
CREATE TRIGGER users_update_at_update_trg
    BEFORE UPDATE
    ON users
    FOR EACH ROW EXECUTE FUNCTION users_update_at_update_fn();
