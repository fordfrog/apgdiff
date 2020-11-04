
CREATE OR REPLACE FUNCTION trigger_test1() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    RAISE NOTICE 'trigger_test1 invoked';
    RETURN NEW;
END;
$$;

CREATE TRIGGER test1_trigger_update
	AFTER UPDATE ON test1
	FOR EACH ROW
	EXECUTE PROCEDURE public.trigger_test1();
