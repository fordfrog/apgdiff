
CREATE TABLE IF NOT EXISTS parenttable2 (
	id bigserial NOT NULL
);

ALTER TABLE parenttable2 OWNER TO fordfrog;

ALTER TABLE parenttable
	DROP COLUMN IF EXISTS id,
	ADD COLUMN IF NOT EXISTS field3 information_schema.cardinal_number;

ALTER TABLE testtable
	NO INHERIT parenttable;

ALTER TABLE testtable
	INHERIT parenttable2;
