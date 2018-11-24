
CREATE VIEW testview WITH (security_barrier) AS
	SELECT testtable.id, testtable.name FROM testtable;
