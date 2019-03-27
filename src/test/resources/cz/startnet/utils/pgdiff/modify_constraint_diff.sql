
ALTER TABLE IF EXISTS testtable
	DROP CONSTRAINT IF EXISTS field4check;

ALTER TABLE IF EXISTS testtable
	ADD CONSTRAINT IF NOT EXISTS field4check CHECK ((field4 > (0.0)::double precision));
