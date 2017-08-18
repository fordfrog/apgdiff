create table todos (
	id integer,
  name text
);

grant select(id) on todos to anonymous;

grant all (id, name) on todos to admin;

create view todos_view as
  select id, name from todos;

grant select(id) on todos_view to anonymous;
grant all (id, name) on todos_view to admin;



-- Just to make sure it gets ignored
grant webuser to anonymous;
