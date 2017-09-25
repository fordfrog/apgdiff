/* Test table */

CREATE table "test1" (
"id" BIGINT,
"value" VARCHAR(255),
test2 TEXT DEFAULT '*/',
test TEXT DEFAULT 'this /*is*/ test' /* in /*line*/ comment */,
test3 TEXT DEFAULT '*/'
);