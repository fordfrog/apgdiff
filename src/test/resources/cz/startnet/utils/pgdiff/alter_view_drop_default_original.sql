CREATE VIEW test AS
	SELECT test_col FROM test2;

ALTER VIEW test ALTER COLUMN test_col SET DEFAULT now();
