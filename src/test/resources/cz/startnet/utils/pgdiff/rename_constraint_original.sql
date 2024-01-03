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


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE "addresses"  (
    "id" SERIAL NOT NULL,
    "address" TEXT,
    "created_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "updated_at" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "deleted_at" TIMESTAMP WITHOUT TIME ZONE,
    "user_id" INTEGER NOT NULL
);


--
-- Name: testtable; Type: TABLE; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE TABLE testtable (
    field1 integer,
    field2 integer,
    field3 character varying(150) DEFAULT 'none'::character varying,
    field4 double precision,
    "full" timestamp with time zone DEFAULT '2006-11-10 00:00:00+01'::timestamp with time zone NOT NULL,
    CONSTRAINT field4check CHECK ((field4 > (0.0)::double precision))
);
ALTER TABLE ONLY testtable ALTER COLUMN "full" SET STATISTICS 200;


ALTER TABLE public.testtable OWNER TO fordfrog;


--
-- Name: testindex; Type: INDEX; Schema: public; Owner: fordfrog; Tablespace: 
--

CREATE INDEX testindex ON testtable USING btree (field3);


ALTER TABLE ONLY addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


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

