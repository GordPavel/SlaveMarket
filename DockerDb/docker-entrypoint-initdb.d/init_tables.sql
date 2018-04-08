-- we don't know how to generate database slaveMarket (class Database) :(
create sequence merchandise_id_super;

create table classes
(
  classname varchar(20) not null,
  fields    character varying [],
  constraint table_name_pkey
  primary key (classname)
);

create unique index table_name_classname_uindex
  on classes (classname);

create table users
(
  id       serial                                      not null,
  username varchar(20)                                 not null,
  password varchar(20)                                 not null,
  token    varchar,
  balance  integer default 0,
  role     varchar default 'user' :: character varying not null,
  constraint users_pkey
  primary key (id)
);

create unique index users_id_uindex
  on users (id);

create table merchandises
(
  id      integer          not null,
  name    varchar(20)      not null,
  class   varchar(20)      not null,
  benefit double precision not null,
  info    varchar          not null,
  image   bytea,
  constraint merchandises_id_pk
  primary key (id)
);

create table slaves
(
  id     integer default nextval('merchandise_id_super' :: regclass) not null,
  height double precision                                            not null,
  weight double precision                                            not null,
  age    integer                                                     not null,
  gender varchar default 'UNKNOWN' :: character varying              not null,
  constraint slave_pkey
  primary key (id)
)
  inherits (merchandises);

create unique index slave_id_uindex
  on slaves (id);

create table aliens
(
  id     integer default nextval('merchandise_id_super' :: regclass) not null,
  planet varchar(20)                                                 not null,
  weight double precision                                            not null,
  height double precision                                            not null,
  color  varchar(20)                                                 not null,
  age    integer                                                     not null,
  race   varchar(20)                                                 not null,
  constraint table_name_pkey1
  primary key (id)
)
  inherits (merchandises);

create unique index table_name_id_uindex
  on aliens (id);

create table poisons
(
  id        integer default nextval('merchandise_id_super' :: regclass) not null,
  onset     varchar(20)                                                 not null,
  frequency varchar(20)                                                 not null,
  effect    text                                                        not null,
  chance    double precision                                            not null,
  weight    double precision                                            not null,
  type      varchar(20)                                                 not null,
  constraint poison_pkey
  primary key (id)
)
  inherits (merchandises);

create unique index poison_id_uindex
  on poisons (id);

create table food
(
  id          integer default nextval('merchandise_id_super' :: regclass) not null,
  energy      double precision                                            not null,
  weight      double precision                                            not null,
  composition text                                                        not null,
  constraint food_pkey
  primary key (id)
)
  inherits (merchandises);

create unique index food_id_uindex
  on food (id);

create table deal_states
(
  state varchar not null,
  constraint deal_states_pkey
  primary key (state)
);

create unique index deal_states_state_uindex
  on deal_states (state);

create table deals
(
  id      serial      not null,
  userid  integer     not null,
  state   varchar(20) not null,
  time    timestamp   not null,
  merchid integer     not null,
  price   integer     not null,
  constraint deals_pkey
  primary key (id),
  constraint deals_users_id_fk
  foreign key (userid) references users
);

create table news
(
  id          serial            not null,
  header      varchar           not null,
  description varchar           not null,
  newstext    text              not null,
  newsimg     bytea,
  slider      boolean default false,
  author      integer default 1 not null,
  constraint news_pkey
  primary key (id)
);

create unique index news_id_uindex
  on news (id);

create table roles
(
  role varchar
);

create unique index roles_role_uindex
  on roles (role);

create or replace function insert_user()
  returns trigger
language plpgsql
as $$
DECLARE
  message    VARCHAR;
  errcode    VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF EXISTS(SELECT *
            FROM users
            WHERE password = ENCODE(CONVERT_TO(NEW.password, 'UTF-8'), 'base64') AND username = NEW.username)
  THEN
    message := 'This username already taken';
    errcode := 'U0001';
    check_data := FALSE;
  ELSEIF length(NEW.password) < 8
    THEN
      message := 'This password is too weak';
      errcode := 'U0002';
      check_data := FALSE;
  ELSEIF length(NEW.username) < 5
    THEN
      message := 'This username is too short';
      errcode := 'U0006';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'Can''t add new user because %', message
    USING ERRCODE = errcode;
    RETURN NULL;
  END IF;
  NEW.password = ENCODE(CONVERT_TO(NEW.password, 'UTF-8'), 'base64');
  RETURN NEW;
