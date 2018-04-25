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
	BEGIN
		INSERT INTO keskusdivari.teos_kpl VALUES (maxid(NEW.isbn), NEW.isbn, NEW.ostohinta, NEW.paino, null, NEW.hinta, null, NEW.d_id);
		RETURN NEW;
	END;
$paivita_teos_kpl_kd$ LANGUAGE plpgsql;

CREATE TRIGGER paivita_teos_kpl_kd 
AFTER INSERT ON teos_kpl 
FOR EACH ROW EXECUTE PROCEDURE paivita_teos_kpl();