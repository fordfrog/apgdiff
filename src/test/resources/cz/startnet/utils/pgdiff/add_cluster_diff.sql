
CREATE SEQUENCE IF NOT EXISTS testtable2_id_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE IF NOT EXISTS testtable2 (
	id integer DEFAULT nextval('testtable2_id_seq'::regclass) NOT NULL,
	col1 boolean NOT NULL
);

ALTER TABLE testtable2 OWNER TO fordfrog;

ALTER SEQUENCE testtable2_id_seq
	OWNED BY testtable2.id;

CREATE INDEX IF NOT EXISTS testindex ON testtable USING btree (field1);

CREATE INDEX IF NOT EXISTS testtable2_col1 ON testtable2 USING btree (col1);

ALTER TABLE testtable CLUSTER ON testindex;

ALTER TABLE testtable2 CLUSTER ON testtable2_col1;
