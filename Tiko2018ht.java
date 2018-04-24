import java.sql.*;
import java.util.*;

/**
 *
 * @author Joel
 */
public class Tiko2018ht {

    public static final String VIRHE = "Tapahtui seuraava virhe: ";
    
    //Kirjautuneen käyttäjän as_id
    public static int kirjautunut = 0; 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Testailua
        Connection con = connect();
        rekisteroi(con, "Matti", "asdasd", "asadafdf", "342-2323233", "sipuli");
        kirjaudu(con, "Atte Asiakas", "asd123");
        System.out.println("Kirjautunut: " + kirjautunut);
        kirjaudu(con, "Matti", "sipuli");
        System.out.println("Kirjautunut: " + kirjautunut);
        hae(con, "Turms");
    }
    
	//Yhteyden muodostaminen
    public static Connection connect() {
        Connection con = null;
        String url ="jdbc:postgresql://dbstud2.sis.uta.fi:5432/tiko2018r23?currentSchema=keskusdivari";
	String tunnus = "jr425042";
	String salasana = "123456";
	try {
            con = DriverManager.getConnection(url, tunnus, salasana);
	} catch (SQLException e) {
            System.err.println("Tietokantayhteyden avaus ei onnistu");
            System.err.println(e.getMessage());
	}
	return con;
    }
    
    public static void rekisteroi(Connection con, String as_nimi, String sposti, String osoite, String puhelin, String salasana) {

        int as_id = 0;
        boolean onJoOlemassa = false;

        // Katsotaan, onko käyttäjänimi jo viety.
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT as_nimi FROM asiakas;");
            ResultSet rs = prstmt.executeQuery();
            while(rs.next()) {
                if (as_nimi.equals(rs.getString("as_nimi"))) {
                    onJoOlemassa = true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(VIRHE + e);
        }

        if (!onJoOlemassa) {

            try {
                PreparedStatement prstmt = con.prepareStatement("SELECT MAX(as_id) FROM asiakas;");
                ResultSet rs = prstmt.executeQuery();
                if (rs.next()) {
                    as_id = rs.getInt("max");
                }
                rs.close();
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }

            ++as_id;

            //Muuttuneiden rivien määrä
            int muuttui = 0;
            try {
                PreparedStatement prstmt = con.prepareStatement("INSERT INTO asiakas "+
                                                                "VALUES (?, ?, ?, ?, ?, ?);");
                prstmt.clearParameters();
                prstmt.setInt(1, as_id);
                prstmt.setString(2, as_nimi);
                prstmt.setString(3, sposti);
                prstmt.setString(4, osoite);
                prstmt.setString(5, puhelin);
                prstmt.setString(6, salasana);
                muuttui = prstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }
            System.out.println("Rivejä muuttui: " + muuttui);
            if (muuttui != 0) {
                kirjautunut = as_id;
            }

        } else {
            System.out.println("Käyttäjä on jo olemassa!");
        }
    }
    
    public static void kirjaudu(Connection con, String as_nimi, String salasana) {
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT as_id, as_nimi, salasana "+
                                                            "FROM asiakas "+
                                                            "WHERE as_nimi = ? AND "+
                                                            "salasana = ?");
            prstmt.clearParameters();
            prstmt.setString(1, as_nimi);
            prstmt.setString(2, salasana);
            ResultSet rs = prstmt.executeQuery();
            if (!rs.wasNull()) {
                rs.next();
                kirjautunut = rs.getInt("as_id");
            } else {
                System.out.println("Kirjautuminen epäonnistui!");
            }
        } catch (SQLException e) {
            System.err.println(VIRHE + e);
        }
    }
    
    //Palauttaa LinkedList-olion, joka sisältää hakutuloksen Kirja-olioina
    //Toimii
    public static LinkedList<Kirja> hae(Connection con, String haku) {
        LinkedList<Kirja> loytyi = new LinkedList();
        Kirja k = null;
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT isbn, teos_nimi, tekija, tyyppi, luokka "+
                                                            "FROM teos;");
            ResultSet rs = prstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("teos_nimi").contains(haku)) {
                    k = new Kirja(rs.getString("isbn"), rs.getString("teos_nimi"), rs.getString("tekija"), rs.getString("tyyppi"), rs.getString("luokka"));
                } else if (rs.getString("tekija").contains(haku)) {
                    k = new Kirja(rs.getString("isbn"), rs.getString("teos_nimi"), rs.getString("tekija"), rs.getString("tyyppi"), rs.getString("luokka"));
                } else if (rs.getString("tyyppi").contains(haku)) {
                    k = new Kirja(rs.getString("isbn"), rs.getString("teos_nimi"), rs.getString("tekija"), rs.getString("tyyppi"), rs.getString("luokka"));
                } else if (rs.getString("luokka").contains(haku)) {
                    k = new Kirja(rs.getString("isbn"), rs.getString("teos_nimi"), rs.getString("tekija"), rs.getString("tyyppi"), rs.getString("luokka"));
                }
                
                if (k != null) {
                    loytyi.add(k);
                }
            }
        } catch (SQLException e) {
            System.out.println("Tapahtui virhe: " + e);
        }
        return loytyi;
    }
    
    //Ei toimi
    private static boolean onkoOlemassa(Connection con, String teos_nimi, String tekija) {
        boolean retVal = false;
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT teos_nimi, tekija "+
                                                            "FROM teos;");
            ResultSet rs = prstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("teos_nimi").equals(teos_nimi) &&
                   (rs.getString("tekija").equals(tekija))) {
                        retVal = true;
                }
            } 
        } catch (SQLException e) {
            System.err.println("Haku ei onnistunut: " + e);
        }
        return retVal;
    }
    
    //Kesken
    public static void lisaa(Connection con, String isbn, String teos_nimi, String tekija, String tyyppi, String luokka, int d_id) {
        if (onkoOlemassa(con, teos_nimi, tekija)) {
            try {
                PreparedStatement prstmt = con.prepareStatement("INSERT INTO teos_kpl "+
                                                                "VALUES (?, ?, ?, ?, ?, ?, ?);");
                prstmt.setString(1, isbn);
                prstmt.setString(2, 
                prstmt.setString(3, 
                prstmt.setString(4, 
                prstmt.setString(5, 
                prstmt.setString(6, 
                prstmt.setString(7, 
            }
        }
    }
}