
CREATE OR REPLACE FUNCTION public.function2() RETURNS integer
    LANGUAGE sql
    AS $$ select 2; $$;

CREATE VIEW public.view2 AS
	SELECT table1.col1,
    table1.col2
   FROM schema1.table1
  WHERE (table1.col2 < 10);

ALTER SEQUENCE schema1.table1_col1_seq
	INCREMENT BY 5;

ALTER TABLE schema1.table1 OWNER TO postgres;

ALTER TABLE schema2.table2
	DROP CONSTRAINT table2_col2_fkey;

DROP INDEX schema2.index2;

ALTER TABLE schema2.table2
	ADD COLUMN added character varying;

ALTER TABLE schema2.table2 OWNER TO postgres;

ALTER TABLE schema2.table2
	ADD CONSTRAINT table1_col2_key UNIQUE (col2);

ALTER TABLE schema2.table2
	ADD CONSTRAINT table2_col2_fkey FOREIGN KEY (col2) REFERENCES schema3.table3(col2);

DROP FUNCTION schema3.function3();

ALTER TABLE schema3.table3
	DROP CONSTRAINT table3_pkey;

DROP INDEX schema3.index3;

ALTER TABLE schema3.table3
	ADD CONSTRAINT table3_pkey_new PRIMARY KEY (col1);

ALTER TABLE schema3.table3
	ADD CONSTRAINT table3_col2_key UNIQUE (col2);

CREATE INDEX index3 ON schema3.table3 USING btree (col2 DESC);

CREATE TRIGGER trigger3
	AFTER INSERT ON schema3.table3
	FOR EACH STATEMENT
	EXECUTE PROCEDURE triggfunc();
