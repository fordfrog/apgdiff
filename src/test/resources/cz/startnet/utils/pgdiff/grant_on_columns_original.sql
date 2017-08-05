create table todos (
	id integer,
  name text
);

grant select(id, name) on todos to anonymous;

-- Just to make sure it gets ignored
grant webuser to anonymous;
