CREATE FUNCTION f_obj_execute_node_select(in_id_model bigint, in_id_object text, in_arr_val text, in_mode bigint) RETURNS bigint
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
        v_ret   bigint;
BEGIN
    RETURN v_ret + 1;
END;
$$;