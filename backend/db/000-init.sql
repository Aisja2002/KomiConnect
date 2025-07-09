CREATE TABLE user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    data TEXT NOT NULL DEFAULT '{}'
);

CREATE INDEX user_username ON user(username);

CREATE TABLE convention (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    data TEXT NOT NULL DEFAULT '{}'
);

CREATE TABLE label (
    name TEXT PRIMARY KEY
);

CREATE TABLE post (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user INTEGER NOT NULL,
    convention INTEGER NOT NULL,
    label TEXT NOT NULL,
    data TEXT NOT NULL DEFAULT '{}',
    FOREIGN KEY(user) REFERENCES user(id),
    FOREIGN KEY(convention) REFERENCES convention(id),
    FOREIGN KEY(label) REFERENCES label(name)
);

CREATE INDEX post_user ON post(user);
CREATE INDEX post_convention ON post(convention);
CREATE INDEX post_label ON post(label);

CREATE TABLE like (
    user INTEGER NOT NULL,
    post INTEGER NOT NULL,
    PRIMARY KEY(user, post),
    FOREIGN KEY(user) REFERENCES user(id),
    FOREIGN KEY(post) REFERENCES post(id) ON DELETE CASCADE
);

CREATE INDEX like_user ON like(user);
CREATE INDEX like_post ON like(post);

CREATE TABLE favorite (
    user INTEGER NOT NULL,
    post INTEGER NOT NULL,
    PRIMARY KEY(user, post),
    FOREIGN KEY(user) REFERENCES user(id),
    FOREIGN KEY(post) REFERENCES post(id) ON DELETE CASCADE
);

CREATE INDEX favorite_user ON favorite(user);
CREATE INDEX favorite_post ON favorite(post);