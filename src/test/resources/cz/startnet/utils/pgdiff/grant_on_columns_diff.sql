REVOKE ALL (id) ON TABLE todos FROM admin;
GRANT ALL (id) ON TABLE todos TO admin;
REVOKE ALL (name) ON TABLE todos FROM anonymous;
REVOKE ALL (name) ON TABLE todos FROM admin;
GRANT ALL (name) ON TABLE todos TO admin;
