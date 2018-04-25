/**
 *
 * @author Joel
 */
public class Nide {
    private String isbn;
    private String teosNimi;
    private String tekija;
    private String tyyppi;
    private String luokka;
    private int kpl_id;
    
    public Nide(String isbn, String teosNimi, String tekija, String tyyppi, String luokka, int kpl_id) {
        this.isbn = isbn;
        this.teosNimi = teosNimi;
        this.tekija = tekija;
        this.tyyppi = tyyppi;
        this.luokka = luokka;
        this.kpl_id = kpl_id;
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
    
    public int kpl_id() {
        return kpl_id;
    }
    
    public void kpl_id(int a) {
        kpl_id = a;
    }
}
