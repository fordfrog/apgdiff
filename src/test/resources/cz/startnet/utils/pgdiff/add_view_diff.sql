
CREATE VIEW testview WITH (security_barrier) AS
	SELECT testtable.id, testtable.name FROM testtable;

ALTER VIEW testview OWNER TO fordfrog;
