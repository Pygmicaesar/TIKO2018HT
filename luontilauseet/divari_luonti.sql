CREATE SCHEMA divari;
SET SEARCH_PATH TO divari;

CREATE TABLE divaritieto (
arvo VARCHAR(50) NOT NULL,
tieto VARCHAR(50) NOT NULL,
PRIMARY KEY (arvo));

CREATE TABLE teos (
isbn VARCHAR(13),
teos_nimi VARCHAR(50) NOT NULL,
tekija VARCHAR(50) NOT NULL,
tyyppi VARCHAR(50) NOT NULL,
luokka VARCHAR(50) NOT NULL,
PRIMARY KEY (isbn));

CREATE TABLE teos_kpl (
kpl_id INT,
isbn VARCHAR(50),
ostohinta DECIMAL(4, 2) NOT NULL,
paino INT NOT NULL,
myyntipvm DATE,
hinta DECIMAL(4, 2) NOT NULL,
arvo VARCHAR(50) NOT NULL,
PRIMARY KEY (kpl_id, isbn),
FOREIGN KEY (arvo) REFERENCES divaritieto(arvo),
FOREIGN KEY (isbn) REFERENCES teos(isbn));