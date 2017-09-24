CREATE SCHEMA admin;

ALTER SCHEMA admin OWNER TO postgres;

SET search_path = admin, pg_catalog;

CREATE TABLE acl_role (
    id bigint NOT NULL
);

ALTER TABLE admin.acl_role OWNER TO postgres;

ALTER TABLE ONLY acl_role
    ADD CONSTRAINT acl_role_pkey PRIMARY KEY (id);

CREATE TABLE "user" (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(40) NOT NULL,
    is_active boolean DEFAULT false NOT NULL,
    updated timestamp without time zone DEFAULT now() NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    role_id bigint NOT NULL,
    last_visit timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE admin."user" OWNER TO postgres;

CREATE INDEX fki_user_role_id_fkey ON "user" USING btree (role_id);

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_role_id_fkey FOREIGN KEY (role_id) REFERENCES acl_role(id);