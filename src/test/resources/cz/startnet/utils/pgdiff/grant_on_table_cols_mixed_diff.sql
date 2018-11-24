
REVOKE ALL ON TABLE items FROM admin;
GRANT UPDATE ON TABLE items TO admin;

REVOKE ALL ON TABLE items FROM customer;
GRANT SELECT ON TABLE items TO customer;

REVOKE ALL ON TABLE items FROM webuser;
GRANT INSERT, DELETE ON TABLE items TO webuser;

REVOKE ALL (id) ON TABLE items FROM admin;
GRANT SELECT (id) ON TABLE items TO admin;
REVOKE ALL (id) ON TABLE items FROM webuser;
GRANT SELECT (id) ON TABLE items TO webuser;
REVOKE ALL (name) ON TABLE items FROM admin;
GRANT SELECT (name), INSERT (name) ON TABLE items TO admin;
REVOKE ALL (name) ON TABLE items FROM customer;
GRANT UPDATE (name) ON TABLE items TO customer;
REVOKE ALL (name) ON TABLE items FROM webuser;
GRANT SELECT (name), UPDATE (name) ON TABLE items TO webuser;
