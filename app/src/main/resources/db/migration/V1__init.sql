create table if not exists users ( -- user is a postgres keyword
	id SERIAL PRIMARY KEY,
	username varchar(128) not null,
	password varchar(128) not null
);

CREATE TYPE user_element_enum AS ENUM ('element1', 'element2', 'element3', 'element4', 'element5');
CREATE TABLE user_element (
  id SERIAL PRIMARY KEY,
  elements user_element_enum not null
);

CREATE TYPE user_action_enum AS ENUM ('create', 'read', 'update', 'delete');
CREATE TABLE user_action (
  id SERIAL PRIMARY KEY,
  actions user_action_enum not null
);

CREATE TABLE user_element_action (
  id SERIAL PRIMARY KEY,
  user_id int not null,
  user_element_id int not null,
  user_action_id int not null,
  constraint fk_user_element_id foreign key (user_element_id) REFERENCES user_element (id),
  constraint fk_user_action_id foreign key (user_action_id) REFERENCES user_action (id),
  constraint fk_user_id foreign key (user_id) REFERENCES users (id)
);

insert into users (username, password) values('admin', '$2a$10$quEY4KMHab.Hdyo4//1SgeT8PZiLWAesKmTrFbelvv3Fb6JzpJSaS');

-- Populate all elements.
do $body$
declare
    el user_element_enum;
begin
    <<"FOREACH element">>
    foreach el in array enum_range(NULL::user_element_enum) loop
        insert into user_element (elements) values (el);
    end loop "FOREACH element";
end;
$body$;

-- Populate all actions.
do $body$
declare
    ac user_action_enum;
begin
    <<"FOREACH element">>
    foreach ac in array enum_range(NULL::user_action_enum) loop
        insert into user_action (actions) values (ac);
    end loop "FOREACH element";
end;
$body$;

-- Give admin all permissions (element and action).
do $body$
declare
    elId int not null := 1;
    acId int not null := 1;
begin
    <<"FOREACH element">>
    FOR elId in select user_element.id from user_element loop
         <<"FOREACH action">>
        FOR acId in select user_action.id from user_action loop
           insert into user_element_action (user_id, user_element_id, user_action_id) values (1, elId, acId);
        end loop "FOREACH action";
    end loop "FOREACH element";
end;
$body$;
