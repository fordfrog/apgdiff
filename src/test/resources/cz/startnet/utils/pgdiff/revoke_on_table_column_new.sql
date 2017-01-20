--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.5
-- Dumped by pg_dump version 9.5.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

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
-- Name: table1; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE table1 (
    col1 integer,
    col2 integer,
    col3 integer
);


ALTER TABLE table1 OWNER TO postgres;

--
-- Data for Name: table1; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY table1 (col1, col2, col3) FROM stdin;
\.


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: table1; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE table1 FROM PUBLIC;
REVOKE ALL ON TABLE table1 FROM postgres;
GRANT ALL ON TABLE table1 TO postgres;
GRANT SELECT ON TABLE table1 TO ellmkay;


--
-- Name: table1.col1; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL(col1) ON TABLE table1 FROM PUBLIC;
REVOKE ALL(col1) ON TABLE table1 FROM postgres;
GRANT ALL(col1) ON TABLE table1 TO ellmkay;


--
-- Name: table1.col3; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL(col3) ON TABLE table1 FROM PUBLIC;
REVOKE ALL(col3) ON TABLE table1 FROM postgres;
GRANT UPDATE(col3) ON TABLE table1 TO ellmkay;


--
-- PostgreSQL database dump complete
--

