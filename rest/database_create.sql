DROP DATABASE IF EXISTS warhammer_companion;
CREATE DATABASE warhammer_companion;
\c warhammer_companion

DROP TABLE IF EXISTS figurine CASCADE;

CREATE TABLE figurine(
  id int PRIMARY KEY,
  name varchar,
  points int
);