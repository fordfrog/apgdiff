
CREATE TABLE parenttable2 (
	id bigserial NOT NULL
);

ALTER TABLE parenttable
	DROP COLUMN id,
	ADD COLUMN field3 information_schema.cardinal_number;

Modified INHERITS on TABLE testtable: original table uses INHERITS (parenttable) but new table uses INHERITS (parenttable2)
