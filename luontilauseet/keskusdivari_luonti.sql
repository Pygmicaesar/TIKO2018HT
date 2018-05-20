CREATE SCHEMA keskusdivari;
SET SEARCH_PATH TO keskusdivari;

CREATE TABLE divari (
d_id INT,
nimi VARCHAR(50) NOT NULL,
osoite VARCHAR(50) NOT NULL,
PRIMARY KEY (d_id));

CREATE TABLE asiakas (
as_id INT,
as_nimi VARCHAR(50) NOT NULL,
sposti VARCHAR(50) NOT NULL,
osoite VARCHAR(50) NOT NULL,
puhelin VARCHAR(50),
salasana VARCHAR(64), NOT NULL,
PRIMARY KEY (as_id));

CREATE TABLE tilaus (
tilaus_id INT,
pvm DATE NOT NULL,
as_id INT,
PRIMARY KEY (tilaus_id),
FOREIGN KEY (as_id) REFERENCES asiakas(as_id));

CREATE TABLE divaritieto (
arvo VARCHAR(50),
tyyppi VARCHAR(50),
PRIMARY KEY (arvo));

CREATE TABLE teos (
isbn BIGINT NOT NULL,
teos_nimi VARCHAR(50) NOT NULL,
tekija VARCHAR(50) NOT NULL,
tyyppi VARCHAR(50) NOT NULL,
luokka VARCHAR(50) NOT NULL,
arvo VARCHAR(50),
PRIMARY KEY (isbn),
FOREIGN KEY (arvo) REFERENCES divaritieto(arvo));

CREATE TABLE teos_kpl (
kpl_id INT,
isbn BIGINT NOT NULL,
ostohinta DECIMAL(4, 2) NOT NULL,
paino INT NOT NULL,
myyntipvm DATE,
hinta DECIMAL(4, 2) NOT NULL,
tilaus_id INT,
FOREIGN KEY (tilaus_id) REFERENCES tilaus(tilaus_id),
FOREIGN KEY (isbn) REFERENCES teos(isbn),
PRIMARY KEY (kpl_id, isbn));

CREATE TABLE postikulut (
paino INT NOT NULL,
hinta DECIMAL(4, 2) NOT NULL,
PRIMARY KEY (paino));

CREATE TABLE sisaltaa (
tilaus_id INT,
paino INT,
PRIMARY KEY (tilaus_id, paino),
FOREIGN KEY (tilaus_id) REFERENCES tilaus(tilaus_id),
FOREIGN KEY (paino) REFERENCES postikulut(paino));

CREATE FUNCTION paivita_tkpl_myyntipvm() RETURNS TRIGGER AS $paivita_tkpl_myyntipvm$
BEGIN
UPDATE divari.teos_kpl SET myyntipvm = NEW.myyntipvm WHERE isbn = NEW.isbn AND kpl_id = NEW.kpl_id;
RETURN NEW;
END;

CREATE TRIGGER paivita_tkpl_myyntipvm
AFTER UPDATE ON teos_kpl
FOR EACH ROW EXECUTE PROCEDURE paivita_tkpl_myyntipvm();