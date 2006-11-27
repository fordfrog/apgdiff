
CREATE INDEX testindex ON testtable USING btree (field1);

ALTER TABLE testtable CLUSTER ON testindex;
