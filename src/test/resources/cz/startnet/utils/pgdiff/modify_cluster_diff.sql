
CREATE INDEX testindex2 ON testtable USING btree (field2);

ALTER TABLE testtable CLUSTER ON testindex2;
