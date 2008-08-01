CREATE TABLE fax_boxes (
  fax_box_id serial NOT NULL,
  name text,
  CONSTRAINT fax_boxes_pkey PRIMARY KEY (fax_box_id)
);
CREATE TABLE extensions (id serial NOT NULL);
ALTER TABLE fax_boxes OWNER TO postgres;
ALTER TABLE extensions ADD FOREIGN KEY (fax_box_id) REFERENCES fax_boxes
(fax_box_id)    ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE TABLE faxes (
  fax_id serial NOT NULL,
  fax_box_id int4,
  from_name text,
  from_number text,
  status int4, -- 1=pending, 2=failed, 3=received
  pages int4,
  time_received timestamp DEFAULT now(),
  time_finished_received timestamp,
  "read" int2 DEFAULT 0,
  station_id text,
  CONSTRAINT faxes_pkey PRIMARY KEY (fax_id),
  CONSTRAINT faxes_fax_box_id_fkey FOREIGN KEY (fax_box_id)
      REFERENCES fax_boxes (fax_box_id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE CASCADE
);
