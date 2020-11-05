--
-- PostgreSQL database dump
--

-- Dumped from database version 12.4
-- Dumped by pg_dump version 12.4

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
-- Name: trigger_test1(); Type: FUNCTION; Schema: public; Owner: krioweb2
--

CREATE FUNCTION public.trigger_test1() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    RAISE NOTICE 'trigger_test1 invoked';
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.trigger_test1() OWNER TO krioweb2;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: test1; Type: TABLE; Schema: public; Owner: krioweb2
--

CREATE TABLE public.test1 (
    id integer NOT NULL,
    text name
);


ALTER TABLE public.test1 OWNER TO krioweb2;

--
-- Name: test1_id_seq; Type: SEQUENCE; Schema: public; Owner: krioweb2
--

CREATE SEQUENCE public.test1_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.test1_id_seq OWNER TO krioweb2;

--
-- Name: test1_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: krioweb2
--

ALTER SEQUENCE public.test1_id_seq OWNED BY public.test1.id;


--
-- Name: test1 id; Type: DEFAULT; Schema: public; Owner: krioweb2
--

ALTER TABLE ONLY public.test1 ALTER COLUMN id SET DEFAULT nextval('public.test1_id_seq'::regclass);


--
-- Data for Name: test1; Type: TABLE DATA; Schema: public; Owner: krioweb2
--

COPY public.test1 (id, text) FROM stdin;
1	mahh
\.


--
-- Name: test1_id_seq; Type: SEQUENCE SET; Schema: public; Owner: krioweb2
--

SELECT pg_catalog.setval('public.test1_id_seq', 1, true);


--
-- Name: test1 test1_pkey; Type: CONSTRAINT; Schema: public; Owner: krioweb2
--

ALTER TABLE ONLY public.test1
    ADD CONSTRAINT test1_pkey PRIMARY KEY (id);


--
-- Name: test1 test1_trigger_update; Type: TRIGGER; Schema: public; Owner: krioweb2
--

CREATE TRIGGER test1_trigger_update AFTER UPDATE ON public.test1 FOR EACH ROW EXECUTE FUNCTION public.trigger_test1();


--
-- PostgreSQL database dump complete
--

