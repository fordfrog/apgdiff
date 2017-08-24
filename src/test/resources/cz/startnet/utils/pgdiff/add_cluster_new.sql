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
    field1 integer,
    field2 integer,
    field3 character varying(150) DEFAULT 'none'::character varying,
    field4 double precision
);


ALTER TABLE public.testtable OWNER TO fordfrog;

--
-- Name: testtable2; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE testtable2 (
    id integer NOT NULL,
    col1 boolean NOT NULL
);


ALTER TABLE public.testtable2 OWNER TO fordfrog;

--
-- Name: testtable2_id_seq; Type: SEQUENCE; Schema: public; Owner: fordfrog
--

CREATE SEQUENCE testtable2_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.testtable2_id_seq OWNER TO fordfrog;

--
-- Name: testtable2_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fordfrog
--

ALTER SEQUENCE testtable2_id_seq OWNED BY testtable2.id;


--
-- Name: testtable2_id_seq; Type: SEQUENCE SET; Schema: public; Owner: fordfrog
--

SELECT pg_catalog.setval('testtable2_id_seq', 1, false);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: fordfrog
--

ALTER TABLE testtable2 ALTER COLUMN id SET DEFAULT nextval('testtable2_id_seq'::regclass);


--
-- Data for Name: testtable; Type: TABLE DATA; Schema: public; Owner: fordfrog
--



--
-- Data for Name: testtable2; Type: TABLE DATA; Schema: public; Owner: fordfrog
--



--
-- Name: testindex; Type: INDEX; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE INDEX testindex ON testtable USING btree (field1);

ALTER TABLE testtable CLUSTER ON testindex;


--
-- Name: testtable2_col1; Type: INDEX; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE INDEX testtable2_col1 ON testtable2 USING btree (col1);

ALTER TABLE testtable2 CLUSTER ON testtable2_col1;


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

