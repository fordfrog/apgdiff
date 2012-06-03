--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: table1; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE table1 (
    col1 integer NOT NULL
);


ALTER TABLE public.table1 OWNER TO fordfrog;

--
-- Name: table1_col1_seq; Type: SEQUENCE; Schema: public; Owner: fordfrog
--

CREATE SEQUENCE table1_col1_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.table1_col1_seq OWNER TO fordfrog;

--
-- Name: table1_col1_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fordfrog
--

ALTER SEQUENCE table1_col1_seq OWNED BY table1.col1;


--
-- Name: table1_col1_seq; Type: SEQUENCE SET; Schema: public; Owner: fordfrog
--

SELECT pg_catalog.setval('table1_col1_seq', 1, false);


--
-- Name: col1; Type: DEFAULT; Schema: public; Owner: fordfrog
--

ALTER TABLE ONLY table1 ALTER COLUMN col1 SET DEFAULT nextval('table1_col1_seq'::regclass);


--
-- Data for Name: table1; Type: TABLE DATA; Schema: public; Owner: fordfrog
--

COPY table1 (col1) FROM stdin;
\.


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

