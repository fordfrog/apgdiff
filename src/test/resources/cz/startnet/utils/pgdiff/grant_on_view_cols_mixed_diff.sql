
REVOKE ALL ON TABLE items_view FROM admin;
GRANT UPDATE ON TABLE items_view TO admin;

REVOKE ALL ON TABLE items_view FROM customer;
GRANT SELECT ON TABLE items_view TO customer;

REVOKE ALL ON TABLE items_view FROM webuser;
GRANT INSERT, DELETE ON TABLE items_view TO webuser;

REVOKE ALL (id) ON TABLE items_view FROM admin;
GRANT SELECT (id) ON TABLE items_view TO admin;
REVOKE ALL (id) ON TABLE items_view FROM webuser;
GRANT SELECT (id) ON TABLE items_view TO webuser;
REVOKE ALL (name) ON TABLE items_view FROM admin;
GRANT SELECT (name), INSERT (name) ON TABLE items_view TO admin;
REVOKE ALL (name) ON TABLE items_view FROM customer;
GRANT UPDATE (name) ON TABLE items_view TO customer;
REVOKE ALL (name) ON TABLE items_view FROM webuser;
GRANT SELECT (name), UPDATE (name) ON TABLE items_view TO webuser;
