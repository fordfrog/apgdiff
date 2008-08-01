
ALTER TABLE inventoryitemsupplier
	ADD CONSTRAINT inventoryitemsupplier_pkey PRIMARY KEY (id);

ALTER TABLE inventoryitemsupplier
	ADD CONSTRAINT inventoryitemsupplier_5a808b9c_key UNIQUE (inventoryitemid, partneridentificationid);
