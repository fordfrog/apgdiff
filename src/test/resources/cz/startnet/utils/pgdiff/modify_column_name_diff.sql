ALTER TABLE testtable
	DROP COLUMN meta_title,
	RENAME COLUMN filed10 TO filed_11 /* RENAME column - table: testtable original: filed10 new: filed_11 */,
	RENAME COLUMN field1 TO field_1 /* RENAME column - table: testtable original: field1 new: field_1 */,
	RENAME COLUMN field2 TO field_21 /* RENAME column - table: testtable original: field2 new: field_21 */,
	RENAME COLUMN meta_discription TO seo_description /* RENAME column - table: testtable original: meta_discription new: seo_description */,
	RENAME COLUMN field_3 TO field3 /* RENAME column - table: testtable original: field_3 new: field3 */;