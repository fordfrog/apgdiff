--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: ABC; Type: SCHEMA; Schema: -; Owner: fordfrog
--

CREATE SCHEMA "ABC";


ALTER SCHEMA "ABC" OWNER TO fordfrog;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = "ABC", pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: testtable; Type: TABLE; Schema: ABC; Owner: fordfrog; Tablespace: 
--

CREATE TABLE testtable (
    field1 integer,
    field2 integer,
    field3 character varying(150) DEFAULT 'none'::character varying,
    field4 double precision
);


ALTER TABLE "ABC".testtable OWNER TO fordfrog;

--
-- Data for Name: testtable; Type: TABLE DATA; Schema: ABC; Owner: fordfrog
--

COPY testtable (field1, field2, field3, field4) FROM stdin;
\.


--
-- Name: testindex; Type: INDEX; Schema: ABC; Owner: fordfrog; Tablespace: 
--

CREATE INDEX testindex ON testtable USING btree (field3);


--
-- PostgreSQL database dump complete
--

