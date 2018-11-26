ALTER TABLE test1
	ADD COLUMN IF NOT EXISTS test2 TEXT,
	ADD COLUMN IF NOT EXISTS test TEXT,
	ADD COLUMN IF NOT EXISTS test3 TEXT;
UPDATE  test1 SET test2='*/' WHERE test2 is null;
ALTER  TABLE test1 ALTER  COLUMN  test2 SET DEFAULT '*/';
UPDATE  test1 SET test='this /*is*/ test' WHERE test is null;
ALTER  TABLE test1 ALTER  COLUMN  test SET DEFAULT 'this /*is*/ test';
UPDATE  test1 SET test3='*/' WHERE test3 is null;
ALTER  TABLE test1 ALTER  COLUMN  test3 SET DEFAULT '*/';