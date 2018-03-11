------------------------------------------------------------------------
---------------------------Introducing----------------------------------
------------------------------------------------------------------------
-- Please name your database as 'slaveMarket'


CREATE TABLE IF NOT EXISTS classes
(
  classname VARCHAR(20) NOT NULL
    CONSTRAINT table_name_pkey
    PRIMARY KEY
);

CREATE UNIQUE INDEX IF NOT EXISTS table_name_classname_uindex
  ON classes (classname);

INSERT INTO classes VALUES ('slave'), ('food'), ('alien'), ('poison');

-------------------------------------------------------------------------
-----------------------------Users---------------------------------------
-------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users
(
  id       SERIAL      NOT NULL
    CONSTRAINT users_pkey
    PRIMARY KEY,
  username VARCHAR(20) NOT NULL,
  password VARCHAR(20) NOT NULL,
  token    VARCHAR(20)
);

CREATE UNIQUE INDEX IF NOT EXISTS users_id_uindex
  ON users (id);

CREATE OR REPLACE FUNCTION insert_user()
  RETURNS TRIGGER AS $$
DECLARE
  message        VARCHAR;
  exception_hint VARCHAR;
  check_data     BOOLEAN;
BEGIN
  check_data := TRUE;
  IF EXISTS(SELECT *
            FROM users
            WHERE password = NEW.password AND username = NEW.username)
  THEN
    message := 'This username already taken';
    exception_hint := 'Choose another username';
    check_data := FALSE;
  ELSEIF length(NEW.password) < 8
    THEN
      message := 'password is too weak';
      exception_hint := 'use password with more than 8 symbols';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'Can''t add new user because %', message
    USING HINT = exception_hint;
    RETURN NULL;
  END IF;

  RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS insert_user_trg
ON users;
CREATE TRIGGER insert_user_trg
  BEFORE INSERT
  ON users
  FOR EACH ROW
EXECUTE PROCEDURE insert_user();

-------------------------------------------------------------------------
-------------------------Merchandises------------------------------------
-------------------------------------------------------------------------

CREATE SEQUENCE IF NOT EXISTS merchandise_id_super
  START 1;

CREATE TABLE IF NOT EXISTS merchandises
(
  id      INTEGER          NOT NULL
    CONSTRAINT merchandises_pkey
    PRIMARY KEY,
  name    VARCHAR(20)      NOT NULL,
  class   VARCHAR(20)      NOT NULL,
  benefit DOUBLE PRECISION NOT NULL,
  info    JSON             NOT NULL
);


CREATE UNIQUE INDEX IF NOT EXISTS merchandises_id_uindex
  ON merchandises (id);


