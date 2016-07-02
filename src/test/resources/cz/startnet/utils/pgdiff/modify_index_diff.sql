
DROP INDEX IF EXISTS testindex;

CREATE INDEX IF NOT EXISTS testindex ON testtable USING btree (field3);
