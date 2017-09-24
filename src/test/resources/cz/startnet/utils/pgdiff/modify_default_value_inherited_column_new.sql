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
-- Name: parenttable; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

CREATE TABLE parenttable (
    parenttable_id integer NOT NULL,
    parenttable_date timestamptz NOT NULL
);

--
-- Name: childtable; Type: TABLE; Schema: public; Owner: admin; Tablespace: 
--

CREATE TABLE childtable (
)
INHERITS (parenttable);

--
-- Name: parenttable_id; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY childtable ALTER COLUMN parenttable_id SET DEFAULT 1;

--
-- PostgreSQL database dump complete
--