END;
$$;

create trigger insert_user_trg
  before insert
  on users
  for each row
execute procedure insert_user();

create or replace function insert_slave()
  returns trigger
language plpgsql
as $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
  code       VARCHAR;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    code := 'I0001';
    check_data := FALSE;
  ELSEIF NEW.height <= 0
    THEN
      var := 'height';
      code := 'I0002';
      check_data := FALSE;
  ELSEIF NEW.age <= 0
    THEN
      var := 'age';
      code := 'I0003';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING ERRCODE = code;
    RETURN NULL;
  END IF;

  NEW.class := 'slaves';
  NEW.benefit := mod(CAST((NEW.height * NEW.weight / NEW.age) AS NUMERIC), 100);
  NEW.info := json_build_object('id', NEW.id,
                                'class', NEW.class,
                                'name', NEW.name,
                                'age', NEW.age,
                                'weight', NEW.weight,
                                'height', NEW.height,
                                'benefit', NEW.benefit);
  RETURN NEW;
END;
$$;

create trigger slave_insert
  before insert or update
  on slaves
  for each row
execute procedure insert_slave();

create or replace function insert_alien()
  returns trigger
language plpgsql
as $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
  code       VARCHAR;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    code := 'I0001';
    check_data := FALSE;
  ELSEIF NEW.height <= 0
    THEN
      var := 'height';
      code := 'I0002';
      check_data := FALSE;
  ELSEIF NEW.age <= 0
    THEN
      var := 'age';
      code := 'I0003';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING ERRCODE = code;
    RETURN NULL;
  END IF;

  NEW.class := 'aliens';
  NEW.benefit := mod(CAST((NEW.height * NEW.weight / NEW.age) AS NUMERIC), 100);
  NEW.info := json_build_object('id', NEW.id,
                                'class', NEW.class,
                                'name', NEW.name,
                                'age', NEW.age,
                                'weight', NEW.weight,
                                'height', NEW.height,
                                'planet', NEW.planet,
                                'color', NEW.color,
                                'race', NEW.race,
                                'benefit', NEW.benefit);
  RETURN NEW;
END;
$$;

create trigger alien_insert
  before insert or update
  on aliens
  for each row
execute procedure insert_alien();

create or replace function insert_poison()
  returns trigger
language plpgsql
as $$
DECLARE
  var        VARCHAR;
  code       VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    code := 'I0001';
    check_data := FALSE;
  ELSEIF NEW.chance <= 0
    THEN
      var := 'chance';
      code := 'I0005';
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING ERRCODE = code;
    RETURN NULL;
  END IF;
  NEW.class := 'poisons';
  NEW.benefit := NEW.chance;
  NEW.info := json_build_object('id', NEW.id,
                                'class', NEW.class,
                                'name', NEW.name,
                                'onset', NEW.onset,
                                'frequency', NEW.FREQUENCY,
                                'effect', NEW.EFFECT,
                                'chance', NEW.chance,
                                'type', NEW.type,
                                'weight', NEW.WEIGHT,
                                'benefit', NEW.benefit);
  RETURN NEW;
END;
$$;

create trigger poison_insert
  before insert or update
  on poisons
  for each row
execute procedure insert_poison();

create or replace function insert_food()
  returns trigger
language plpgsql
as $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
  code       VARCHAR;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    code := 'I0001';
    check_data := FALSE;
  ELSEIF NEW.energy <= 0
    THEN
      var := 'energy';
      code := 'I0004';
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING ERRCODE = code;
    RETURN NULL;
  END IF;
  NEW.class := 'food';
  NEW.benefit := cast(sqrt(power(NEW.weight, 2) * power(NEW.energy, 2)) AS NUMERIC) % 100;
  NEW.info := json_build_object('id', NEW.id,
                                'class', NEW.class,
                                'name', NEW.name,
                                'energy', NEW.energy,
                                'weight', NEW.WEIGHT,
                                'benefit', NEW.benefit);
  RETURN NEW;
END;
$$;

create trigger insert_food_trg
  before insert or update
  on food
  for each row
execute procedure insert_food();

create or replace function readonly_trigger_function()
  returns trigger
language plpgsql
as $$
BEGIN
  RAISE EXCEPTION 'The "%" table is read only!', TG_TABLE_NAME
  USING HINT = 'Look at tables that inherit from this table and write to them instead.', ERRCODE ='TA001';
  RETURN NULL;
END;
$$;

