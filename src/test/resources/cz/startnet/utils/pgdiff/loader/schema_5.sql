CREATE FUNCTION gtsq_in(cstring) RETURNS gtsq
    AS '$libdir/tsearch2', 'gtsq_in'
    LANGUAGE c STRICT;

CREATE FUNCTION multiply_numbers(number1 integer, number2 integer) RETURNS integer
    AS $$
begin
	return number1 * number2;
end;
$$
    LANGUAGE plpgsql STRICT;

CREATE FUNCTION select_something(number1 integer, number2 integer) RETURNS integer
    AS $_$SELECT number1 * number2$_$ LANGUAGE plpgsql;

CREATE FUNCTION select_something2(number1 integer, number2 integer) RETURNS integer
    AS 'SELECT number1 * number2 || \'text\'' LANGUAGE plpgsql;

CREATE FUNCTION select_something3(number1 integer, number2 integer) RETURNS integer
    AS '
SELECT number1 * number2 || \'text\'
' LANGUAGE plpgsql;
