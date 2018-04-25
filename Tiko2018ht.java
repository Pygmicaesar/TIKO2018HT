import java.sql.*;
import java.util.*;

/**
 *
 * @author Joel
 */
public class Tiko2018ht {

    // Default messages used by the program.
    public static final String SYOTAKOMENTO = "Mitä haluaisit tehdä?";
    public static final String TERVETULOA = "Tervetuloa Keijon Keskusdivariin!";
    public static final String UUSIVAIVANHA= "Haluatko kirjautua sisään vai rekisteröityä? kirjaudu / rekisteröidy";
    public static final String NAKEMIIN = "Kiitos käynnistä, näkemiin!";
    public static final String VIRHE = "Tapahtui seuraava virhe: ";

    // Command recognised by the program.
    public static final String KIRJAUDU = "kirjaudu";
    public static final String REKISTEROIDY = "rekisteröidy";
    public static final String HAE = "hae";
    public static final String LISAA = "lisää";
    public static final String POISTA = "poista";
    public static final String OSTOSKORI = "ostoskori";
    public static final String PERUUTA = "peruuta";
    public static final String LOGOUT = "logout";
    public static final String LOPETA = "lopeta";

    //Kirjautuneen käyttäjän as_id
    public static int kirjautunut = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Yhdistetään tietokantaan.
        Connection kdCon = kdConnect();

        // Silmukointiin käytettäviä lippumuuttujia.
        boolean jatkaSuorittamista = true;
        boolean onnistuiko = false;

        // Tulostetaan alkubanneri.
        System.out.println("- - - - - - - - - - - - - - - - -");
        System.out.println("|-------------------------------|");
        System.out.println("|- - - - - - - - - - - - - - - -|");
        System.out.println("|:         K E I J O N         :|");
        System.out.println("|:   K E S K U S D I V A R I   :|");
        System.out.println("|- - - - - - - - - - - - - - - -|");
        System.out.println("|-------------------------------|");
        System.out.println("- - - - - - - - - - - - - - - - -");

        // Tervehditään käyttäjää.
        System.out.println(TERVETULOA);

        do {

            // Silmukka rekisteröitymiseen/kirjautumiseen.
            do {

                Scanner lukija = new Scanner(System.in);
                System.out.println(UUSIVAIVANHA);

                if (lukija.nextLine().equals(KIRJAUDU)) {
                    onnistuiko = kirjaudu(kdCon);
                } else if (lukija.nextLine().equals(REKISTEROIDY)) {
                    onnistuiko = rekisteroi(kdCon);
                } else {
                    System.out.println("Komentoa ei tunnistettu!");
                }

                lukija.close();

            } while(!onnistuiko);

            System.out.println(SYOTAKOMENTO);

        } while(jatkaSuorittamista);