create trigger merchandises_readonly_trigger
  before insert or update or delete
  on merchandises
execute procedure readonly_trigger_function();

create or replace function insert_deal()
  returns trigger
language plpgsql
as $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
  exc_hint   VARCHAR;
  dealstates VARCHAR;
  code       VARCHAR;
BEGIN
  check_data := TRUE;
  IF NOT EXISTS(SELECT *
                FROM merchandises AS merch
                WHERE merch.id = NEW.merchid)
  THEN
    var := 'merchId';
    check_data := FALSE;
    exc_hint := 'use the existing merchandise''s id';
    code := 'I0006';
  ELSEIF NOT EXISTS(SELECT *
                    FROM users AS us
                    WHERE us.id = NEW.userid)
    THEN
      var := 'userId';
      code := 'I0007';
      check_data := FALSE;
      exc_hint := 'use the existing user''s id';
  ELSEIF NEW.price <= 0
    THEN
      var := 'price';
      exc_hint := 'price must be greater than zero';
      code := 'I0008';
      check_data := FALSE;
  ELSEIF NOT EXISTS(SELECT *
                    FROM deal_states AS ds
                    WHERE ds.state = NEW.state)
    THEN
      var := 'Unknown deal state';
      code := 'I0009';
      SELECT string_agg(ds.state, ', ')
      INTO dealstates
      FROM deal_states AS ds;
      exc_hint := 'use one of this: ' || dealstates || ' instead of ' || NEW.state;
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT =exc_hint, ERRCODE = code;
    RETURN NULL;
  END IF;
  NEW.time = now();
  RETURN NEW;
END;
$$;

create trigger insert_deal_trg
  before insert
  on deals
  for each row
execute procedure insert_deal();

create or replace function login(usernamevar character varying, passwordvar character varying)
  returns character varying
language plpgsql
as $$
DECLARE
  genarated_token VARCHAR;
  decodedPass     VARCHAR;
BEGIN
  decodedPass := ENCODE(CONVERT_TO(passwordvar, 'UTF-8'), 'base64');
  IF EXISTS(SELECT *
            FROM users
            WHERE username = usernameVar)
  THEN
    IF EXISTS(SELECT *
              FROM users
              WHERE username = usernameVar AND password = decodedPass)
    THEN
      IF EXISTS(SELECT token
                FROM users
                WHERE username = usernamevar AND PASSWORD = decodedPass AND users.token IS NULL)
      THEN
        genarated_token := (SELECT md5(random() :: TEXT || clock_timestamp() :: TEXT) :: UUID);
        UPDATE users
        SET token = genarated_token
        WHERE password = decodedPass AND username = usernameVar;
        RETURN genarated_token;
      ELSE
        RAISE EXCEPTION 'user already in'
        USING ERRCODE ='U0003';
      END IF;
    ELSE
      RAISE EXCEPTION 'Wrong password'
      USING ERRCODE ='U0008';
    END IF;
  ELSE
    RAISE EXCEPTION 'user not exists'
    USING ERRCODE ='U0004';
  END IF;
  RETURN -1;
END;
$$;

create or replace function logout(usernamevar character varying, tokenvar character varying)
  returns boolean
language plpgsql
as $$
BEGIN
  IF EXISTS(SELECT token
            FROM users
            WHERE username = usernameVar AND token = tokenVar AND token IS NOT NULL)
  THEN
    UPDATE users
    SET token = NULL
    WHERE username = usernameVar AND token = tokenVar;
    RETURN TRUE;
  ELSE
    RAISE EXCEPTION 'user not logged in'
    USING ERRCODE ='U0005';
    RETURN FALSE;
  END IF;

END;
$$;

create or replace function selectaliens(param character varying)
  returns TABLE(id integer)
language plpgsql
as $$
DECLARE
  patt VARCHAR;
BEGIN
  patt := $1 || '%';
  RETURN QUERY SELECT aliens.id
               FROM aliens
               WHERE
                 class ILIKE patt OR name ILIKE patt OR planet ILIKE patt OR race ILIKE patt OR color ILIKE patt;
  RETURN;
END;
$$;

create or replace function selectslave(param character varying)
  returns TABLE(id integer)
language plpgsql
as $$
DECLARE
  patt VARCHAR;
BEGIN
  patt := $1 || '%';
  RETURN QUERY SELECT sl.id
               FROM slaves AS sl
               WHERE name ILIKE patt OR sl.class ILIKE patt OR gender ILIKE patt;
  RETURN;
