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
-- Name: schema1; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA schema1;


ALTER SCHEMA schema1 OWNER TO postgres;

--
-- Name: schema2; Type: SCHEMA; Schema: -; Owner: ellmkay
--

CREATE SCHEMA schema2;


ALTER SCHEMA schema2 OWNER TO ellmkay;

--
-- Name: schema3; Type: SCHEMA; Schema: -; Owner: ellmkay
--

CREATE SCHEMA schema3;


ALTER SCHEMA schema3 OWNER TO ellmkay;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = schema1, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: table1; Type: TABLE; Schema: schema1; Owner: marcus
--

CREATE TABLE table1 (
    x integer
);


ALTER TABLE table1 OWNER TO marcus;

SET search_path = schema2, pg_catalog;

--
-- Name: table2; Type: TABLE; Schema: schema2; Owner: marcus
--

CREATE TABLE table2 (
    x integer
);


ALTER TABLE table2 OWNER TO marcus;

SET search_path = schema3, pg_catalog;

--
-- Name: sequence3; Type: SEQUENCE; Schema: schema3; Owner: postgres
--

CREATE SEQUENCE sequence3
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sequence3 OWNER TO postgres;

--
-- Name: table3; Type: TABLE; Schema: schema3; Owner: marcus
--

CREATE TABLE table3 (
    x integer
);


ALTER TABLE table3 OWNER TO marcus;

SET search_path = schema1, pg_catalog;

--
-- Data for Name: table1; Type: TABLE DATA; Schema: schema1; Owner: marcus
--

COPY table1 (x) FROM stdin;
\.


SET search_path = schema2, pg_catalog;

--
-- Data for Name: table2; Type: TABLE DATA; Schema: schema2; Owner: marcus
--

COPY table2 (x) FROM stdin;
\.


SET search_path = schema3, pg_catalog;

--
-- Name: sequence3; Type: SEQUENCE SET; Schema: schema3; Owner: postgres
--

SELECT pg_catalog.setval('sequence3', 1, false);


--
-- Data for Name: table3; Type: TABLE DATA; Schema: schema3; Owner: marcus
--

COPY table3 (x) FROM stdin;
\.


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: schema1; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA schema1 FROM PUBLIC;
REVOKE ALL ON SCHEMA schema1 FROM postgres;
GRANT ALL ON SCHEMA schema1 TO postgres;
GRANT USAGE ON SCHEMA schema1 TO ellmkay;


--
-- Name: schema2; Type: ACL; Schema: -; Owner: ellmkay
--

REVOKE ALL ON SCHEMA schema2 FROM PUBLIC;
REVOKE ALL ON SCHEMA schema2 FROM ellmkay;
GRANT ALL ON SCHEMA schema2 TO ellmkay;
GRANT USAGE ON SCHEMA schema2 TO postgres;


SET search_path = schema1, pg_catalog;

--
-- Name: table1; Type: ACL; Schema: schema1; Owner: marcus
--

REVOKE ALL ON TABLE table1 FROM PUBLIC;
REVOKE ALL ON TABLE table1 FROM marcus;
GRANT ALL ON TABLE table1 TO marcus;
GRANT SELECT ON TABLE table1 TO postgres;
GRANT ALL ON TABLE table1 TO ellmkay;


SET search_path = schema2, pg_catalog;

--
-- Name: table2; Type: ACL; Schema: schema2; Owner: marcus
--

REVOKE ALL ON TABLE table2 FROM PUBLIC;
REVOKE ALL ON TABLE table2 FROM marcus;
GRANT ALL ON TABLE table2 TO marcus;
GRANT UPDATE ON TABLE table2 TO postgres;


SET search_path = schema3, pg_catalog;

--
-- Name: sequence3; Type: ACL; Schema: schema3; Owner: postgres
--

REVOKE ALL ON SEQUENCE sequence3 FROM PUBLIC;
REVOKE ALL ON SEQUENCE sequence3 FROM postgres;
GRANT ALL ON SEQUENCE sequence3 TO postgres;
GRANT SELECT ON SEQUENCE sequence3 TO ellmkay;


--
-- Name: table3; Type: ACL; Schema: schema3; Owner: marcus
--

REVOKE ALL ON TABLE table3 FROM PUBLIC;
REVOKE ALL ON TABLE table3 FROM marcus;
GRANT ALL ON TABLE table3 TO marcus;


--
-- PostgreSQL database dump complete
--

