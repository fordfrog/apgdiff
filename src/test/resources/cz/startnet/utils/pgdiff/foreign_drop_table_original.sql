CREATE FOREIGN TABLE foreign_to_drop (
    id bigint
)
SERVER ats
OPTIONS (
    updatable 'false'
);