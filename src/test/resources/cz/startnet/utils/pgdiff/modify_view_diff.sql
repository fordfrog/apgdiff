
DROP VIEW IF EXISTS testview;

CREATE VIEW testview AS
	SELECT testtable.name, testtable.id FROM testtable;
