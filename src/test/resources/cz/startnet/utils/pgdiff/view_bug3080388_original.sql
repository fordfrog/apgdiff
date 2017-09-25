create table t ( pk serial primary key, t text default '' );
create view v as select * from t;
alter view v alter column t set default '';
