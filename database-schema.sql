DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Todo;
CREATE TABLE User (
    id int NOT NULL AUTO_INCREMENT,  
    cc varchar(200),  
    username varchar(200),
    password varchar(200)
);
CREATE TABLE Todo (
    id int NOT NULL AUTO_INCREMENT,
    content varchar(200)
);
