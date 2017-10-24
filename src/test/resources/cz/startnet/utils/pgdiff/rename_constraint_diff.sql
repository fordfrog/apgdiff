ALTER TABLE addresses
	RENAME CONSTRAINT IF EXISTS addresses_pkey TO addresses_id_pkey;

ALTER TABLE testtable
	RENAME CONSTRAINT IF EXISTS field4check TO field4check_renamed;

