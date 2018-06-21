
CREATE SCHEMA IF NOT EXISTS schema1;

SET search_path = schema1, pg_catalog;

CREATE TABLE IF NOT EXISTS childtable (
	childtable_date timestamptz NOT NULL
)
INHERITS (public.parenttable);

ALTER TABLE ONLY childtable
	ALTER COLUMN parenttable_id SET DEFAULT 0;
