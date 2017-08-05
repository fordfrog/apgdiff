create table todos (
	id integer,
  name text
);

grant select(id) on todos to anonymous;

grant all (id, name) on todos to admin;

-- Just to make sure it gets ignored
grant webuser to anonymous;
