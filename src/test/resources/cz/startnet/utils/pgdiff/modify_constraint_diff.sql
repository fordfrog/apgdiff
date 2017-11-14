
ALTER TABLE testtable
	DROP CONSTRAINT IF EXISTS field4check;

ALTER TABLE testtable
	ADD CONSTRAINT field4check CHECK ((field4 > (0.0)::double precision));
