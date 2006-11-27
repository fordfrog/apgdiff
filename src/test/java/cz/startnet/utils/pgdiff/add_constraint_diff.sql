
ALTER TABLE testtable
	ADD CONSTRAINT field4check CHECK (((field4 > (-5.0)::double precision) AND (field4 < (5.0)::double precision)));
