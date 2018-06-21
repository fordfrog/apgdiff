create table items(id integer, name text);

create view items_view as select id from items;

grant select(id, name), insert(name), update on items_view to admin;

grant select, update(name) on items_view to customer;

grant select(id, name), insert, delete, update(name) on items_view to webuser;
