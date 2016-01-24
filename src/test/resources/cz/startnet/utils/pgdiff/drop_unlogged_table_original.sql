--
-- Name: rad; Type: SCHEMA; Schema: -; Owner: asi
--

CREATE SCHEMA rad;


ALTER SCHEMA rad OWNER TO asi;

--
-- Name: radstg; Type: SCHEMA; Schema: -; Owner: asi
--

CREATE SCHEMA radstg;


ALTER SCHEMA radstg OWNER TO asi;



SET search_path = rad, pg_catalog;

--
-- Name: asset_country_weight; Type: TABLE; Schema: rad; Owner: asi; Tablespace:
--

CREATE TABLE asset_country_weight (
    asset integer NOT NULL,
    country character varying(3) NOT NULL,
    scaled_weight double precision NOT NULL
);


ALTER TABLE asset_country_weight OWNER TO asi;


SET search_path = radstg, pg_catalog;

--
-- Name: asset_country_weight; Type: TABLE; Schema: radstg; Owner: asi; Tablespace:
--

CREATE UNLOGGED TABLE asset_country_weight (
    asset integer NOT NULL,
    country character varying(3) NOT NULL,
    scaled_weight double precision NOT NULL
);


ALTER TABLE asset_country_weight OWNER TO asi;