        System.out.println(NAKEMIIN);
    }

    //Yhteyden muodostaminen
    public static Connection kdConnect() {
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

    public static Connection dConnect() {
        Connection con = null;
        String url ="jdbc:postgresql://dbstud2.sis.uta.fi:5432/tiko2018r23?currentSchema=divari";
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

    public static boolean rekisteroi(Connection con) {

        Scanner lukija = new Scanner(System.in);
        boolean onkoNimiOikein = false;
        boolean onkoSpostiOikein = false;
        boolean onkoOsoiteOikein = false;
        boolean onkoSalasanaOikein = false;
        boolean onnistuiko = false;

        // Tiedot
        String as_nimi;
        String sposti;
        String osoite;
        String puhelin;
        String salasana;

        do {

            // Asetetaan käyttäjänimi.
            do {

                System.out.print("Syötä käyttäjänimi: ");
                as_nimi = lukija.nextLine();

                if (as_nimi.equals(PERUUTA)) {
                    return false;
                }

                if (as_nimi != null && as_nimi.length() > 0) {
                    onkoNimiOikein = true;
                } else {
                    System.out.println("Käyttäjänimi on liian lyhyt!");
                }

            } while(!onkoNimiOikein);

            // Asetetaan sähköpostiosoite.
            do {

                System.out.print("Syötä sähköpostiosoite: ");
                sposti = lukija.nextLine();

                if (sposti.equals(PERUUTA)) {
                    return false;
                }

                if (sposti != null && sposti.length() > 0) {
                    onkoSpostiOikein = true;
                } else {
                    System.out.println("Virheellinen sähköpostiosoite!");
                }

            } while(!onkoSpostiOikein);

            // Asetetaan osoite.
            do {

                System.out.print("Syötä osoite: ");
                osoite = lukija.nextLine();

                if (osoite.equals(PERUUTA)) {
                    return false;
                }

                if (osoite != null && osoite.length() > 0) {
                    onkoOsoiteOikein = true;
                } else {
                    System.out.println("Virheellinen osoite!");
                }

            } while(!onkoOsoiteOikein);

            // Asetetaan puhelinnumero.
            System.out.print("Syötä puhelinnumero (valinnainen): ");
            puhelin = lukija.nextLine();

            if (puhelin.equals(PERUUTA)) {
                return false;
            }

            // Asetetaan salasana.
            do {

                System.out.print("Syötä salasana: ");
                salasana = lukija.nextLine();

                if (salasana.equals(PERUUTA)) {
                    return false;
                }

                if (salasana != null && salasana.length() > 6) {
                    onkoSalasanaOikein = true;
                } else {
                    System.out.println("Liian lyhyt salasana (salasanan tulee olla vähintään 7 merkin pituinen)!");
                }

            } while(!onkoSalasanaOikein);

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
                onnistuiko = true;

            } else {
                System.out.println("Käyttäjä on jo olemassa!");
            }

        } while(!onnistuiko);

        lukija.close();
        return true;

    }

    public static boolean kirjaudu(Connection con) {

        Scanner lukija = new Scanner(System.in);
        boolean onnistuiko = false;

        // Tiedot
        String as_nimi;
        String salasana;

        do {

            System.out.print("Anna käyttäjänimi: ");
            as_nimi = lukija.nextLine();

            if (as_nimi.equals(PERUUTA)) {
                return false;
            }

            System.out.print("Anna salasana: ");
            salasana = lukija.nextLine();

            if (salasana.equals(PERUUTA)) {
                return false;
            }

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
                    System.out.println("Kirjautuminen onnistui!");
                    onnistuiko = true;
                } else {
                    System.out.println("Kirjautuminen epäonnistui!");
                }
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }

        } while(!onnistuiko);

        lukija.close();
        return true;

    }

    //Palauttaa LinkedList-olion, joka sisältää hakutuloksen Kirja-olioina
    //Toimii
    public static LinkedList<Nide> hae(Connection con, String haku) {
        LinkedList<Nide> loytyi = new LinkedList();
        Nide n = null;
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT isbn, teos_nimi, tekija, "+
                    "tyyppi, luokka, kpl_id, hinta, paino, "+
                    "FROM teos INNER JOIN teos_kpl "+
                    "ON teos.isbn = teos_kpl.isbn;");
            ResultSet rs = prstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("teos_nimi").contains(haku)) {
                    n = new Nide(rs.getString("isbn"), rs.getString("teos_nimi"),
                            rs.getString("tekija"), rs.getString("tyyppi"),
                            rs.getString("luokka"), rs.getInt("kpl_id"),
                            rs.getDouble("hinta"), rs.getInt("paino"));
                } else if (rs.getString("tekija").contains(haku)) {
                    n = new Nide(rs.getString("isbn"), rs.getString("teos_nimi"),
                            rs.getString("tekija"), rs.getString("tyyppi"),
                            rs.getString("luokka"), rs.getInt("kpl_id"),
                            rs.getDouble("hinta"), rs.getInt("paino"));
                } else if (rs.getString("tyyppi").contains(haku)) {
                    n = new Nide(rs.getString("isbn"), rs.getString("teos_nimi"),
                            rs.getString("tekija"), rs.getString("tyyppi"),
                            rs.getString("luokka"), rs.getInt("kpl_id"),
                            rs.getDouble("hinta"), rs.getInt("paino"));
                } else if (rs.getString("luokka").contains(haku)) {
                    n = new Nide(rs.getString("isbn"), rs.getString("teos_nimi"),
                            rs.getString("tekija"), rs.getString("tyyppi"),
                            rs.getString("luokka"), rs.getInt("kpl_id"),
                            rs.getDouble("hinta"), rs.getInt("paino"));
                }

                if (n != null) {
                    loytyi.add(n);
                }
            }
        } catch (SQLException e) {
            System.out.println(VIRHE + e);
        }
        return loytyi;
    }

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
            System.err.println(VIRHE + e);
        }
        return retVal;
    }

    public static void lisaa(Connection con, String isbn, String teos_nimi, String tekija,
                             String tyyppi, String luokka, double ostohinta, int paino,
                             double hinta, int d_id) {
        if (onkoOlemassa(con, teos_nimi, tekija)) {
            int kpl_id = 0;
            try {
                PreparedStatement prstmt = con.prepareStatement("SELECT MAX(kpl_id) FROM teos_kpl "+
                        "WHERE isbn = ?;");
                prstmt.clearParameters();
                prstmt.setString(1, isbn);
                ResultSet rs = prstmt.executeQuery();
                if (rs.next()) {
                    kpl_id = rs.getInt("max");
                }
                ++kpl_id;
                prstmt = con.prepareStatement("INSERT INTO teos_kpl "+
                        "VALUES (?, ?, ?, ?, null, ?, null, ?);");
                prstmt.clearParameters();
                prstmt.setInt(1, kpl_id);
                prstmt.setString(2, isbn);
                prstmt.setDouble(3, ostohinta);
                prstmt.setInt(4, paino);
                prstmt.setDouble(5, hinta);
                prstmt.setInt(6, d_id);
                int muuttui = prstmt.executeUpdate();
                System.out.println("Rivejä muuttui: " + muuttui);
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }
        } else {
            int kpl_id = 1;
            try {
                PreparedStatement prstmt = con.prepareStatement("INSERT INTO teos "+
                        "VALUES (?, ?, ?, ?, ?)");
                prstmt.clearParameters();
                prstmt.setString(1, isbn);
                prstmt.setString(2, teos_nimi);
                prstmt.setString(3, tekija);
                prstmt.setString(4, tyyppi);
                prstmt.setString(5, luokka);
                int muuttui = prstmt.executeUpdate();

                prstmt = con.prepareStatement("INSERT INTO teos_kpl "+
                        "VALUES (?, ?, ?, ?, null, ?, null, ?)");
                prstmt.clearParameters();
                prstmt.setInt(1, kpl_id);
                prstmt.setString(2, isbn);
                prstmt.setDouble(3, ostohinta);
                prstmt.setInt(4, paino);
                prstmt.setDouble(5, hinta);
                prstmt.setInt(6, d_id);
                muuttui = muuttui + prstmt.executeUpdate();
                System.out.println("Rivejä muuttui: " + muuttui);
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }
        }
    }
}
