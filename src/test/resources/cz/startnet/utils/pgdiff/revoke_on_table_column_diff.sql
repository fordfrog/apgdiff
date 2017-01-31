
REVOKE ALL ON TABLE table1 FROM ellmkay;
GRANT SELECT ON TABLE table1 TO ellmkay;

REVOKE ALL (col1) ON TABLE table1 FROM ellmkay;
GRANT ALL (col1) ON TABLE table1 TO ellmkay;
REVOKE ALL (col2) ON TABLE table1 FROM public;
REVOKE ALL (col2) ON TABLE table1 FROM postgres;
REVOKE ALL (col2) ON TABLE table1 FROM ellmkay;
REVOKE ALL (col3) ON TABLE table1 FROM ellmkay;
GRANT UPDATE (col3) ON TABLE table1 TO ellmkay;
