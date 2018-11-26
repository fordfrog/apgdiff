
ALTER TABLE testtable
	ADD COLUMN IF NOT EXISTS field5 boolean;
UPDATE  testtable SET field5=false WHERE field5 is null;
ALTER  TABLE testtable ALTER  COLUMN  field5 SET DEFAULT false;
ALTER  TABLE testtable ALTER  COLUMN field5 SET NOT NULL;