CREATE TABLE IF NOT EXISTS slaves
(
  id     INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL
    CONSTRAINT slave_pkey
    PRIMARY KEY,
  height DOUBLE PRECISION                                            NOT NULL,
  weight DOUBLE PRECISION                                            NOT NULL,
  age    INTEGER                                                     NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS slave_id_uindex
  ON slaves (id);

CREATE OR REPLACE FUNCTION insert_slave()
  RETURNS TRIGGER AS $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    check_data := FALSE;
  ELSEIF NEW.height <= 0
    THEN
      var := 'height';
      check_data := FALSE;
  ELSEIF NEW.age <= 0
    THEN
      var := 'age';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT = 'Check your input.';
    RETURN NULL;
  END IF;

  NEW.class := 'slave';
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
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS slave_insert
ON slaves;
CREATE TRIGGER slave_insert
  BEFORE INSERT OR UPDATE
  ON slaves
  FOR EACH ROW
EXECUTE PROCEDURE insert_slave();

----------------------------------------------------------------------------------------
--Aliens


CREATE TABLE IF NOT EXISTS aliens
(
  id     INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL
    CONSTRAINT table_name_pkey1
    PRIMARY KEY,
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

CREATE OR REPLACE FUNCTION insert_alien()
  RETURNS TRIGGER AS $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    check_data := FALSE;
  ELSEIF NEW.height <= 0
    THEN
      var := 'height';
      check_data := FALSE;
  ELSEIF NEW.age <= 0
    THEN
      var := 'age';
      check_data := FALSE;
  END IF;
  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT = 'Check your input.';
    RETURN NULL;
  END IF;

  NEW.class := 'alien';
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
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS alien_insert
ON aliens;

CREATE TRIGGER alien_insert
  BEFORE INSERT OR UPDATE
  ON aliens
  FOR EACH ROW
EXECUTE PROCEDURE insert_alien();

------------------------------------------------------------------------------
-- Poisons table

CREATE TABLE IF NOT EXISTS poisons
(
  id        INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL
    CONSTRAINT poison_pkey
    PRIMARY KEY,
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

CREATE OR REPLACE FUNCTION insert_poison()
  RETURNS TRIGGER AS $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    check_data := FALSE;
  ELSEIF NEW.chance <= 0
    THEN
      var := 'chance';
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT = 'Check your input.';
    RETURN NULL;
  END IF;
  NEW.class := 'poison';
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
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS poison_insert
ON poisons;
CREATE TRIGGER poison_insert
  BEFORE INSERT OR UPDATE
  ON poisons
  FOR EACH ROW
EXECUTE PROCEDURE insert_poison();

----------------------------------------------------------------------------
--Food  tables

CREATE TABLE IF NOT EXISTS food
(
  id     INTEGER DEFAULT nextval('merchandise_id_super' :: REGCLASS) NOT NULL
    CONSTRAINT food_pkey
    PRIMARY KEY,
  energy DOUBLE PRECISION                                            NOT NULL,
  weight DOUBLE PRECISION                                            NOT NULL
)
  INHERITS (merchandises);

CREATE UNIQUE INDEX IF NOT EXISTS food_id_uindex
  ON food (id);

CREATE OR REPLACE FUNCTION insert_food()
  RETURNS TRIGGER AS $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
BEGIN
  check_data := TRUE;
  IF NEW.weight <= 0
  THEN
    var := 'weight';
    check_data := FALSE;
  ELSEIF NEW.energy <= 0
    THEN
      var := 'energy';
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT = 'Check your input.';
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
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS insert_food_trg
ON food;

CREATE TRIGGER insert_food_trg
  BEFORE INSERT OR UPDATE
  ON food
  FOR EACH ROW
EXECUTE PROCEDURE insert_food();
-----------------------------------------------------------------------------------------
-- Making merchandises read-only.
CREATE OR REPLACE FUNCTION readonly_trigger_function()
  RETURNS TRIGGER AS $$
BEGIN
  RAISE EXCEPTION 'The "%" table is read only!', TG_TABLE_NAME
  USING HINT = 'Look at tables that inherit from this table and write to them instead.';
  RETURN NULL;
END;
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS merchandises_readonly_trigger
ON merchandises;
CREATE TRIGGER merchandises_readonly_trigger
  BEFORE INSERT OR UPDATE OR DELETE OR TRUNCATE
  ON merchandises
  FOR EACH STATEMENT EXECUTE PROCEDURE readonly_trigger_function();

------------------------------------------------------------------------
------------------------------------------------------
-- Inserts
INSERT INTO slaves (name, height, weight, age) VALUES ('Samantha', 183, 93, 24);
INSERT INTO slaves (name, height, weight, age) VALUES ('Tommy', 170, 74, 23);
INSERT INTO poisons (onset, frequency, effect, chance, weight, type, name)
VALUES ('20 minutes', '3minutes', 'suffocation', 46, .2, 'Respiratory', 'carbon mono oxide');
INSERT INTO poisons (onset, frequency, effect, chance, weight, type, name)
VALUES ('15 minutes', '3 hours', 'heal', 100, 0.2, 'cure', 'weed');
INSERT INTO aliens (planet, weight, height, color, age, race, name)
VALUES ('mars', 114, 169, 'blue', 43, 'Krogan', 'Drack');
INSERT INTO aliens (planet, weight, height, color, age, race, name)
VALUES ('mars', 78, 176, 'blue', 153, 'asari', 'Liara t''sony');
INSERT INTO food (name, energy, weight) VALUES ('bread', 1, 0.2);

------------------------------------------------------
--Deletes

DELETE FROM slaves *;
DELETE FROM poisons *;
DELETE FROM food *;
DELETE FROM aliens *;


ALTER SEQUENCE merchandise_id_super RESTART;
-------------------------------------------------------------------------
-------------------------------Deals-------------------------------------
-------------------------------------------------------------------------

-------------------------------------------------------------------------
--Deal states

CREATE TABLE deal_states
(
  state VARCHAR PRIMARY KEY NOT NULL
);
CREATE UNIQUE INDEX deal_states_state_uindex
  ON deal_states (state);

INSERT INTO deal_states VALUES ('sold'), ('bought'), ('on sale'), ('removed');
------------------------------------------------------------------------
--Deals
CREATE TABLE IF NOT EXISTS deals
(
  id      SERIAL      NOT NULL
    CONSTRAINT deals_pkey
    PRIMARY KEY,
  userid  INTEGER     NOT NULL
    CONSTRAINT deals_users_id_fk
    REFERENCES users,
  state   VARCHAR(20) NOT NULL,
  time    TIMESTAMP   NOT NULL,
  merchid INTEGER     NOT NULL,
  price   INTEGER     NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS deals_id_uindex
  ON deals (id);

CREATE OR REPLACE FUNCTION insert_deal()
  RETURNS TRIGGER AS $$
DECLARE
  var        VARCHAR;
  check_data BOOLEAN;
  exc_hint   VARCHAR;
  dealstates VARCHAR;
BEGIN
  check_data := TRUE;
  IF NOT EXISTS(SELECT *
                FROM merchandises AS merch
                WHERE merch.id = NEW.merchid)
  THEN
    var := 'merchId';
    check_data := FALSE;
    exc_hint := 'use the existing merchandise''s id';
  ELSEIF NOT EXISTS(SELECT *
                    FROM users AS us
                    WHERE us.id = NEW.userid)
    THEN
      var := 'userId';
      check_data := FALSE;
      exc_hint := 'use the existing user''s id';
  ELSEIF NEW.price < 0
    THEN
      var := 'price';
      exc_hint := 'price must be greater than zero';
      check_data := FALSE;
  ELSEIF NOT EXISTS(SELECT *
                    FROM deal_states AS ds
                    WHERE ds.state = NEW.state)
    THEN
      var := 'Unknown deal state';
      SELECT string_agg(ds.state, ', ')
      INTO dealstates
      FROM deal_states AS ds;
      exc_hint := 'use one of this: ' || dealstates || ' instead of ' || NEW.state;
      check_data := FALSE;
  END IF;

  IF check_data <> TRUE
  THEN
    RAISE EXCEPTION 'input for ''%'' value is incorrect', var
    USING HINT = exc_hint;
    RETURN NULL;
  END IF;
  NEW.time = now();
  RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS insert_deal_trg
ON deals;
CREATE TRIGGER insert_deal_trg
  BEFORE INSERT
  ON deals
  FOR EACH ROW
EXECUTE PROCEDURE insert_deal();
