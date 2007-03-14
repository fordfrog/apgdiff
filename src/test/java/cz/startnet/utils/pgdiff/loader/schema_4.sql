CREATE TABLE call_logs (
    id bigint NOT NULL
);

ALTER TABLE call_logs ALTER COLUMN id SET DEFAULT
nextval('call_logs_id_seq'::regclass);