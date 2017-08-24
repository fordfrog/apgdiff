
DROP SCHEMA IF EXISTS testschema1 CASCADE;

CREATE SCHEMA IF NOT EXISTS testschema2;

SET search_path = public, pg_catalog;

DROP TABLE IF EXISTS testtable2;

DROP SEQUENCE IF EXISTS testtable2_id_seq;

CREATE SEQUENCE IF NOT EXISTS testtable3_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE IF NOT EXISTS testtable3 (
	id bigint DEFAULT nextval('testtable3_id_seq'::regclass) NOT NULL
);

ALTER TABLE testtable3 OWNER TO fordfrog;

ALTER SEQUENCE testtable3_id_seq
	OWNED BY testtable3.id;

SET search_path = testschema2, pg_catalog;

CREATE SEQUENCE IF NOT EXISTS testtable1_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE IF NOT EXISTS testtable1 (
	id integer DEFAULT nextval('testtable1_id_seq'::regclass) NOT NULL
);

ALTER TABLE testtable1 OWNER TO fordfrog;

ALTER SEQUENCE testtable1_id_seq
	OWNED BY testtable1.id;
