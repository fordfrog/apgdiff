create table items(id integer, name text);

grant select(id, name), insert(name), update on items to admin;

grant select, update(name) on items to customer;

grant select(id, name), insert, delete, update(name) on items to webuser;
