--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: comments; Type: COMMENT; Schema: -; Owner: fordfrog
--

COMMENT ON DATABASE comments IS 'comments database';

COMMENT ON SCHEMA public IS 'public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- Name: test_fnc(character varying); Type: FUNCTION; Schema: public; Owner: fordfrog
--

CREATE FUNCTION test_fnc(arg character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$BEGIN
RETURN true;
END;$$;


ALTER FUNCTION public.test_fnc(arg character varying) OWNER TO fordfrog;

--
-- Name: FUNCTION test_fnc(arg character varying); Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON FUNCTION test_fnc(arg character varying) IS 'test function';


--
-- Name: trigger_fnc(); Type: FUNCTION; Schema: public; Owner: fordfrog
--

CREATE FUNCTION trigger_fnc() RETURNS trigger
    LANGUAGE plpgsql
    AS $$begin
end;$$;


ALTER FUNCTION public.trigger_fnc() OWNER TO fordfrog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: test; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace:
--

CREATE TABLE test (
    id integer NOT NULL,
    text character varying(20) NOT NULL,
    CONSTRAINT text_check CHECK ((length((text)::text) > 0))
);


ALTER TABLE public.test OWNER TO fordfrog;

COMMENT ON TABLE test IS 'test table';

--
-- Name: COLUMN test.id; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON COLUMN test.id IS 'id column';


--
-- Name: COLUMN test.text; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON COLUMN test.text IS 'text column';


--
-- Name: CONSTRAINT text_check ON test; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON CONSTRAINT text_check ON test IS 'text check';


--
-- Name: test_id_seq; Type: SEQUENCE; Schema: public; Owner: fordfrog
--

CREATE SEQUENCE test_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.test_id_seq OWNER TO fordfrog;

--
-- Name: test_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fordfrog
--

ALTER SEQUENCE test_id_seq OWNED BY test.id;


--
-- Name: SEQUENCE test_id_seq; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON SEQUENCE test_id_seq IS 'test table sequence';


--
-- Name: test_view; Type: VIEW; Schema: public; Owner: fordfrog
--

CREATE VIEW test_view AS
    SELECT test.id, test.text FROM test;


ALTER TABLE public.test_view OWNER TO fordfrog;

--
-- Name: VIEW test_view; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON VIEW test_view IS 'test view';


--
-- Name: COLUMN test_view.id; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON COLUMN test_view.id IS 'view id col';


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: fordfrog
--

ALTER TABLE test ALTER COLUMN id SET DEFAULT nextval('test_id_seq'::regclass);


--
-- Name: test_pkey; Type: CONSTRAINT; Schema: public; Owner: fordfrog; Tablespace:
--

ALTER TABLE ONLY test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);


--
-- Name: INDEX test_pkey; Type: COMMENT; Schema: public; Owner: fordfrog
--

COMMENT ON INDEX test_pkey IS 'primary key';


--
-- Name: test_trigger; Type: TRIGGER; Schema: public; Owner: fordfrog
--

CREATE TRIGGER test_trigger BEFORE UPDATE ON test FOR EACH STATEMENT EXECUTE PROCEDURE trigger_fnc();

COMMENT ON TRIGGER test_trigger ON test IS 'test trigger';


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

