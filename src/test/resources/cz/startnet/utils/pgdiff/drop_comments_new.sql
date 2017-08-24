CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;

ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;


SET search_path = public, pg_catalog;


CREATE FUNCTION test_fnc(arg character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN true;
END;$$;

ALTER FUNCTION public.test_fnc(arg character varying) OWNER TO fordfrog;


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


CREATE SEQUENCE test_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.test_id_seq OWNER TO fordfrog;

ALTER SEQUENCE test_id_seq OWNED BY test.id;


CREATE VIEW test_view AS
    SELECT test.id, test.text FROM test;

ALTER TABLE public.test_view OWNER TO fordfrog;


ALTER TABLE test ALTER COLUMN id SET DEFAULT nextval('test_id_seq'::regclass);


ALTER TABLE ONLY test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);


CREATE TRIGGER test_trigger BEFORE UPDATE ON test FOR EACH STATEMENT EXECUTE PROCEDURE trigger_fnc();