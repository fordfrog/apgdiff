
ALTER TABLE IF EXISTS inventoryitemsupplier
	ADD CONSTRAINT IF NOT EXISTS inventoryitemsupplier_pkey PRIMARY KEY (id);

ALTER TABLE IF EXISTS inventoryitemsupplier
	ADD CONSTRAINT IF NOT EXISTS inventoryitemsupplier_5a808b9c_key UNIQUE (inventoryitemid, partneridentificationid);
