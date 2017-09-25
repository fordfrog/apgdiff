ALTER TABLE agent
	ADD COLUMN abc BIGINT;

COMMENT ON COLUMN agent.abc IS 'This agent supports credit system or not.';