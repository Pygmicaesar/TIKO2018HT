import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 *
 * @author Joel
 */
public class Tiko2018ht {

    // Default messages used by the program.
    public static final String SYOTAKOMENTO = "Mitä haluaisit tehdä?";
    public static final String TERVETULOA = "Tervetuloa Keijon Keskusdivariin!";
    public static final String UUSIVAIVANHA = "Haluatko kirjautua sisään vai rekisteröityä? (k / r) : ";
    public static final String ULOS = "Olet kirjautunut ulos.";
    public static final String NAKEMIIN = "Kiitos käynnistä, näkemiin!";
    public static final String KOMENTOVIRHE = "Komentoa ei tunnistettu!";
    public static final String VIRHE = "Tapahtui seuraava virhe: ";
    public static final String EIOIKEUKSIA = "Ei oikeuksia!";

    // Command recognised by the program.
    public static final String KIRJAUDU = "k";
    public static final String REKISTEROIDY = "r";
    public static final String HAE = "hae";
    public static final String LISAA = "lisää";
    public static final String POISTA = "poista";
    public static final String OSTOSKORI = "ostoskori";
    public static final String PERUUTA = "peruuta";
    public static final String LOGOUT = "logout";
    public static final String LOPETA = "lopeta";

    //Kirjautuneen käyttäjän as_id
    public static int kirjautunut = 0;
    public static ArrayList<TeosKpl> ostoskori = new ArrayList();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        // Yhdistetään tietokantoihin.
        Connection kdCon = kdConnect();
        Connection dCon = dConnect();

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

