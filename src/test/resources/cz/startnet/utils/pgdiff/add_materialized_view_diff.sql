
CREATE MATERIALIZED VIEW testview AS
	SELECT testtable.id, testtable.name FROM testtable;

ALTER VIEW testview OWNER TO fordfrog;
