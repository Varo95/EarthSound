CREATE TABLE IF NOT EXISTS artist
(
    id          bigint(20)                              NOT NULL AUTO_INCREMENT,
    name        varchar(30) COLLATE utf8mb4_spanish_ci  NOT NULL,
    nationality varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    photoURL    varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS disc
(
    id        bigint(20)                              NOT NULL AUTO_INCREMENT,
    name      varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    photoURL  varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    pub_date  date                                    NOT NULL,
    id_artist bigint(20)                              NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_artist FOREIGN KEY (id_artist) REFERENCES artist (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS genre
(
    id   bigint(20)                              NOT NULL AUTO_INCREMENT,
    name varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS song
(
    id       bigint(20)                              NOT NULL AUTO_INCREMENT,
    name     varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    duration double                                  NOT NULL,
    songURL  varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    id_genre bigint(20)                              NOT NULL,
    n_plays  bigint(20)                              NOT NULL,
    id_disc  bigint(20)                              NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_genre FOREIGN KEY (id_genre) REFERENCES genre (id) ON DELETE NO ACTION ON UPDATE CASCADE,
    CONSTRAINT id_disc FOREIGN KEY (id_disc) REFERENCES disc (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS _user
(
    id       bigint(20)                              NOT NULL AUTO_INCREMENT,
    name     varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    passwd   varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    photoURL varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    disabled tinyint(1)                              NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS playlist
(
    id          bigint(20)                              NOT NULL AUTO_INCREMENT,
    name        varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    description varchar(256) COLLATE utf8mb4_spanish_ci NOT NULL,
    id_ucreator bigint(20)                              NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_ucreator FOREIGN KEY (id_ucreator) REFERENCES _user (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS comment
(
    id          bigint(20)                      NOT NULL AUTO_INCREMENT,
    date        timestamp                       NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    text        text COLLATE utf8mb4_spanish_ci NOT NULL,
    id_user     bigint(20)                      NOT NULL,
    id_playlist bigint(20)                      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_user FOREIGN KEY (id_user) REFERENCES _user (id) ON DELETE NO ACTION ON UPDATE CASCADE,
    CONSTRAINT id_playlist FOREIGN KEY (id_playlist) REFERENCES playlist (id) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS pl_songs
(
    id          bigint(20) NOT NULL AUTO_INCREMENT,
    id_playlist bigint(20) NOT NULL,
    id_song     bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_playlist1 FOREIGN KEY (id_playlist) REFERENCES playlist (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT id_song FOREIGN KEY (id_song) REFERENCES song (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT id_song_id_pl UNIQUE (ID_PLAYLIST, ID_SONG)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;
CREATE TABLE IF NOT EXISTS subscribe
(
    id          bigint(20) NOT NULL AUTO_INCREMENT,
    id_user     bigint(20) NOT NULL,
    id_playlist bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT id_user1 FOREIGN KEY (id_user) REFERENCES _user (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT id_playlist2 FOREIGN KEY (id_playlist) REFERENCES playlist (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_spanish_ci;