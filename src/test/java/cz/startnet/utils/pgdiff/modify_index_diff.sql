
DROP INDEX testindex;

CREATE INDEX testindex ON testtable USING btree (field3);
