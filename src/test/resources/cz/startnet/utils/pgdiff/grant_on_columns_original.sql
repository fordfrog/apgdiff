create table todos (
	id integer,
  name text
);

grant select(id, name) on todos to anonymous;

create view todos_view as
  select id, name from todos;

grant select(id, name) on todos_view to anonymous;

-- Just to make sure it gets ignored
grant webuser to anonymous;
