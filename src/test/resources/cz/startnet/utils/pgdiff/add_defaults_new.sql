--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: testtable; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE testtable (
    id integer NOT NULL,
    col1 smallint NOT NULL,
    col2 character varying(20),
    col3 boolean NOT NULL,
    col4 character(10) NOT NULL,
    col5 text NOT NULL
);


ALTER TABLE public.testtable OWNER TO fordfrog;

--
-- Name: testtable_id_seq; Type: SEQUENCE; Schema: public; Owner: fordfrog
--

CREATE SEQUENCE testtable_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.testtable_id_seq OWNER TO fordfrog;

--
-- Name: testtable_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fordfrog
--

ALTER SEQUENCE testtable_id_seq OWNED BY testtable.id;


--
-- Name: testtable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: fordfrog
--

SELECT pg_catalog.setval('testtable_id_seq', 1, false);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: fordfrog
--

ALTER TABLE testtable ALTER COLUMN id SET DEFAULT nextval('testtable_id_seq'::regclass);


--
-- Data for Name: testtable; Type: TABLE DATA; Schema: public; Owner: fordfrog
--



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

