SET search_path = radstg, pg_catalog;

CREATE UNLOGGED TABLE asset_country_weight (
	asset integer NOT NULL,
	country character varying(3) NOT NULL,
	scaled_weight double precision NOT NULL
);