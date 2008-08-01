
DROP TRIGGER test_table_trigger ON test_table;

CREATE TRIGGER test_table_trigger
	BEFORE INSERT ON test_table
	FOR EACH STATEMENT
	EXECUTE PROCEDURE test_table_trigger();
