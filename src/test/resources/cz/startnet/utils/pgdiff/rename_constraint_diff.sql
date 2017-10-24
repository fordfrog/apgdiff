ALTER TABLE IF EXISTS addresses
	RENAME CONSTRAINT addresses_pkey TO addresses_id_pkey;

ALTER TABLE IF EXISTS testtable
	RENAME CONSTRAINT field4check TO field4check_renamed;