END;
$$;

create or replace function selectfood(param character varying)
  returns TABLE(id integer)
language plpgsql
as $$
DECLARE
  patt VARCHAR;
BEGIN
  patt := $1 || '%';
  RETURN QUERY SELECT food.id
               FROM food
               WHERE
                 class ILIKE patt OR name ILIKE patt;
  RETURN;
END;
$$;

create or replace function selectpoisons(character varying)
  returns TABLE(id integer)
language plpgsql
as $$
DECLARE
  patt VARCHAR;
BEGIN
  patt := $1 || '%';
  RETURN QUERY SELECT poisons.id
               FROM poisons
               WHERE class ILIKE patt OR name ILIKE patt OR type ILIKE patt;
  RETURN;
END;
$$;

create or replace function searchmerchandise(params character varying)
  returns SETOF merchandises
language plpgsql
as $$
--   RETURNS TABLE(id INT, name VARCHAR, class VARCHAR, benefit FLOAT) AS
BEGIN
  RETURN QUERY SELECT *
               FROM merchandises AS mrc
               WHERE mrc.id IN (
                 (SELECT sa.id
                  FROM selectAliens($1) AS sa)
                 UNION (SELECT ss.id
                        FROM selectSlave($1) AS ss)
                 UNION (SELECT sf.id
                        FROM selectFood($1) AS sf)
                 UNION (SELECT sp.id
                        FROM selectPoisons($1) AS sp)

               ) AND mrc.id NOT IN (SELECT merchid
                                    FROM deals
                                    WHERE state <> 'on sale')
               ORDER BY mrc.id ASC;
END;
$$;

create or replace function insert_classes_proc()
  returns trigger
language plpgsql
as $$
DECLARE
  fields VARCHAR [];
BEGIN
  SELECT string_to_array(string_agg(ti.column_name, ' '), ' ')
  INTO fields
  FROM (SELECT *
        FROM information_schema.columns
        WHERE table_name = NEW.classname) AS ti
  WHERE
    ti.is_nullable = 'NO' AND ti.column_name <> 'id' AND ti.column_name <> 'class' AND ti.column_name <> 'benefit' AND
    ti.column_name <> 'info';
  IF fields IS NOT NULL
  THEN
    NEW.fields = fields;
    RETURN NEW;
  ELSE
    RAISE EXCEPTION 'Can''t find table with name %', NEW.classname
    USING ERRCODE ='CT001';
    RETURN NULL;
  END IF;
END;
$$;

create trigger insert_classes_trg
  before insert
  on classes
  for each row
execute procedure insert_classes_proc();

create or replace function buymerchandise(mid integer, userlogin character varying, usertoken character varying)
  returns character varying
language plpgsql
as $$
DECLARE
  deal    deals%ROWTYPE;
  userRow users%ROWTYPE;
BEGIN
  IF EXISTS(SELECT *
            FROM users AS us
            WHERE username LIKE $2 AND token LIKE $3)
  THEN
    SELECT *
    INTO userRow
    FROM users AS us
    WHERE username LIKE $2 AND token LIKE $3;
    IF (SELECT state
        FROM deals
        WHERE merchid = $1
        ORDER BY id DESC
        LIMIT 1) LIKE 'on sale'
    THEN
      SELECT *
      INTO deal
      FROM deals
      WHERE merchid = $1
      ORDER BY id DESC
      LIMIT 1;
      IF deal.userid <> userRow.id
      THEN
        IF userRow.balance < deal.price
        THEN
          RAISE EXCEPTION 'Not enough money'
          USING ERRCODE ='U0009';
        ELSE
          INSERT INTO deals (userid, state, time, merchid, price) VALUES (deal.userid,
                                                                          'sold', now(), $1, deal.price);
          INSERT INTO deals (userid, state, time, merchid, price) VALUES ((SELECT id
                                                                           FROM users
                                                                           WHERE username LIKE $2 AND token LIKE $3),
                                                                          'bought', now(), $1, deal.price);
          UPDATE users
          SET balance = (userRow.balance - deal.price)
          WHERE id = userRow.id;
          UPDATE users
          set balance = ((select balance
                          from users
                          where id = deal.userid) + deal.price)
          where id = deal.userid;
          RETURN (SELECT info
                  FROM merchandises
                  WHERE id = $1);
        end if;
      ELSE
        RAISE EXCEPTION 'Merchandise on sale by you'
        USING ERRCODE ='BM002';
      end if;
    ELSE
      RAISE EXCEPTION 'Merchandise already bought'
      USING ERRCODE ='BM001';
    END IF;
  ELSE
    RAISE EXCEPTION 'Wrong token'
    USING ERRCODE ='U0007';
  END IF;
