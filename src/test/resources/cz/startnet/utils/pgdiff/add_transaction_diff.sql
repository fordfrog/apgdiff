START TRANSACTION;

DROP SCHEMA testschema1 CASCADE;

CREATE SCHEMA testschema2;

SET search_path = public, pg_catalog;

DROP SEQUENCE testtable2_id_seq;

CREATE SEQUENCE testtable3_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

DROP TABLE testtable2;

CREATE TABLE testtable3 (
	id bigint DEFAULT nextval('testtable3_id_seq'::regclass) NOT NULL
);

SET search_path = testschema2, pg_catalog;

CREATE SEQUENCE testtable1_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE testtable1 (
	id integer DEFAULT nextval('testtable1_id_seq'::regclass) NOT NULL
);

COMMIT TRANSACTION;
