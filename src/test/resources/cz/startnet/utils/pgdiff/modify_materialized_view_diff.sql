
DROP MATERIALIZED VIEW IF EXISTS testview;

CREATE MATERIALIZED VIEW testview AS
	SELECT testtable.name, testtable.id FROM testtable;
