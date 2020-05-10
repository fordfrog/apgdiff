--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: schema_1; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA schema_1;


ALTER SCHEMA schema_1 OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: tb_teste1; Type: TABLE; Schema: schema_1; Owner: postgres
--

CREATE TABLE schema_1.tb_teste1 (
    id integer,
    description character varying
);


ALTER TABLE schema_1.tb_teste1 OWNER TO postgres;

--
-- Name: tb_teste2; Type: TABLE; Schema: schema_1; Owner: postgres
--

CREATE TABLE schema_1.tb_teste2 (
    id integer,
    description character varying
);


ALTER TABLE schema_1.tb_teste2 OWNER TO postgres;

--
-- PostgreSQL database dump complete
--

