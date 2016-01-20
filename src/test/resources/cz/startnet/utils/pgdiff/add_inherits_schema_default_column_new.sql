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
-- Name: schema1; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA schema1;

--
-- Name: parenttable; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

CREATE TABLE parenttable (
    parenttable_id integer NOT NULL,
    parenttable_date timestamptz NOT NULL
);


SET search_path = schema1, pg_catalog;

--
-- Name: childtable; Type: TABLE; Schema: schema1; Owner: admin; Tablespace: 
--

CREATE TABLE childtable (
    childtable_date timestamptz NOT NULL
)
INHERITS (public.parenttable);

--
-- Name: parenttable_id; Type: DEFAULT; Schema: schema1; Owner: admin
--

ALTER TABLE ONLY childtable ALTER COLUMN parenttable_id SET DEFAULT 0;

--
-- PostgreSQL database dump complete
--