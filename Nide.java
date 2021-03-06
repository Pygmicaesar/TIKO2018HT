import java.util.Comparator;

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
    private double hinta;
    private int paino;
    
    public Nide(String isbn, 
                String teosNimi, 
                String tekija, 
                String tyyppi, 
                String luokka, 
                int kpl_id,
                double hinta,
                int paino) throws IllegalArgumentException {
        
        if (isbn != null &&
            teosNimi != null && 
            tekija != null &&
            tyyppi != null &&
            luokka != null &&
            kpl_id > 0 &&
            hinta > 0 &&
            paino > 0) {
            
            this.isbn = isbn;
            this.teosNimi = teosNimi;
            this.tekija = tekija;
            this.tyyppi = tyyppi;
            this.luokka = luokka;
            this.kpl_id = kpl_id;
            this.hinta = hinta;
            this.paino = paino;
            
        } else {
            
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public String toString() {
        final String D = ", ";
        return new String(isbn + D +teosNimi + D + kpl_id + D + tekija + D + tyyppi + D + luokka + D + hinta + D + paino);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Nide)) {
            return false;
        }

        Nide n = (Nide)o;

        if (this.isbn.equals(n.isbn()) &&
            this.teosNimi.equals(n.teosNimi()) &&
            this.hinta == n.hinta() &&
            this.kpl_id == n.kpl_id() &&
            this.luokka.equals(n.luokka()) &&
            this.paino == n.paino() &&
            this.tekija.equals(n.tekija()) &&
            this.tyyppi.equals(n.tyyppi())) {
                return true;
            } else {
                return false;
            }
    }

    public String isbn() {
        return isbn;
    }
    
    public void isbn(String a) throws IllegalArgumentException {

        if (a != null) {
            isbn = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public String teosNimi() {
        return teosNimi;
    }
    
    public void teosNimi(String a) throws IllegalArgumentException {
        
        if (a != null) {
            teosNimi = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public String tekija() {
        return tekija;
    }
    
    public void tekija(String a) throws IllegalArgumentException {
        
        if (a != null) {
            tekija = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public String tyyppi() {
        return tyyppi;
    }
    
    public void tyyppi(String a) throws IllegalArgumentException {
        
        if (a != null) {
            tyyppi = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public String luokka() {
        return luokka;
    }
    
    public void luokka(String a) throws IllegalArgumentException {
        
        if (a != null) {
            luokka = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public int kpl_id() {
        return kpl_id;
    }
    
    public void kpl_id(int a) throws IllegalArgumentException {
        
        if (a > 0) {
            kpl_id = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public double hinta() {
        return hinta;
    }
    
    public void hinta(double a) throws IllegalArgumentException {
        
        if (a > 0) {
            hinta = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public int paino() {
        return paino;
    }
    
    public void paino(int a) throws IllegalArgumentException {
        
        if (a > 0) {
            paino = a;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
