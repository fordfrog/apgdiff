CREATE SCHEMA juzz_system;

CREATE OR REPLACE FUNCTION "juzz_system"."f_obj_execute_node_select" (
"in_id_model" bigint,
"in_arr_val" text,
"in_mode" bigint
)
RETURNS bigint AS
$body$
DECLARE
v_ret bigint;
BEGIN
return v_ret;
END;$body$
LANGUAGE plpgsql;