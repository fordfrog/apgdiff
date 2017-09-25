
CREATE TABLE childtable (
)
INHERITS (parenttable);

ALTER TABLE ONLY childtable
	ALTER COLUMN parenttable_id SET DEFAULT 0;
