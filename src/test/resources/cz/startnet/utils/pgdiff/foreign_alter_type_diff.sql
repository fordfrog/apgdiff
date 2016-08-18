ALTER FOREIGN TABLE foreign_to_alter
	ADD COLUMN country_code character varying(5),
	ALTER COLUMN ref2 TYPE character varying(20) /* TYPE change - table: foreign_to_alter original: character varying(60) new: character varying(20) */,
	ALTER COLUMN deleted TYPE numeric(1,0) /* TYPE change - table: foreign_to_alter original: boolean new: numeric(1,0) */;