
CREATE OR REPLACE FUNCTION power_number("input" integer) RETURNS integer
    AS $$
begin
	return input * input;
end;
$$
    LANGUAGE plpgsql;

