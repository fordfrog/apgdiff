
CREATE OR REPLACE FUNCTION return_one() RETURNS integer
    AS $$
begin
	return -1 + 2;
end;
$$
    LANGUAGE plpgsql;

