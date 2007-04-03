--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: 
--

CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

--
-- Name: power_number(integer); Type: FUNCTION; Schema: public; Owner: fordfrog
--

CREATE FUNCTION power_number(number integer) RETURNS integer
    AS $$
begin
	return number * number;
end;
$$
    LANGUAGE plpgsql;


ALTER FUNCTION public.power_number(number integer) OWNER TO fordfrog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: test_table; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE test_table (
    id serial NOT NULL
);


ALTER TABLE public.test_table OWNER TO fordfrog;

--
-- Name: test_table_pkey; Type: CONSTRAINT; Schema: public; Owner: fordfrog; Tablespace: 
--

ALTER TABLE ONLY test_table
    ADD CONSTRAINT test_table_pkey PRIMARY KEY (id);


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

