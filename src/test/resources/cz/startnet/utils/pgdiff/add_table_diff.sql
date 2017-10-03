
CREATE TABLE IF NOT EXISTS testtable2 (
	id integer,
	name character varying(100) NOT NULL
);

ALTER TABLE testtable2 OWNER TO fordfrog;
