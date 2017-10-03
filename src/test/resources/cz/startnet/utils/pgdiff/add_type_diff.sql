CREATE TYPE bug_status AS ENUM (
	'new',
	'open',
	'closed'
);

CREATE TYPE descr_type AS (
	name text,
	amount integer
);

CREATE TABLE IF NOT EXISTS t1 (
	id integer,
	descr descr_type
);

ALTER TABLE t1 OWNER TO dv;
