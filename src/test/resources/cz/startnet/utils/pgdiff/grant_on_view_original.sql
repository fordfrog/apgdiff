--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.1

-- Started on 2016-03-28 21:18:55 KRAT

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
-- TOC entry 2149 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 182 (class 1259 OID 17707)
-- Name: table1; Type: TABLE; Schema: public; Owner: dv
--

CREATE TABLE table1 (
    id integer NOT NULL,
    msg text,
    date_create timestamp without time zone
);


ALTER TABLE table1 OWNER TO dv;

--
-- TOC entry 181 (class 1259 OID 17705)
-- Name: table1_id_seq; Type: SEQUENCE; Schema: public; Owner: dv
--

CREATE SEQUENCE table1_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE table1_id_seq OWNER TO dv;

--
-- TOC entry 2151 (class 0 OID 0)
-- Dependencies: 181
-- Name: table1_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dv
--

ALTER SEQUENCE table1_id_seq OWNED BY table1.id;


--
-- TOC entry 183 (class 1259 OID 17716)
-- Name: view1; Type: VIEW; Schema: public; Owner: dv
--

CREATE VIEW view1 AS
 SELECT table1.id,
    table1.msg,
    table1.date_create
   FROM table1;


ALTER TABLE view1 OWNER TO dv;

--
-- TOC entry 2024 (class 2604 OID 17710)
-- Name: id; Type: DEFAULT; Schema: public; Owner: dv
--

ALTER TABLE ONLY table1 ALTER COLUMN id SET DEFAULT nextval('table1_id_seq'::regclass);


--
-- TOC entry 2026 (class 2606 OID 17715)
-- Name: table1_pkey; Type: CONSTRAINT; Schema: public; Owner: dv
--

ALTER TABLE ONLY table1
    ADD CONSTRAINT table1_pkey PRIMARY KEY (id);


--
-- TOC entry 2148 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2150 (class 0 OID 0)
-- Dependencies: 182
-- Name: table1; Type: ACL; Schema: public; Owner: dv
--

REVOKE ALL ON TABLE table1 FROM PUBLIC;
REVOKE ALL ON TABLE table1 FROM dv;
GRANT ALL ON TABLE table1 TO dv;


--
-- TOC entry 2152 (class 0 OID 0)
-- Dependencies: 181
-- Name: table1_id_seq; Type: ACL; Schema: public; Owner: dv
--

REVOKE ALL ON SEQUENCE table1_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE table1_id_seq FROM dv;
GRANT ALL ON SEQUENCE table1_id_seq TO dv;


--
-- TOC entry 2153 (class 0 OID 0)
-- Dependencies: 183
-- Name: view1; Type: ACL; Schema: public; Owner: dv
--

REVOKE ALL ON TABLE view1 FROM PUBLIC;
REVOKE ALL ON TABLE view1 FROM dv;
GRANT ALL ON TABLE view1 TO dv;


-- Completed on 2016-03-28 21:18:55 KRAT

--
-- PostgreSQL database dump complete
--