        // Silmukointiin käytettäviä lippumuuttujia.
        boolean jatkaSuorittamista = true;
        while (jatkaSuorittamista) {
            if (kirjautunut == 0) {
                // Silmukka rekisteröitymiseen/kirjautumiseen.
                System.out.print(UUSIVAIVANHA);
                boolean onnistuiko = false;
                while (!onnistuiko) {
                    String a = sc.nextLine();
                    if (a.equals(KIRJAUDU)) {
                        onnistuiko = kirjaudu(kdCon);
                    } else if (a.equals(REKISTEROIDY)) {
                        onnistuiko = rekisteroi(kdCon);
                    } else {
                        System.out.println("Valitse k tai r!");
                    }
                }
            }
        
            boolean loggedIn = true;

            //Admintila
            if (kirjautunut == 1 || kirjautunut == 2) {
                Connection con = kdCon;
                //Vaihdetaan divari-yhteyteteen mikäli käyttäjänä on divarin admin
                if (kirjautunut == 1) {
                    con = dCon;
                }

                System.out.println(SYOTAKOMENTO);
                System.out.println("OHJE: apua, hae, listaa, lisäätuote, poistakpl, poistatuote, logout");
                while (loggedIn) {
                    String komento = sc.nextLine();
                    boolean syoteOk = false;
                    if (komento.equals("apua")) {
                        aOhje();
                    } else if (komento.equals(HAE)) {
                        ArrayList hakutermit = new ArrayList<String>();
                        String haku = null;
                        while (!syoteOk) {
                            try {
                                System.out.println("Syötä hakutermi: ");
                                haku = sc.nextLine();
                                System.out.println("Anna hakukohteet välilyönnillä erotettuina");
                                System.out.println("(teos_nimi, tekija, tyyppi, luokka)");
                                String[] htermit = sc.nextLine().split(" ");
                                for (int i = 0; i < htermit.length; ++i) {
                                    hakutermit.add(i, htermit[i]);
                                }

                                //Tarkistetaan että annetut hakukohteet ovat "laillisia"
                                String[] kohteet = {"teos_nimi", "tekija", "tyyppi", "luokka"};
                                boolean ok = false;
                                for (int i = 0; i < hakutermit.size(); ++i) {
                                    for (int j = 0; j < kohteet.length; ++j)  {
                                        if (hakutermit.get(i).equals(kohteet[j])) {
                                            ok = true;
                                            syoteOk = true;
                                        }
                                    }
                                    if (!ok) {
                                        throw new Exception();
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Virheellinen syöte!");
                            }
                        }
                        hae(con, haku, (String[])hakutermit.toArray(new String[hakutermit.size()]));
                    } else if (komento.equals("listaa")) {
                        raportti(con);
                    } else if (komento.equals("lisäätuote")) {
                        long isbn = 0;
                        String teos_nimi = null;
                        String tekija = null;
                        String tyyppi = null;
                        String luokka = null;
                        double ostohinta = 0;
                        int paino = 0;
                        double hinta = 0; 
                        while (!syoteOk) {
                            try {
                                System.out.println("Anna teoksen ISBN, tai valitsemasi tunnisteluku mikäli ISBN ei ole saatavilla");
                                isbn = sc.nextLong();
                                sc.nextLine();
                                System.out.println("Anna teoksen nimi");
                                teos_nimi = sc.nextLine();
                                System.out.println("Anna teoksen tekijä");
                                tekija = sc.nextLine();
                                System.out.println("Anna teoksen tyyppi");
                                tyyppi = sc.nextLine();
                                System.out.println("Anna teoksen luokka");
                                luokka = sc.nextLine();
                                System.out.println("Anna teoksen ostohinta");
                                ostohinta = sc.nextDouble();
                                sc.nextLine();
                                System.out.println("Anna teoksen paino grammoissa");
                                paino = sc.nextInt();
                                sc.nextLine();
                                System.out.println("Anna teoksen hinta");
                                hinta = sc.nextDouble();
                                sc.nextLine();
                                if (teos_nimi.length() == 0 ||
                                    tekija.length() == 0 ||
                                    tyyppi.length() == 0 ||
                                    luokka.length() == 0) {
                                        System.out.println("Anna kaikki tiedot!");
                                    } else {
                                        syoteOk = true;
                                    }
                            } catch (Exception e) {
                                System.out.println("Virheellinen syöte!");
                            }
                        }
                        lisaa(con, isbn, teos_nimi, tekija, tyyppi, luokka, ostohinta, paino, hinta, kirjautunut);
                    
                    
                    } else if (komento.equals("poistatuote")) {
                        System.out.println("Anna poistettavan teoksen nimi");
                        String teos_nimi = sc.nextLine();
                        System.out.println("Anna poistettavan teoksen tekijän nimi");
                        String tekija = sc.nextLine();
                        poistaTuote(con, teos_nimi, tekija);
                    } else if (komento.equals("poistakpl")) {
                        System.out.println("Anna poistettavan kappaleen kappale-ID");
                        int kpl_id = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Anna poistettavan kappaleen ISBN");
                        long isbn = sc.nextLong();
                        sc.nextLine();
                        poistaKpl(con, kpl_id, isbn);
                    } else if (komento.equals("logout")) {

                    } else if (komento.equals(LOPETA)) {
                        jatkaSuorittamista = false;
                    } else {
                        System.out.println(KOMENTOVIRHE);
                    }
                    syoteOk = false;
                }
            } else {
                //Käyttäjätila
                
                System.out.println(SYOTAKOMENTO);
                System.out.println("OHJE: apua, hae, listaa, lisää, poista, ostoskori, tilaa, logout");
                while (loggedIn) {
                    String komento = sc.nextLine();
                    if (komento.equals("apua")) {
                        kOhje();
                    } else if (komento.equals(HAE)) {
                        ArrayList hakutermit = new ArrayList<String>();
                        String haku = null;
                        boolean syoteOk = false;
                        while (!syoteOk) {
                            try {
                                System.out.println("Syötä hakutermi: ");
                                haku = sc.nextLine();
                                System.out.println("Anna hakukohteet välilyönnillä erotettuina");
                                System.out.println("(teos_nimi, tekija, tyyppi, luokka)");
                                String[] htermit = sc.nextLine().split(" ");
                                for (int i = 0; i < htermit.length; ++i) {
                                    hakutermit.add(i, htermit[i]);
                                }

                                //Tarkistetaan että annetut hakukohteet ovat "laillisia"
                                String[] kohteet = {"teos_nimi", "tekija", "tyyppi", "luokka"};
                                boolean ok = false;
                                for (int i = 0; i < hakutermit.size(); ++i) {
                                    for (int j = 0; j < kohteet.length; ++j)  {
                                        if (hakutermit.get(i).equals(kohteet[j])) {
                                            ok = true;
                                            syoteOk = true;
                                        }
                                    }
                                    if (!ok) {
                                        throw new Exception();
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Virheellinen syöte!");
                            }
                        }
                        hae(kdCon, haku, (String[])hakutermit.toArray(new String[hakutermit.size()]));
                    } else if (komento.equals("listaa")) {
                        raportti(kdCon);
                    } else if (komento.startsWith(LISAA)) {

                    } else if (komento.startsWith(POISTA)) {

                    } else if (komento.equals(OSTOSKORI)) {

                    } else if (komento.equals(LOGOUT)) {

                        kirjautunut = 0;
                        System.out.println(ULOS);
                    } else if (komento.equals(LOPETA)) {
                        jatkaSuorittamista = false;
                    } else {
                        System.out.println(KOMENTOVIRHE);
                    }
                }
            }
        }

        System.out.println(NAKEMIIN);
    }

    //Tulostaa ohjestuksen adminin työkaluille
    public static void aOhje() {
        System.out.println("apua - tulostaa tämän ohjeviestin");
        System.out.println("hae - pyytää hakutermin ja hakukohteen tai -kohteet");
        System.out.println("listaa - listaa myynnissä olevat tuotteet ja antaa luokkansa kokonais- ja keskihinnat");
        System.out.println("lisäätuote - lisää uusi tuote tai kappale divarin valikoimaan");
        System.out.println("poistatuote - poistaa tuotteen kappaleen divarista");
        System.out.println("logout - kirjaudu ulos");
    }

    //Tulostaa ohjestuksen verkkokaupan käyttöön
    public static void kOhje() {
        System.out.println("apua - tulostaa tämän ohjeviestin");
        System.out.println("hae - pyytää hakutermin ja hakukohteen tai -kohteet");
        System.out.println("listaa - listaa myynnissä olevat tuotteet ja antaa luokkansa kokonais- ja keskihinnat");
        System.out.println("lisää - lisää tuote ostoskoriin tuotteen isbn:än ja ID:n perusteella");
        System.out.println("poista - poista tuote ostoskorista");
        System.out.println("ostoskori - näyttää ostoskorin sisällön");
        System.out.println("tilaa - lähettää tilauksen");
        System.out.println("logout - kirjaudu ulos");
    }

    //Yhteyden muodostaminen
    public static Connection kdConnect() {
        Connection con = null;
		try {
			Class.forName("com.postgresql.jdbc.Driver");
        } catch (Exception e) {
			System.err.println("Drivererror: " + e);
		}
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

                if (sposti != null && sposti.length() > 0 && sposti.contains("@")) {
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
                System.out.println("Kirjautuminen epäonnistui! Tarkista käyttäjänimi ja salasana.");     
            }

        } while(!onnistuiko);

        return true;

    }
    
    //Hakutoiminto, tarjoaa
    public static void hae(Connection con, String haku, String[] hakukohde) {
        LinkedList tulos = new LinkedList<Nide>();
        Nide n = null;
        if (hakukohde.length != 0) {
            try {
                //Haetaan jokaisesta hakukohteesta
                for (int i = 0; i < hakukohde.length; i++) {
                    PreparedStatement prstmt = con.prepareStatement("SELECT teos.isbn, teos_nimi, tekija, " +
                                                                    "tyyppi, luokka, kpl_id, hinta, paino " +
                                                                    "FROM teos INNER JOIN teos_kpl " +
                                                                    "ON teos.isbn = teos_kpl.isbn;");
                    ResultSet rs = prstmt.executeQuery();
                    while (rs.next()) {
                        if (rs.getString(hakukohde[i]).contains(haku)) {
                            n = new Nide(rs.getString("isbn"), rs.getString("teos_nimi"),
                            rs.getString("tekija"), rs.getString("tyyppi"),
                            rs.getString("luokka"), rs.getInt("kpl_id"),
                            rs.getDouble("hinta"), rs.getInt("paino"));
                        }

                        if (n != null && !(tulos.contains(n))) {
                            tulos.add(n);
                        }
                    }
                }
                System.out.println("Kappaleen ID, ISBN, teoksen nimi, tekijä, tyyppi, luokka, hinta, ja paino:");
                for (int i = 0; i < tulos.size(); i++) {
                    System.out.println(tulos.get(i).toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Anna jokin hakukohde!");
        }
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

    private static boolean onkoKplOlemassa(Connection con, int kpl_id, long isbn) {
        boolean retVal = false;
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT * FROM teos_kpl " +
                                                            "WHERE kpl_id = ? AND " +
                                                            "isbn = ?;");
            prstmt.clearParameters();
            prstmt.setInt(1, kpl_id);
            prstmt.setLong(2, isbn);
            ResultSet rs = prstmt.executeQuery();
            if (rs.next()) {
                retVal = true;
            }
        } catch (SQLException e) {
            System.out.println("Kappaletta ei löydy!");
        }
        return retVal;
    }

    public static void poistaKpl (Connection con, int kpl_id, long isbn) {
        if (kirjautunut < 3) {
            if (onkoKplOlemassa(con, kpl_id, isbn)) {
                try {
                    PreparedStatement prstmt = con.prepareStatement("DELETE FROM teos_kpl " +
                                                                    "WHERE kpl_id = ? AND " +
                                                                    "isbn = ?;");
                    prstmt.clearParameters();
                    prstmt.setInt(1, kpl_id);
                    prstmt.setLong(2, isbn);
                    if (prstmt.executeUpdate() > 0) {
                        System.out.println("Poistettu");
                    } else {
                        System.out.println("Poisto ei onnistu!");
                    }
                } catch (SQLException e) {
                    System.out.println("Poisto ei onnistu!");
                }
            }
        } else {
            System.out.println(EIOIKEUKSIA);
        }
    }

    public static void poistaTuote (Connection con, String teos_nimi, String tekija) {
        if (kirjautunut < 3) {
            if (onkoOlemassa(con, teos_nimi, tekija)) {
                try {
                    PreparedStatement prstmt = con.prepareStatement("SELECT isbn FROM teos " +
                                                                    "WHERE teos_nimi = ? AND " +
                                                                    "tekija = ?;");
                    prstmt.clearParameters();
                    prstmt.setString(1, teos_nimi);
                    prstmt.setString(2, tekija);
                    ResultSet rs = prstmt.executeQuery();
                    prstmt = con.prepareStatement("SELECT MAX(kpl_id) FROM teos_kpl " +
                                                  "WHERE isbn = ?;");
                    ResultSet rs2 = prstmt.executeQuery();
                    rs2.next();
                    while (rs.next()) {
                        poistaKpl(con, rs2.getInt("max"), rs.getLong(1));
                    }
                    prstmt = con.prepareStatement("DELETE FROM teos " +
                                                  "WHERE teos_nimi = ? AND " +
                                                  "tekija = ?;");
                    if (prstmt.executeUpdate() != 0) {
                        System.out.println("Poistettu");
                    } else {
                        System.out.println("Poisto ei onnistu!");
                    }
                } catch (SQLException e) {
                    System.out.println("Poisto ei onnistu!");
                }
            } else {
                System.out.println("Teosta ei löydy!");
            }
        } else {
            System.out.println(EIOIKEUKSIA);
        }
    }

    //divari-arvo 1: divarin ylläpitäjä
    //divari-arvo 2: keskusdivarin ylläpitäjä
    public static void lisaa(Connection con, long isbn, String teos_nimi, String tekija,
                             String tyyppi, String luokka, double ostohinta, int paino,
                             double hinta, int d_id) {
        if (kirjautunut < 3) {
            //Mikäli teos on jo olemassa, lisätään uusi kpl
            if (onkoOlemassa(con, teos_nimi, tekija)) {
                int kpl_id = 0;
                try {
                    //Etsitään seuraava vapaa kpl_id
                    PreparedStatement prstmt = con.prepareStatement("SELECT MAX(kpl_id) FROM teos_kpl "+
                            "WHERE isbn = ?;");
                    prstmt.clearParameters();
                    prstmt.setLong(1, isbn);
                    ResultSet rs = prstmt.executeQuery();
                    if (rs.next()) {
                        kpl_id = rs.getInt("max");
                    }
                    ++kpl_id;

                    //Kirjoitetaan tiedot tietokantaan
                    prstmt = con.prepareStatement("INSERT INTO teos_kpl "+
                            "VALUES (?, ?, ?, ?, null, ?, null, ?);");
                    prstmt.clearParameters();
                    prstmt.setInt(1, kpl_id);
                    prstmt.setLong(2, isbn);
                    prstmt.setDouble(3, ostohinta);
                    prstmt.setInt(4, paino);
                    prstmt.setDouble(5, hinta);
                    prstmt.setInt(6, kirjautunut);
                    int muuttui = prstmt.executeUpdate();
                    System.out.println("Päivitetty " + muuttui + " riviä.");
                } catch (SQLException e) {
                    System.err.println("Lisäys epäonnistui!");
                    e.printStackTrace();
                }
            //Mikäli teosta ei vielä ole tietokannassa, luodaan tiedot ja lisätään uusi kpl
            } else {
                int kpl_id = 1;
                try {
                    PreparedStatement prstmt = con.prepareStatement("INSERT INTO teos "+
                            "VALUES (?, ?, ?, ?, ?)");
                    prstmt.clearParameters();
                    prstmt.setLong(1, isbn);
                    prstmt.setString(2, teos_nimi);
                    prstmt.setString(3, tekija);
                    prstmt.setString(4, tyyppi);
                    prstmt.setString(5, luokka);
                    int muuttui = prstmt.executeUpdate();

                    prstmt = con.prepareStatement("INSERT INTO teos_kpl "+
                            "VALUES (?, ?, ?, ?, null, ?, null, ?)");
                    prstmt.clearParameters();
                    prstmt.setInt(1, kpl_id);
                    prstmt.setLong(2, isbn);
                    prstmt.setDouble(3, ostohinta);
                    prstmt.setInt(4, paino);
                    prstmt.setDouble(5, hinta);
                    prstmt.setInt(6, kirjautunut);
                    muuttui = muuttui + prstmt.executeUpdate();
                    System.out.println("Päivitetty " + muuttui + " riviä.");
                } catch (SQLException e) {
                    System.err.println("Lisäys epäonnistui!");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(EIOIKEUKSIA);
        }
    }
    
    //Toimii
    public static void raportti(Connection con) {
        //Etsitään luokat
        ArrayList luokat = new ArrayList();
        ArrayList<RaporttiNide> raportti = new ArrayList();
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT DISTINCT luokka " +
                                                            "FROM teos " + 
                                                            "ORDER BY luokka;");
            ResultSet rs = prstmt.executeQuery();
            while (rs.next()) {
                luokat.add(rs.getString("luokka"));
            }
        } catch (SQLException e) {
            System.err.println(VIRHE + e);
            e.printStackTrace();
        }
        
        //Muodostetaan luokka kerrallaan tiedot,
        for (int i = 0; i < luokat.size(); ++i) {
            try {
                PreparedStatement prstmt = con.prepareStatement("SELECT teos.teos_nimi, " +
                                                                "teos.luokka, " +
                                                                "SUM(teos_kpl.hinta) AS kokonaishinta, " +
                                                                "TRUNC(AVG(teos_kpl.hinta), 2) AS keskihinta " +
                                                                "FROM teos, teos_kpl " +
                                                                "WHERE teos.isbn = teos_kpl.isbn AND " +
                                                                "myyntipvm IS NULL AND " +
                                                                "teos.luokka = '" + luokat.get(i) + "' " +
                                                                "GROUP BY teos.teos_nimi, teos.luokka " +
                                                                "ORDER BY teos.teos_nimi;");
                ResultSet rs = prstmt.executeQuery();
                while (rs.next()) {
                    raportti.add(new RaporttiNide(rs.getString("teos_nimi"),
                                                  rs.getString("luokka"),
                                                  rs.getDouble("kokonaishinta"),
                                                  rs.getDouble("keskihinta")));
                }
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
                e.printStackTrace();
            }
        }
        
        System.out.println("Teoksen nimi, luokka, luokan teosten kokonaishinta ja luokan teosten keskihinta");
        for (int i = 0; i < raportti.size(); i++) {
            System.out.println(raportti.get(i).nimi() + ", " +
                               raportti.get(i).luokka() + ", " +
                               raportti.get(i).kokonaishinta() + "€, " +
                               raportti.get(i).keskihinta() + "€");
        }
    }
    
    public static void lisaaOstoskoriin(Connection con, int kpl_id, long isbn) {
        try {
            PreparedStatement prstmt = con.prepareStatement("SELECT kpl_id, isbn " +
                                                            "FROM teos_kpl " +
                                                            "WHERE kpl_id = ? AND " +
                                                            "isbn = ?;");
            prstmt.clearParameters();
            prstmt.setInt(1, kpl_id);
            prstmt.setLong(2, isbn);
            ResultSet rs = prstmt.executeQuery();
            if (rs.next()) {
                if (!rs.next()) {
                    ostoskori.add(new TeosKpl(kpl_id, isbn));
                } else {
                    System.err.println("Tietokanta epäjohdonmukainen");
                }
            } else {
                System.err.println("Kirjaa ei löydy!");
            }
        } catch (SQLException e) {
            System.err.println(VIRHE + e);
        }
    }
    
    public static void tilaa(Connection con) {
        java.util.Date d = new Date();
        java.sql.Date sqlD = new java.sql.Date(d.getTime());
        for (int i = 0; i < ostoskori.size(); i++) {
            try {
                //Etsitään seuraava vapaa tilaus_id
                int tilaus_id = 0;
                PreparedStatement prstmt = con.prepareStatement("SELECT MAX(tilaus_id) FROM tilaus;");
                ResultSet rs = prstmt.executeQuery();
                if (rs.next()) {
                    tilaus_id = rs.getInt("max");
                }
                ++tilaus_id;

                //Lisätään tilaus tilaus-tauluun
                prstmt = con.prepareStatement("INSERT INTO tilaus VALUES (?, ?, ?)");
                prstmt.clearParameters();
                prstmt.setInt(1, tilaus_id);
                prstmt.setDate(2, sqlD);
                prstmt.setInt(3, kirjautunut);
                if (!prstmt.execute()) {
                    System.out.println("Tilaus epäonnistui");
                }

                //Lisätään myyntipäivämäärä ja tilaus_id myyntikappaleeseen
                prstmt = con.prepareStatement("UPDATE teos_kpl " +
                                              "SET myyntipvm = ?, " +
                                              "tilaus_id = ? " +
                                              "WHERE kpl_id = ? AND " +
                                              "isbn = ?;");
                prstmt.clearParameters();
                prstmt.setDate(1, sqlD);
                prstmt.setInt(2, tilaus_id);
                prstmt.setInt(3, ostoskori.get(i).kplId);
                prstmt.setLong(4, ostoskori.get(i).isbn);
                if (prstmt.executeUpdate() == 0) {
                    System.out.println("Tilaus epäonnistui");
                }
            } catch (SQLException e) {
                System.err.println(VIRHE + e);
            }
        }
    }
}