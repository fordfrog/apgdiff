--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.13
-- Dumped by pg_dump version 9.5.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE TABLE public.locations (
    id uuid NOT NULL,
    square_feet integer DEFAULT 0 NOT NULL,
    name character varying(50),
    description character varying(255),
    site_id uuid NOT NULL,
    default_yearly_hours integer,
    epact_code_name character varying,
    savings_percent double precision,
    ceiling_height integer DEFAULT 0 NOT NULL,
    map_number integer,
    project_id uuid,
    notes character varying,
    existing_controls boolean DEFAULT false NOT NULL,
    fixture_height integer DEFAULT 0 NOT NULL,
    luminescence character varying(255)
);

--
-- Name: COLUMN locations.default_yearly_hours; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.locations.default_yearly_hours IS 'Informs audit items on what their default yearly hours should be';
