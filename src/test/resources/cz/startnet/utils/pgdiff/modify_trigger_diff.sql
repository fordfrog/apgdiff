
DROP TRIGGER IF EXISTS test_table_trigger ON test_table;

CREATE TRIGGER test_table_trigger
	BEFORE INSERT OR UPDATE OF id ON test_table
	FOR EACH STATEMENT
	EXECUTE PROCEDURE test_table_trigger();
