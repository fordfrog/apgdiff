SET search_path = juzz_system, pg_catalog;

DROP FUNCTION f_obj_execute_node_select(in_id_model bigint, in_id_object text, in_arr_val text, in_mode bigint);

CREATE OR REPLACE FUNCTION f_obj_execute_node_select(in_id_model bigint, in_arr_val text, in_mode bigint) RETURNS bigint AS
$body$
DECLARE
v_ret bigint;
BEGIN
return v_ret;
END;$body$
LANGUAGE plpgsql;