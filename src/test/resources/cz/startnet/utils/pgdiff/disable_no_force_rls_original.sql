create table items (
	id integer
);


alter table items enable row level security;

create table projects (
	id integer
);


alter table projects force row level security;
