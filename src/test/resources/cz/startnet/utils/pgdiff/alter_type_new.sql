--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

-- Started on 2016-05-02 12:55:57 KRAT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12395)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2146 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 556 (class 1247 OID 16397)
-- Name: bug_status; Type: TYPE; Schema: public; Owner: dv
--

CREATE TYPE bug_status AS ENUM (
    'new',
    'open',
    'closed',
    'deleted'
);


ALTER TYPE bug_status OWNER TO dv;

--
-- TOC entry 549 (class 1247 OID 16389)
-- Name: descr_type; Type: TYPE; Schema: public; Owner: dv
--

CREATE TYPE descr_type AS (
	name text,
	amount integer,
	date_create timestamp without time zone
);


ALTER TYPE descr_type OWNER TO dv;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 16390)
-- Name: t1; Type: TABLE; Schema: public; Owner: dv
--

CREATE TABLE t1 (
    id integer,
    descr descr_type
);


ALTER TABLE t1 OWNER TO dv;

--
-- TOC entry 2145 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-05-02 12:55:57 KRAT

--
-- PostgreSQL database dump complete
--

