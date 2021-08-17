CREATE TABLE IF NOT EXISTS players (
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    user_name               VARCHAR(255)    NOT NULL,
    email                   VARCHAR(255)    NOT NULL,
    password                VARCHAR(255)    NOT NULL,
    active                  BOOLEAN         DEFAULT false,
    locked                  BOOLEAN         DEFAULT false,
    wallet                  VARCHAR(255),
    tag_wallet              VARCHAR(100),
    credits                 BIGINT,
    account_status_code     VARCHAR(10)     DEFAULT '000',
    reset_password_token    VARCHAR(255),
    activation_account_code VARCHAR(255),
    restart_password_code   VARCHAR(255),
    pay_code                VARCHAR(255),
    incorrect_login_counter INTEGER         DEFAULT 0,
    created_at              TIMESTAMP       DEFAULT now(),
    account_block_timestamp TIMESTAMP,
    reset_token_timestamp   TIMESTAMP,
    last_request_timestamp  TIMESTAMP,
    password_timestamp      TIMESTAMP,
    days_to_block           BIGINT,

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS messages (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    user_id                 BIGINT,
    teg                     VARCHAR(100),
    message                 VARCHAR(2048)   NOT NULL,
    number_of_likes         INTEGER DEFAULT 0,
    player_id               BIGINT,

    PRIMARY KEY (id)
--    CONSTRAINT messages_player_FK FOREIGN KEY (player_id) REFERENCES players;
);



CREATE TABLE IF NOT EXISTS messages_likes (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    message_id              BIGINT,
    player_id               BIGINT,

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS roles (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    player_id               BIGINT          NOT NULL,
    role                    VARCHAR(50)     NOT NULL,

    PRIMARY KEY (id)
--    CONSTRAINT role_player_FK FOREIGN KEY (user_id) REFERENCES players (id)
);



CREATE TABLE IF NOT EXISTS arsenal (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    credits                 BIGINT          DEFAULT 0,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS conditions (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    bet                     BIGINT          NOT NULL,
    bias                    INTEGER         DEFAULT 0,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS deposit_of_funds (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    player_id               BIGINT          NOT NULL,
    number_of_coins         BIGINT          NOT NULL,
    coin_name               VARCHAR(100)    NOT NULL,
    tag                     VARCHAR(100)    NOT NULL,
    came_from_wallet        VARCHAR(255)    NOT NULL,
    came_to_wallet          VARCHAR(255)    NOT NULL,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS donations (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    donations               BIGINT          NOT NULL,
    win                     BIGINT          NOT NULL,
    type_win                VARCHAR(100)    NOT NULL,
    total_donations         BIGINT          NOT NULL,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS lotto (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    replenishment           BIGINT          NOT NULL,
    total_lotto_credits     BIGINT          NOT NULL,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS statistics_wins_and_losses (
    id                      BIGINT          AUTO_INCREMENT NOT NULL,
    player_id               BIGINT          NOT NULL,
    bet                     BIGINT          NOT NULL,
    win                     BIGINT          NOT NULL,
    type_bet                VARCHAR(100)    NOT NULL,
    type_win                VARCHAR(100)    NOT NULL,
    created_at              TIMESTAMP       DEFAULT now(),

    PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS challenge_question
(
    id                      BIGINT          AUTO_INCREMENT,
    type                    VARCHAR(50),
    answer                  VARCHAR(255),
    user_id                 BIGINT          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uc_question_per_user UNIQUE (user_id, type),
    CONSTRAINT challenge_question_user_FK FOREIGN KEY (user_id) REFERENCES users (id)
    );