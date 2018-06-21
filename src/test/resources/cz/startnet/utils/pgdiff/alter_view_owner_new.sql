create table items(id integer);

create view items_view as select id from items;
alter view items_view owner to webuser;
