/**
 *
 * @author Joel
 */
public class Kirja {
    private String isbn;
    private String teosNimi;
    private String tekija;
    private String tyyppi;
    private String luokka;
    
    public Kirja(String isbn, String teosNimi, String tekija, String tyyppi, String luokka) {
        this.isbn = isbn;
        this.teosNimi = teosNimi;
        this.tekija = tekija;
        this.tyyppi = tyyppi;
        this.luokka = luokka;
    }
    
    public String isbn() {
        return isbn;
    }
    
    public void isbn(String a) {
        isbn = a;
    }
    
    public String teosNimi() {
        return teosNimi;
    }
    
    public void teosNimi(String a) {
        teosNimi = a;
    }
    
    public String tekija() {
        return tekija;
    }
    
    public void tekija(String a) {
        tekija = a;
    }
    public String tyyppi() {
        return tyyppi;
    }
    
    public void tyyppi(String a) {
        tyyppi = a;
    }
    public String luokka() {
        return luokka;
    }
    
    public void luokka(String a) {
        luokka = a;
    }
}
