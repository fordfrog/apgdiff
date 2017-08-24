
CREATE OR REPLACE FUNCTION multiply_numbers(number1 smallint, number2 smallint) RETURNS smallint
    AS $$
begin
        return number1 * number2;
end;
$$
    LANGUAGE plpgsql;

