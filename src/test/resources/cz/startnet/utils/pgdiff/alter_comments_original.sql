COMMENT ON DATABASE comments IS 'comments database';
COMMENT ON SCHEMA public IS 'public schema';


CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;

ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;


SET search_path = public, pg_catalog;


CREATE FUNCTION test_fnc(arg character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN true;
END;$$;

ALTER FUNCTION public.test_fnc(arg character varying) OWNER TO fordfrog;

COMMENT ON FUNCTION test_fnc(arg character varying) IS 'test function';


CREATE FUNCTION trigger_fnc() RETURNS trigger
    LANGUAGE plpgsql
    AS $$begin
end;$$;

ALTER FUNCTION public.trigger_fnc() OWNER TO fordfrog;


CREATE TABLE test (
    id integer NOT NULL,
    text character varying(20) NOT NULL,
    CONSTRAINT text_check CHECK ((length((text)::text) > 0))
);

ALTER TABLE public.test OWNER TO fordfrog;

COMMENT ON TABLE test IS 'test table';

COMMENT ON COLUMN test.id IS 'id column';

COMMENT ON COLUMN test.text IS 'text column';


COMMENT ON CONSTRAINT text_check ON test IS 'text check';


CREATE SEQUENCE test_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.test_id_seq OWNER TO fordfrog;

ALTER SEQUENCE test_id_seq OWNED BY test.id;

COMMENT ON SEQUENCE test_id_seq IS 'test table sequence';


CREATE VIEW test_view AS
    SELECT test.id, test.text FROM test;

ALTER TABLE public.test_view OWNER TO fordfrog;

COMMENT ON VIEW test_view IS 'test view';

COMMENT ON COLUMN test_view.id IS 'view id col';


ALTER TABLE test ALTER COLUMN id SET DEFAULT nextval('test_id_seq'::regclass);


ALTER TABLE ONLY test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);

COMMENT ON INDEX test_pkey IS 'primary key';


CREATE TRIGGER test_trigger BEFORE UPDATE ON test FOR EACH STATEMENT EXECUTE PROCEDURE trigger_fnc();

COMMENT ON TRIGGER test_trigger ON test IS 'test trigger';