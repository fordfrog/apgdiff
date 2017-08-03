SET search_path = public, pg_catalog;
CREATE POLICY only_owners ON todos FOR ALL TO PUBLIC;
CREATE POLICY check_evens ON todos FOR ALL TO PUBLIC
WITH CHECK (
  (id % 2) = 0
);
CREATE POLICY check_using_evens ON todos FOR ALL TO PUBLIC
USING (
  (id % 2) = 0
)
WITH CHECK (
  (id % 2) = 0
);

SET search_path = data, pg_catalog;
CREATE POLICY only_owners ON sub_tasks FOR ALL TO PUBLIC;
CREATE POLICY no_private ON sub_tasks FOR ALL TO anonymous, webuser;
CREATE POLICY only_evens ON sub_tasks FOR ALL TO PUBLIC
USING (
  (id % 2) = 0
);
