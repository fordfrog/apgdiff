
CREATE OR REPLACE FUNCTION trigger_test1() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    RAISE NOTICE 'trigger_test1 invoked';
    RETURN NEW;
END;
$$;

CREATE TRIGGER test1_trigger_ref1
	AFTER UPDATE ON test1
	REFERENCING OLD TABLE AS old_table
	FOR EACH ROW
	EXECUTE PROCEDURE public.trigger_test1();

CREATE TRIGGER test1_trigger_ref2
	AFTER UPDATE ON test1
	REFERENCING NEW TABLE AS new_table
	FOR EACH ROW
	EXECUTE PROCEDURE public.trigger_test1();

CREATE TRIGGER test1_trigger_ref3
	AFTER UPDATE ON test1
	REFERENCING OLD TABLE AS old_table NEW TABLE AS new_table
	FOR EACH ROW
	EXECUTE PROCEDURE public.trigger_test1();
