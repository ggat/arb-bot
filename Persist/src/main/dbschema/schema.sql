CREATE TABLE Person
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);
CREATE TABLE Ranking
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    subject_id INT(11) NOT NULL,
    observer_id INT(11) NOT NULL,
    skill_id INT(11) NOT NULL,
    ranking INT(11) NOT NULL
);
CREATE INDEX Ranking_Person_Observer_id_fk ON Ranking (observer_id);
CREATE INDEX Ranking_Person_Subject_id_fk ON Ranking (subject_id);
CREATE INDEX Ranking_Skill_id_fk ON Ranking (skill_id);
CREATE TABLE Skill
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);