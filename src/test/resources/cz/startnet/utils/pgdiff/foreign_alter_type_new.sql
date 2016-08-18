CREATE FOREIGN TABLE foreign_to_alter (
    id bigint,
    user_id bigint,
    ref1 character varying(60),
    ref2 character varying(20),
    inplay_stake_coef numeric,
    pre_match_stake_coef numeric,
    punter_limits character varying,
    name character varying(60),
    colour_cat character varying(30),
    deleted numeric(1,0),
    country_code character varying(5)
)
SERVER ats
OPTIONS (
    updatable 'false'
);