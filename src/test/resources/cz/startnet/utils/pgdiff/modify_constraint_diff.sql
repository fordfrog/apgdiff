
ALTER TABLE testtable
	DROP CONSTRAINT field4check;

ALTER TABLE testtable
	ADD CONSTRAINT field4check CHECK ((field4 > (0.0)::double precision));
