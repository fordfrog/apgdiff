COMMENT ON DATABASE current_database() IS 'comments database 2';

COMMENT ON SCHEMA "public" IS 'public schema 2';

COMMENT ON SEQUENCE test_id_seq IS 'test table sequence 2';

COMMENT ON TABLE test IS 'test table 2';

COMMENT ON COLUMN test.id IS 'id column 2';

COMMENT ON COLUMN test.text IS 'text column 2';

COMMENT ON VIEW test_view IS 'test view 2';

COMMENT ON COLUMN test_view.id IS 'view id col 2';

COMMENT ON FUNCTION test_fnc(arg character varying) IS 'test function 2';

COMMENT ON CONSTRAINT text_check ON test IS 'text check 2';

COMMENT ON INDEX test_pkey IS 'primary key 2';

COMMENT ON TRIGGER test_trigger ON test IS 'test trigger 2';