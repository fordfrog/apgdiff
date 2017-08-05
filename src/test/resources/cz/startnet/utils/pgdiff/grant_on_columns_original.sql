create table todos (
	id integer,
  name text
);

grant select(id, name) on todos to anonymous;
