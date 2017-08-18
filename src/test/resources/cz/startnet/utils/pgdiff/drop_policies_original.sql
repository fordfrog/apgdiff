create table todos (
	 id integer
);

create policy only_owners on todos;

create schema data;
create table data.sub_tasks (
	 id integer
);

create policy only_owners on data.sub_tasks;

create policy no_private on data.sub_tasks to anonymous, webuser;

create policy only_evens on data.sub_tasks using ( (id % 2) = 0);

create policy check_evens on todos
with check(
  (id % 2) = 0
);

create policy check_using_evens on todos
using (
  (id % 2) = 0
)
with check(
  (id % 2) = 0
);
