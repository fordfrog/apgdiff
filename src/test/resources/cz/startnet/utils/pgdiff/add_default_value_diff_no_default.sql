
UPDATE  testtable SET field4=0.0 WHERE field4 is null;
ALTER TABLE testtable ALTER COLUMN field4 SET DEFAULT 0.0;