SET search_path = public, pg_catalog;
DROP POLICY only_owners ON todos;
DROP POLICY check_evens ON todos;
DROP POLICY check_using_evens ON todos;

SET search_path = data, pg_catalog;
DROP POLICY only_owners ON sub_tasks;
DROP POLICY no_private ON sub_tasks;
DROP POLICY only_evens ON sub_tasks;
