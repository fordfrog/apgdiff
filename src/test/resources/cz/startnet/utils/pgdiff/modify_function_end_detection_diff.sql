
CREATE OR REPLACE FUNCTION afunction(text, text, numeric) RETURNS numeric AS '
DECLARE
  param1 ALIAS FOR $1;
  param2 ALIAS FOR $2;
  param3 ALIAS FOR $3;
  avg NUMERIC;
BEGIN
  IF param1 = ''value1'' AND param2 = ''value2'' THEN
    IF param3 = 0 THEN
      RETURN -0.35;
      ELSE IF param3 > 60 THEN
        avg = 0;
      ELSE
        avg = 0.29;
      END IF;
    END IF;

    RETURN round(avg, 2);
  END IF;

  IF param1 = ''value1'' AND param2 = ''value2'' THEN
    IF param3 = 0 THEN
      RETURN -0.35;
      ELSE IF param3 > 60 THEN
        avg = 0;
      ELSE
        avg = 0.29;
      END IF;
    END IF;

    RETURN round(avg, 2);
  END IF;

  RAISE EXCEPTION ''No info'';
END;
'
    LANGUAGE plpgsql IMMUTABLE;
