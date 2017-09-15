DROP POLICY only_owners ON todos;
CREATE POLICY only_owners ON todos FOR INSERT TO PUBLIC;
ALTER POLICY only_admins ON todos TO admin, manager;
ALTER POLICY only_evens ON todos TO PUBLIC
USING (
  (id % 2) = 0
);
ALTER POLICY only_evens_and_1 ON todos TO PUBLIC
USING (
  (id % 2) = 0 or id = 1
);
ALTER POLICY check_evens_and_1 ON todos TO PUBLIC
WITH CHECK (
  (id % 2) = 0
  or
  id = 1
);
ALTER POLICY check_using_evens_and_1 ON todos TO PUBLIC
USING (
  (id % 2) = 0
  or
  id = 1
)
WITH CHECK (
  (id % 2) = 0
  or
  id = 1
);
