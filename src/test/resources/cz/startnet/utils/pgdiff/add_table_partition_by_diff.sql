CREATE TABLE IF NOT EXISTS "procedureresult$Operation" (
	id bigint NOT NULL,
	name character varying(255),
	result_id bigint
) PARTITION BY RANGE(result_id);

ALTER TABLE "procedureresult$Operation" OWNER TO fordfrog;

ALTER TABLE IF EXISTS "procedureresult$Operation"
	ADD CONSTRAINT IF NOT EXISTS $1 FOREIGN KEY (result_id) REFERENCES testtable(field1) ON UPDATE RESTRICT ON DELETE RESTRICT;
