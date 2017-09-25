
CREATE TABLE IF NOT EXISTS testtable (
	field1 polygon
)
INHERITS (parenttable);

ALTER TABLE testtable OWNER TO fordfrog;
