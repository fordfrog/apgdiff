
CREATE TABLE parenttable2 (
	id bigserial NOT NULL
);

ALTER TABLE parenttable
	RENAME COLUMN id TO field3 /* RENAME column - table: parenttable original: id new: field3 */,
	ALTER COLUMN field3 TYPE information_schema.cardinal_number /* TYPE change - table: parenttable original: bigserial new: information_schema.cardinal_number */,
	ALTER COLUMN field3 DROP NOT NULL;

ALTER TABLE testtable
	NO INHERIT parenttable;

ALTER TABLE testtable
	INHERIT parenttable2;
