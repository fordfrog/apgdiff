
SET search_path = schema_1, pg_catalog;

CREATE TABLE IF NOT EXISTS tb_teste3 (
	id integer,
	description character varying
);

ALTER TABLE tb_teste3 OWNER TO postgres;

CREATE TRIGGER trg_testview_after_insert
	AFTER INSERT ON tb_teste3
	FOR EACH ROW
	EXECUTE PROCEDURE public.fn_trg_testview();
