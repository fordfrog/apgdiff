
CREATE TABLE parenttable2 (
	id bigserial NOT NULL
);

ALTER TABLE parenttable
	DROP COLUMN id,
	ADD COLUMN field3 information_schema.cardinal_number;

ALTER TABLE testtable
	NO INHERIT parenttable;

ALTER TABLE testtable
	INHERIT parenttable2;
