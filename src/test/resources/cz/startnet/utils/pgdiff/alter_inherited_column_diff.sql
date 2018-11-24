
ALTER TABLE ONLY childtable
	ALTER COLUMN a SET DEFAULT "child a";

ALTER TABLE ONLY grandchildtable
	ALTER COLUMN a SET DEFAULT "grandchild a";