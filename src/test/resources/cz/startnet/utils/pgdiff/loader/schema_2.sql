CREATE TABLE contacts(id int PRIMARY KEY, number_pool_id int, name varchar(50));

CREATE INDEX contacts_number_pool_id_idx ON contacts(number_pool_id);