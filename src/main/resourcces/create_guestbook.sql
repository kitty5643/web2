drop table messageTopic;
CREATE TABLE messageTopic (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    username VARCHAR(50),
    title VARCHAR(50),
    message VARCHAR(200),
    category VARCHAR(50),
    date TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE messageReply (
    id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    username VARCHAR(50),
    message VARCHAR(200),
    id_topic INTEGER NOT NULL,
    date TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (id_topic) REFERENCES messageTopic(id)
);


