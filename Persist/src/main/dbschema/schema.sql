CREATE TABLE Bookie
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX Bookie_name_uindex ON Bookie (name);
CREATE TABLE CategoryInfo
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    bookie_id INT(11) NOT NULL,
    name VARCHAR(255),
    category_info_id INT(11),
    CONSTRAINT CategoryInfo_Bookie_id_fk FOREIGN KEY (bookie_id) REFERENCES Bookie (id),
    CONSTRAINT CategoryInfo_CategoryInfo_id_fk FOREIGN KEY (category_info_id) REFERENCES CategoryInfo (id)
);
CREATE INDEX CategoryInfo_Bookie_id_fk ON CategoryInfo (bookie_id);
CREATE INDEX CategoryInfo_CategoryInfo_id_fk ON CategoryInfo (category_info_id);
CREATE TABLE Chain
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    data JSON
);