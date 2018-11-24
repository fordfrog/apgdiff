--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;


CREATE TABLE parenttable (
    a text
);

CREATE TABLE childtable (
    b text
)
INHERITS (parenttable);

CREATE TABLE grandchildtable (
    c text
)
INHERITS (childtable);

--
-- PostgreSQL database dump complete
--