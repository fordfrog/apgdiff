
DROP INDEX IF EXISTS testindex;

CREATE INDEX testindex ON testtable USING btree (field3);
