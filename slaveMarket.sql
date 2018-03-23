CREATE SEQUENCE merchandise_id_super;

CREATE TABLE IF NOT EXISTS classes
(
  classname VARCHAR(20) NOT NULL,
  fields    CHARACTER VARYING []
);

CREATE UNIQUE INDEX IF NOT EXISTS table_name_classname_uindex
  ON classes (classname);

CREATE UNIQUE INDEX IF NOT EXISTS table_name_pkey
  ON classes (classname);

ALTER TABLE classes
  ADD CONSTRAINT table_name_pkey
PRIMARY KEY (classname);

CREATE OR REPLACE FUNCTION insert_classes_proc()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER insert_classes_trg
  BEFORE INSERT
  ON classes
  FOR EACH ROW
EXECUTE PROCEDURE insert_classes_proc();

CREATE TABLE IF NOT EXISTS users
(
  id       SERIAL      NOT NULL,
  username VARCHAR(20) NOT NULL,
  password VARCHAR(20) NOT NULL,
  token    VARCHAR,
  balance  INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS users_id_uindex
  ON users (id);

CREATE UNIQUE INDEX IF NOT EXISTS users_pkey
  ON users (id);

ALTER TABLE users
  ADD CONSTRAINT users_pkey
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION insert_user()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER insert_user_trg
  BEFORE INSERT
  ON users
  FOR EACH ROW
EXECUTE PROCEDURE insert_user();

CREATE TABLE IF NOT EXISTS merchandises
(
  id      INTEGER          NOT NULL,
  name    VARCHAR(20)      NOT NULL,
  class   VARCHAR(20)      NOT NULL,
  benefit DOUBLE PRECISION NOT NULL,
  info    VARCHAR          NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS merchandises_id_pk
  ON merchandises (id);

ALTER TABLE merchandises
  ADD CONSTRAINT merchandises_id_pk
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION readonly_trigger_function()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  RAISE EXCEPTION 'The "%" table is read only!', TG_TABLE_NAME
  USING HINT = 'Look at tables that inherit from this table and write to them instead.', ERRCODE ='TA001';
  RETURN NULL;
END;
$$;

CREATE TRIGGER merchandises_readonly_trigger
  BEFORE INSERT OR UPDATE OR DELETE
  ON merchandises
EXECUTE PROCEDURE readonly_trigger_function();

CREATE TABLE IF NOT EXISTS slaves
(
  id     INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL,
  height DOUBLE PRECISION                                            NOT NULL,
  weight DOUBLE PRECISION                                            NOT NULL,
  age    INTEGER                                                     NOT NULL,
  gender VARCHAR DEFAULT 'UNKNOWN' :: CHARACTER VARYING              NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS slave_id_uindex
  ON slaves (id);

CREATE UNIQUE INDEX IF NOT EXISTS slave_pkey
  ON slaves (id);

ALTER TABLE slaves
  ADD CONSTRAINT slave_pkey
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION insert_slave()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER slave_insert
  BEFORE INSERT OR UPDATE
  ON slaves
  FOR EACH ROW
EXECUTE PROCEDURE insert_slave();

CREATE TABLE IF NOT EXISTS aliens
(
  id     INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL,
  planet VARCHAR(20)                                                 NOT NULL,
  weight DOUBLE PRECISION                                            NOT NULL,
  height DOUBLE PRECISION                                            NOT NULL,
  color  VARCHAR(20)                                                 NOT NULL,
  age    INTEGER                                                     NOT NULL,
  race   VARCHAR(20)                                                 NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS table_name_id_uindex
  ON aliens (id);

CREATE UNIQUE INDEX IF NOT EXISTS table_name_pkey1
  ON aliens (id);

ALTER TABLE aliens
  ADD CONSTRAINT table_name_pkey1
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION insert_alien()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER alien_insert
  BEFORE INSERT OR UPDATE
  ON aliens
  FOR EACH ROW
EXECUTE PROCEDURE insert_alien();

CREATE TABLE IF NOT EXISTS poisons
(
  id        INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL,
  onset     VARCHAR(20)                                                 NOT NULL,
  frequency VARCHAR(20)                                                 NOT NULL,
  effect    TEXT                                                        NOT NULL,
  chance    DOUBLE PRECISION                                            NOT NULL,
  weight    DOUBLE PRECISION                                            NOT NULL,
  type      VARCHAR(20)                                                 NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS poison_id_uindex
  ON poisons (id);

CREATE UNIQUE INDEX IF NOT EXISTS poison_pkey
  ON poisons (id);

ALTER TABLE poisons
  ADD CONSTRAINT poison_pkey
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION insert_poison()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER poison_insert
  BEFORE INSERT OR UPDATE
  ON poisons
  FOR EACH ROW
EXECUTE PROCEDURE insert_poison();

CREATE TABLE IF NOT EXISTS food
(
  id          INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL,
  energy      DOUBLE PRECISION                                            NOT NULL,
  weight      DOUBLE PRECISION                                            NOT NULL,
  composition TEXT                                                        NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS food_id_uindex
  ON food (id);

CREATE UNIQUE INDEX IF NOT EXISTS food_pkey
  ON food (id);

ALTER TABLE food
  ADD CONSTRAINT food_pkey
PRIMARY KEY (id);

CREATE OR REPLACE FUNCTION insert_food()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER insert_food_trg
  BEFORE INSERT OR UPDATE
  ON food
  FOR EACH ROW
EXECUTE PROCEDURE insert_food();

CREATE TABLE IF NOT EXISTS deal_states
(
  state VARCHAR NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS deal_states_pkey
  ON deal_states (state);

CREATE UNIQUE INDEX IF NOT EXISTS deal_states_state_uindex
  ON deal_states (state);

ALTER TABLE deal_states
  ADD CONSTRAINT deal_states_pkey
PRIMARY KEY (state);

CREATE TABLE IF NOT EXISTS deals
(
  id      SERIAL      NOT NULL,
  userid  INTEGER     NOT NULL,
  state   VARCHAR(20) NOT NULL,
  time    TIMESTAMP   NOT NULL,
  merchid INTEGER     NOT NULL,
  price   INTEGER     NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS deals_pkey
  ON deals (id);

ALTER TABLE deals
  ADD CONSTRAINT deals_pkey
PRIMARY KEY (id);

ALTER TABLE deals
  ADD CONSTRAINT deals_users_id_fk
FOREIGN KEY (userid) REFERENCES users;

CREATE OR REPLACE FUNCTION insert_deal()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
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

CREATE TRIGGER insert_deal_trg
  BEFORE INSERT
  ON deals
  FOR EACH ROW
EXECUTE PROCEDURE insert_deal();

CREATE TABLE IF NOT EXISTS test
(
  column_1 INTEGER,
  column_2 INTEGER
);

CREATE OR REPLACE FUNCTION login(usernamevar CHARACTER VARYING, passwordvar CHARACTER VARYING)
  RETURNS CHARACTER VARYING
LANGUAGE plpgsql
AS $$
DECLARE
  genarated_token VARCHAR;
  deodedPass      VARCHAR;
BEGIN
  deodedPass := ENCODE(CONVERT_TO(passwordvar, 'UTF-8'), 'base64');
  IF EXISTS(SELECT *
            FROM users
            WHERE username = usernameVar AND password = deodedPass)
  THEN
    IF EXISTS(SELECT token
              FROM users
              WHERE username = usernamevar AND PASSWORD = deodedPass AND users.token IS NULL)
    THEN
      genarated_token := (SELECT md5(random() :: TEXT || clock_timestamp() :: TEXT) :: UUID);
      UPDATE users
      SET token = genarated_token
      WHERE password = deodedPass AND username = usernameVar;
      RETURN genarated_token;
    ELSE
      RAISE EXCEPTION 'user already in'
      USING ERRCODE ='U0003';
    END IF;
  ELSE
    RAISE EXCEPTION 'user not exists'
    USING ERRCODE ='U0004';
  END IF;
  RETURN -1;
END;
$$;

CREATE OR REPLACE FUNCTION logout(usernamevar CHARACTER VARYING, tokenvar CHARACTER VARYING)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION selectaliens(param CHARACTER VARYING)
  RETURNS TABLE(id INTEGER)
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION selectslave(param CHARACTER VARYING)
  RETURNS TABLE(id INTEGER)
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION selectfood(param CHARACTER VARYING)
  RETURNS TABLE(id INTEGER)
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION selectpoisons(CHARACTER VARYING)
  RETURNS TABLE(id INTEGER)
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION searchmerchandise(params CHARACTER VARYING)
  RETURNS SETOF MERCHANDISES
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION buymerchandise(mid INTEGER, userlogin CHARACTER VARYING, usertoken CHARACTER VARYING)
  RETURNS CHARACTER VARYING
LANGUAGE plpgsql
AS $$
DECLARE
  deal deals%ROWTYPE;
BEGIN
  IF EXISTS(SELECT *
            FROM users AS us
            WHERE username LIKE $2 AND token LIKE $3)
  THEN
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
      INSERT INTO deals (userid, state, time, merchid, price) VALUES (deal.userid,
        'sold', now(), $1, deal.price);
      INSERT INTO deals (userid, state, time, merchid, price) VALUES ((SELECT id
                                                                       FROM users
                                                                       WHERE username LIKE $2 AND token LIKE $3),
        'bought', now(), $1, deal.price);
      RETURN (SELECT info
              FROM merchandises
              WHERE id = $1);
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

CREATE OR REPLACE FUNCTION canupdatemerch(mid INTEGER, uname CHARACTER VARYING, utoken CHARACTER VARYING)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION removemerchandise(mid INTEGER, uname CHARACTER VARYING, utoken CHARACTER VARYING)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION updateusername(currentname CHARACTER VARYING, newname CHARACTER VARYING,
                                          curtoken    CHARACTER VARYING)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION updatepassword(currentname CHARACTER VARYING, newpass CHARACTER VARYING,
                                          curtoken    CHARACTER VARYING)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
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

CREATE OR REPLACE FUNCTION getlastdeal(merch INTEGER)
  RETURNS SETOF DEALS
LANGUAGE plpgsql
AS $$
BEGIN
  RETURN QUERY SELECT *
               FROM deals
               WHERE merchid = $1
               ORDER BY id DESC
               LIMIT 1;
END;
$$;

DELETE FROM slaves;
DELETE FROM poisons;
DELETE FROM aliens;
DELETE FROM food;
DELETE FROM deals;

ALTER SEQUENCE merchandise_id_super RESTART 1;
ALTER SEQUENCE deals_id_seq RESTART 1;

DELETE FROM classes *;
DELETE FROM deal_states *;
INSERT INTO classes (classname) VALUES ('slaves'), ('poisons'), ('food'), ('aliens');
INSERT INTO deal_states VALUES ('on sale'), ('bought'), ('sold'), ('removed');
