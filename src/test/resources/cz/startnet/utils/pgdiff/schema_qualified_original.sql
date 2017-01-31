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
-- Name: schema1; Type: SCHEMA; Schema: -; Owner: ellmkay
--

CREATE SCHEMA schema1;


ALTER SCHEMA schema1 OWNER TO ellmkay;

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

--
-- Name: type1; Type: TYPE; Schema: schema1; Owner: ellmkay
--

CREATE TYPE type1 AS (
	col integer,
	other text
);


ALTER TYPE type1 OWNER TO ellmkay;

--
-- Name: function1(); Type: FUNCTION; Schema: schema1; Owner: ellmkay
--

CREATE FUNCTION function1() RETURNS integer
    LANGUAGE sql
    AS $$ select 1; $$;


ALTER FUNCTION schema1.function1() OWNER TO ellmkay;

SET search_path = schema2, pg_catalog;

--
-- Name: function2(); Type: FUNCTION; Schema: schema2; Owner: ellmkay
--

CREATE FUNCTION function2() RETURNS integer
    LANGUAGE sql
    AS $$ select 1; $$;


ALTER FUNCTION schema2.function2() OWNER TO ellmkay;

SET search_path = schema3, pg_catalog;

--
-- Name: function3(); Type: FUNCTION; Schema: schema3; Owner: ellmkay
--

CREATE FUNCTION function3() RETURNS integer
    LANGUAGE sql
    AS $$ select 1; $$;


ALTER FUNCTION schema3.function3() OWNER TO ellmkay;

--
-- Name: triggfunc(); Type: FUNCTION; Schema: schema3; Owner: ellmkay
--

CREATE FUNCTION triggfunc() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ begin return  null; END; $$;


ALTER FUNCTION schema3.triggfunc() OWNER TO ellmkay;

SET search_path = schema1, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: table1; Type: TABLE; Schema: schema1; Owner: ellmkay
--

CREATE TABLE table1 (
    col1 integer NOT NULL,
    col2 integer NOT NULL
);


ALTER TABLE table1 OWNER TO ellmkay;

--
-- Name: table1_col1_seq; Type: SEQUENCE; Schema: schema1; Owner: ellmkay
--

CREATE SEQUENCE table1_col1_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE table1_col1_seq OWNER TO ellmkay;

--
-- Name: table1_col1_seq; Type: SEQUENCE OWNED BY; Schema: schema1; Owner: ellmkay
--

ALTER SEQUENCE table1_col1_seq OWNED BY table1.col1;


SET search_path = schema2, pg_catalog;

--
-- Name: table2; Type: TABLE; Schema: schema2; Owner: ellmkay
--

CREATE TABLE table2 (
    col1 integer NOT NULL,
    col2 integer NOT NULL
);


ALTER TABLE table2 OWNER TO ellmkay;

--
-- Name: table2_col1_seq; Type: SEQUENCE; Schema: schema2; Owner: ellmkay
--

CREATE SEQUENCE table2_col1_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE table2_col1_seq OWNER TO ellmkay;

--
-- Name: table2_col1_seq; Type: SEQUENCE OWNED BY; Schema: schema2; Owner: ellmkay
--

ALTER SEQUENCE table2_col1_seq OWNED BY table2.col1;


--
-- Name: view2; Type: VIEW; Schema: schema2; Owner: ellmkay
--

CREATE VIEW view2 AS
 SELECT table1.col1,
    table1.col2
   FROM schema1.table1;


ALTER TABLE view2 OWNER TO ellmkay;

SET search_path = schema3, pg_catalog;

--
-- Name: table3; Type: TABLE; Schema: schema3; Owner: ellmkay
--

CREATE TABLE table3 (
    col1 integer NOT NULL,
    col2 integer NOT NULL
);


ALTER TABLE table3 OWNER TO ellmkay;

--
-- Name: table3_col1_seq; Type: SEQUENCE; Schema: schema3; Owner: ellmkay
--

CREATE SEQUENCE table3_col1_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE table3_col1_seq OWNER TO ellmkay;

--
-- Name: table3_col1_seq; Type: SEQUENCE OWNED BY; Schema: schema3; Owner: ellmkay
--

ALTER SEQUENCE table3_col1_seq OWNED BY table3.col1;


SET search_path = schema1, pg_catalog;

--
-- Name: col1; Type: DEFAULT; Schema: schema1; Owner: ellmkay
--

ALTER TABLE ONLY table1 ALTER COLUMN col1 SET DEFAULT nextval('table1_col1_seq'::regclass);


