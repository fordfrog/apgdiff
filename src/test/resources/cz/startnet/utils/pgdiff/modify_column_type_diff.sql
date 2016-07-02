
ALTER TABLE testtable
	ALTER COLUMN field1 TYPE integer USING field1::integer /* TYPE change - table: testtable original: smallint new: integer */,
	ALTER COLUMN field3 TYPE character varying(150) USING field3::character varying(150) /* TYPE change - table: testtable original: character varying(100) new: character varying(150) */;