END;
$$;

create or replace function canupdatemerch(mid integer, uname character varying, utoken character varying)
  returns boolean
language plpgsql
as $$
DECLARE
  usr users%ROWTYPE;
BEGIN
  IF EXISTS(SELECT *
            FROM users AS us
            WHERE username LIKE $2 AND token LIKE $3)
  THEN
    SELECT *
    INTO usr
    FROM users AS us
    WHERE username LIKE $2 AND token LIKE $3;
    IF (SELECT userid
        FROM deals
        WHERE merchid = $1
              AND state <> 'sold'
        ORDER BY id DESC
        LIMIT 1) = usr.id
    THEN
      RETURN TRUE;
    ELSE RAISE EXCEPTION 'Merchandise not yours'
    USING ERRCODE ='SV001';
    END IF;
  ELSE RAISE EXCEPTION 'Wrong token'
  USING ERRCODE ='U0007';
  END IF;
END;
$$;

create or replace function removemerchandise(mid integer, uname character varying, utoken character varying)
  returns boolean
language plpgsql
as $$
DECLARE
  usr  users%ROWTYPE;
  deal deals%ROWTYPE;
BEGIN
  IF EXISTS(SELECT *
            FROM users AS us
            WHERE username LIKE $2 AND token LIKE $3)
  THEN
    SELECT *
    INTO usr
    FROM users AS us
    WHERE username LIKE $2 AND token LIKE $3;
    SELECT *
    INTO deal
    FROM deals
    WHERE merchid = $1
          AND state <> 'sold'
    ORDER BY id DESC
    LIMIT 1;
    IF deal.userid = usr.id
    THEN
      IF deal.state = 'removed'
      THEN
        RAISE EXCEPTION 'Merchandise already removed'
        USING ERRCODE ='RM001';
      ELSE
        INSERT INTO deals (userid, state, merchid, price) VALUES (usr.id, 'removed', deal.merchid, deal.price);
        RETURN TRUE;
      END IF;
    ELSE RAISE EXCEPTION 'Merchandise not yours'
    USING ERRCODE ='SV001';
    END IF;
  ELSE RAISE EXCEPTION 'Wrong token'
  USING ERRCODE ='U0007';
  END IF;
END;
$$;

create or replace function updateusername(currentname character varying, newname character varying,
                                          curtoken    character varying)
  returns boolean
language plpgsql
as $$
BEGIN
  IF exists(SELECT *
            FROM users
            WHERE username LIKE currentName AND token LIKE $3)
  THEN
    IF EXISTS(SELECT
              FROM users
              WHERE username = $2 AND password LIKE (SELECT password
                                                     FROM users AS us
                                                     WHERE username LIKE $1 AND token LIKE $3))
    THEN
      RETURN FALSE;
    ELSE
      UPDATE users
      SET username = $2
      WHERE username = $1 AND token = $3;
      RETURN TRUE;
    END IF;
  ELSE
    RAISE EXCEPTION 'Wrong token'
    USING ERRCODE ='U0007';
  END IF;
END;
$$;

create or replace function updatepassword(currentname character varying, newpass character varying,
                                          curtoken    character varying)
  returns boolean
language plpgsql
as $$
BEGIN
  IF exists(SELECT *
            FROM users
            WHERE username LIKE $1 AND token LIKE $3)
  THEN
    IF EXISTS(SELECT
              FROM users
              WHERE username = $1 AND password LIKE ENCODE(CONVERT_TO($2, 'UTF-8'), 'base64'))
    THEN
      RETURN FALSE;
    ELSE
      UPDATE users
      SET password = ENCODE(CONVERT_TO($2, 'UTF-8'), 'base64')
      WHERE username = $1 AND token = $3;
      RETURN TRUE;
    END IF;
  ELSE
    RAISE EXCEPTION 'Wrong token'
    USING ERRCODE ='U0007';
  END IF;
END;
$$;

create or replace function getlastdeal(merch integer)
  returns SETOF deals
language plpgsql
as $$
BEGIN
  RETURN QUERY SELECT *
               FROM deals
               WHERE merchid = $1
               ORDER BY id DESC
               LIMIT 1;
END;
$$;