SET search_path = schema2, pg_catalog;

--
-- Name: col1; Type: DEFAULT; Schema: schema2; Owner: ellmkay
--

ALTER TABLE ONLY table2 ALTER COLUMN col1 SET DEFAULT nextval('table2_col1_seq'::regclass);


SET search_path = schema3, pg_catalog;

--
-- Name: col1; Type: DEFAULT; Schema: schema3; Owner: ellmkay
--

ALTER TABLE ONLY table3 ALTER COLUMN col1 SET DEFAULT nextval('table3_col1_seq'::regclass);


SET search_path = schema1, pg_catalog;

--
-- Data for Name: table1; Type: TABLE DATA; Schema: schema1; Owner: ellmkay
--

COPY table1 (col1, col2) FROM stdin;
\.


--
-- Name: table1_col1_seq; Type: SEQUENCE SET; Schema: schema1; Owner: ellmkay
--

SELECT pg_catalog.setval('table1_col1_seq', 1, false);


SET search_path = schema2, pg_catalog;

--
-- Data for Name: table2; Type: TABLE DATA; Schema: schema2; Owner: ellmkay
--

COPY table2 (col1, col2) FROM stdin;
\.


--
-- Name: table2_col1_seq; Type: SEQUENCE SET; Schema: schema2; Owner: ellmkay
--

SELECT pg_catalog.setval('table2_col1_seq', 1, false);


SET search_path = schema3, pg_catalog;

--
-- Data for Name: table3; Type: TABLE DATA; Schema: schema3; Owner: ellmkay
--

COPY table3 (col1, col2) FROM stdin;
\.


--
-- Name: table3_col1_seq; Type: SEQUENCE SET; Schema: schema3; Owner: ellmkay
--

SELECT pg_catalog.setval('table3_col1_seq', 1, false);


SET search_path = schema1, pg_catalog;

--
-- Name: table1_col2_key; Type: CONSTRAINT; Schema: schema1; Owner: ellmkay
--

ALTER TABLE ONLY table1
    ADD CONSTRAINT table1_col2_key UNIQUE (col2);


--
-- Name: table1_pkey; Type: CONSTRAINT; Schema: schema1; Owner: ellmkay
--

ALTER TABLE ONLY table1
    ADD CONSTRAINT table1_pkey PRIMARY KEY (col1);


SET search_path = schema2, pg_catalog;

--
-- Name: table2_pkey; Type: CONSTRAINT; Schema: schema2; Owner: ellmkay
--

ALTER TABLE ONLY table2
    ADD CONSTRAINT table2_pkey PRIMARY KEY (col1);


SET search_path = schema3, pg_catalog;

--
-- Name: table3_pkey; Type: CONSTRAINT; Schema: schema3; Owner: ellmkay
--

ALTER TABLE ONLY table3
    ADD CONSTRAINT table3_pkey PRIMARY KEY (col1);


SET search_path = schema1, pg_catalog;

--
-- Name: index1; Type: INDEX; Schema: schema1; Owner: ellmkay
--

CREATE INDEX index1 ON table1 USING btree (col2);


SET search_path = schema2, pg_catalog;

--
-- Name: index2; Type: INDEX; Schema: schema2; Owner: ellmkay
--

CREATE INDEX index2 ON table2 USING btree (col2);


SET search_path = schema3, pg_catalog;

--
-- Name: index3; Type: INDEX; Schema: schema3; Owner: ellmkay
--

CREATE INDEX index3 ON table3 USING btree (col2);


SET search_path = schema1, pg_catalog;

--
-- Name: trigger1; Type: TRIGGER; Schema: schema1; Owner: ellmkay
--

CREATE TRIGGER trigger1 AFTER INSERT ON table1 FOR EACH STATEMENT EXECUTE PROCEDURE schema3.triggfunc();


SET search_path = schema2, pg_catalog;

--
-- Name: table2_col2_fkey; Type: FK CONSTRAINT; Schema: schema2; Owner: ellmkay
--

ALTER TABLE ONLY table2
    ADD CONSTRAINT table2_col2_fkey FOREIGN KEY (col2) REFERENCES schema1.table1(col2);


SET search_path = schema3, pg_catalog;

--
-- Name: table3_col2_fkey; Type: FK CONSTRAINT; Schema: schema3; Owner: ellmkay
--

ALTER TABLE ONLY table3
    ADD CONSTRAINT table3_col2_fkey FOREIGN KEY (col2) REFERENCES schema1.table1(col2);


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

