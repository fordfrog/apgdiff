CREATE TABLE public."agent"(
  id BIGINT,
  abc BIGINT
);
COMMENT ON COLUMN "agent"."abc" IS 'This agent supports credit system or not.';
COMMENT ON COLUMN "public"."agent"."id" IS 'This ID support schema name';