/**
 *
 * @author Joel
 */
public class RaporttiNide {
    private String nimi;
    private String luokka;
    private double kokonaishinta;
    private double keskihinta;
    
    public RaporttiNide(String nimi, String luokka, double kokonaishinta, double keskihinta) {
        this.nimi = nimi;
        this.luokka = luokka;
        this.kokonaishinta = kokonaishinta;
        this.keskihinta = keskihinta;
    }
    
    public String nimi() {
        return nimi;
    }
    
    public void nimi(String nimi) {
        if (nimi != null) {
            this.nimi = nimi;
        }
    }
    
    public String luokka() {
        return luokka;
    }
    
    public void luokka(String luokka) {
        if (luokka != null) {
            this.luokka = luokka;
        }
    }
    
    public double kokonaishinta() {
        return kokonaishinta;
    }
    
    public void kokonaishinta(int khinta) {
        kokonaishinta = khinta;
    }
    
    public double keskihinta() {
        return keskihinta;
    }
    
    public void keskihinta(int khinta) {
        keskihinta = khinta;
    }
    
    
}
