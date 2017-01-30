
SET search_path = schema1, pg_catalog;

REVOKE ALL ON TABLE table1 FROM ellmkay;
GRANT ALL ON TABLE table1 TO ellmkay;

SET search_path = schema2, pg_catalog;

REVOKE ALL ON TABLE table2 FROM postgres;
GRANT UPDATE ON TABLE table2 TO postgres;

SET search_path = schema3, pg_catalog;

REVOKE ALL ON SEQUENCE sequence3 FROM ellmkay;
GRANT SELECT ON SEQUENCE sequence3 TO ellmkay;

REVOKE ALL ON SEQUENCE sequence3 FROM postgres;
GRANT ALL ON SEQUENCE sequence3 TO postgres;

REVOKE ALL ON TABLE table3 FROM ellmkay;
