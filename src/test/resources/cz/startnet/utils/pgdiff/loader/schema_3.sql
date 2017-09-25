--
-- TOC Entry ID 2 (OID 605638)
--
-- Name: admins_aid_seq Type: SEQUENCE Owner: xxxx
--

CREATE SEQUENCE "admins_aid_seq" start 1 increment 1 maxvalue 1000000000 minvalue 1 cache 1;

--
-- TOC Entry ID 108 (OID 605640)
--
-- Name: admins Type: TABLE Owner: enki
--

CREATE TABLE "admins" (
    "aid" integer DEFAULT nextval('"admins_aid_seq"'::text) NOT NULL,
    "companyid" integer DEFAULT 0 NOT NULL,
    "groupid" integer DEFAULT 0 NOT NULL,
    "username" character varying NOT NULL,
    "password" character varying(40) NOT NULL,
    "superuser" boolean DEFAULT 'f'::bool NOT NULL,
    "name" character varying(40),
    "surname" character varying(40),
    "email" character varying(100) NOT NULL,
    "tel" character varying(40),
    "mobile" character varying(40),
    "enabled" boolean DEFAULT 't'::bool NOT NULL,
    "lastlogints" timestamp with time zone DEFAULT now() NOT NULL,
    "expirienced" boolean DEFAULT 'f'::bool,
    Constraint "admins_pkey" Primary Key ("aid")
);