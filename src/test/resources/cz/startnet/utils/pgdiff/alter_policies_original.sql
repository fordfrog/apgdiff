create table todos (
	 id integer
);

create policy only_owners on todos for SELECT;

create policy no_private on todos for ALL TO anonymous;

create policy only_admins on todos TO admin;

create policy only_managers on todos TO PUBLIC;

create policy only_evens on todos;

create policy only_evens_and_1 on todos using ( (id % 2) = 0 );

create policy only_evens_whitespace_comment on todos using (
  -- comment 1
  (id % 2) = 0
  -- comment 2
);

create policy check_evens_and_1 on todos
with check(
  (id % 2) = 0
);

create policy check_using_evens_and_1 on todos
using (
  (id % 2) = 0
)
with check(
  (id % 2) = 0
);
