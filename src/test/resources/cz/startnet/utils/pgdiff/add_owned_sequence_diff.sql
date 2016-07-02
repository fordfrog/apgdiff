
CREATE SEQUENCE IF NOT EXISTS table2_col1_seq
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;

CREATE TABLE IF NOT EXISTS table2 (
	col1 integer DEFAULT nextval('table2_col1_seq'::regclass) NOT NULL
);

ALTER TABLE table2 OWNER TO fordfrog;

ALTER SEQUENCE table2_col1_seq
	OWNED BY table2.col1;
