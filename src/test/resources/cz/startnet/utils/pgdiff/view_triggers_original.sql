--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: testtable; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE testtable (
    id bigint,
    name character varying(30)
);


ALTER TABLE public.testtable OWNER TO fordfrog;

--
-- Name: testview; Type: VIEW; Schema: public; Owner: fordfrog
--

CREATE VIEW testview AS
    SELECT testtable.id, testtable.name FROM testtable;


ALTER TABLE public.testview OWNER TO fordfrog;

CREATE FUNCTION fn_trg_testview() RETURNS trigger LANGUAGE plpgsql
AS $$
BEGIN
	-- do nothing
	RETURN OLD;
END;
$$;

ALTER FUNCTION public.fn_trg_testview() OWNER TO fordfrog;

CREATE TRIGGER trg_testview_instead_of_delete INSTEAD OF DELETE ON testview FOR EACH ROW EXECUTE PROCEDURE fn_trg_testview();
CREATE TRIGGER trg_testview_instead_of_insert INSTEAD OF INSERT ON testview FOR EACH ROW EXECUTE PROCEDURE fn_trg_testview();
CREATE TRIGGER trg_testview_instead_of_update INSTEAD OF UPDATE ON testview FOR EACH ROW EXECUTE PROCEDURE fn_trg_testview();

--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

REVOKE ALL ON FUNCTION fn_trg_testview() FROM PUBLIC;
REVOKE ALL ON FUNCTION fn_trg_testview() FROM postgres;
GRANT ALL ON FUNCTION fn_trg_testview() TO PUBLIC;
GRANT ALL ON FUNCTION fn_trg_testview() TO postgres;

