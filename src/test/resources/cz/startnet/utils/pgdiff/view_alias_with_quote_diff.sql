DROP VIEW IF EXISTS foo;

CREATE VIEW foo AS
	SELECT bar AS "Foo's bar" FROM tableName;
