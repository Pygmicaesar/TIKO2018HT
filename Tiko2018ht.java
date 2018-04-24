import java.sql.*;

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
        rekisteroi(con, "Atte Asiakas", "atte@asiakas.fi", "esimkatu 1", "123-1234567", "asd123");
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
    }
    
    //Ei toimi
    public static void kirjaudu(Connection con, String as_nimi, String salasana) {
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT as_nimi, salasana"+
                                                            "FROM asiakas"+
                                                            "WHERE as_nimi = ? AND"+
                                                            "salasana = ?");
            prstmt.clearParameters();
            prstmt.setString(1, as_nimi);
            prstmt.setString(2, salasana);
            ResultSet rs = prstmt.executeQuery();
            if (!rs.wasNull()) {
                rs.next();
                rs.getInt("as_id");
            } else {
                System.out.println("Kirjautuminen epäonnistui!");
            }
        } catch (SQLException e) {
            System.err.println(VIRHE + e);
        }
    }
}
