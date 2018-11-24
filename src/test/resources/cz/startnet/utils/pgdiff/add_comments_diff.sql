COMMENT ON DATABASE current_database() IS 'comments database';

COMMENT ON SCHEMA "public" IS 'public schema';

COMMENT ON SEQUENCE test_id_seq IS 'test table sequence';

COMMENT ON TABLE test IS 'test table';

COMMENT ON COLUMN test.id IS 'id column';

COMMENT ON COLUMN test.text IS 'text column';

COMMENT ON VIEW test_view IS 'test view';

COMMENT ON COLUMN test_view.id IS 'view id col';

COMMENT ON COLUMN test_view.text IS 'view text col';

COMMENT ON FUNCTION test_fnc(arg character varying) IS 'test function';

COMMENT ON CONSTRAINT text_check ON test IS 'text check';

COMMENT ON INDEX test_pkey IS 'primary key';

COMMENT ON TRIGGER test_trigger ON test IS 'test trigger';
