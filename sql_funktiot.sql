--- DIVARIN FUNKTIOT JA TRIGGERIT

CREATE FUNCTION maxid(VARCHAR(50))
	RETURNS INT
AS $$
	SET SEARCH_PATH TO keskusdivari;
	SELECT COALESCE(MAX(kpl_id), 0) FROM teos_kpl WHERE isbn = $1;
$$ LANGUAGE SQL;

CREATE FUNCTION paivita_teos() RETURNS TRIGGER AS $paivita_teos_kd$
	BEGIN
		INSERT INTO keskusdivari.teos VALUES (NEW.isbn, NEW.teos_nimi, NEW.tekija, NEW.tyyppi, NEW.luokka);
		RETURN NEW;
	END;
$paivita_teos_kd$ LANGUAGE plpgsql;

CREATE TRIGGER paivita_teos_kd 
AFTER INSERT ON teos 
FOR EACH ROW EXECUTE PROCEDURE paivita_teos();

CREATE FUNCTION paivita_teos_kpl() RETURNS TRIGGER AS $paivita_teos_kpl_kd$
DECLARE
	d_id INT := 0;
	BEGIN
		d_id := (SELECT arvo FROM divaritieto WHERE tieto = 'd_id');
		INSERT INTO keskusdivari.teos_kpl VALUES (maxid(NEW.isbn), NEW.isbn, NEW.ostohinta, NEW.paino, null, NEW.hinta, null, d_id);
		RETURN NEW;
	END;
$paivita_teos_kpl_kd$ LANGUAGE plpgsql;

CREATE TRIGGER paivita_teos_kpl_kd 
AFTER INSERT ON teos_kpl 
FOR EACH ROW EXECUTE PROCEDURE paivita_teos_kpl();

--- KESKUSDIVARIN FUNKTIOT JA TRIGGERIT

CREATE FUNCTION paivita_tkpl_myyntipvm() RETURNS TRIGGER AS $paivita_tkpl_myyntipvm$
	BEGIN
		UPDATE divari.teos_kpl SET myyntipvm = NEW.myyntipvm WHERE isbn = NEW.isbn AND kpl_id = NEW.kpl_id;
		RETURN NEW;
	END;
$paivita_tkpl_myyntipvm$ LANGUAGE plpgsql;

CREATE TRIGGER paivita_tkpl_myyntipvm 
AFTER UPDATE ON teos_kpl
FOR EACH ROW EXECUTE PROCEDURE paivita_tkpl_myyntipvm();
