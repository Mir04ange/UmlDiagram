import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

interface ITiskarna {
    String getNazev();

    int spocitejCas(int pocetStran);

    int spocitejCenu(int pocetStran);
}
abstract class Tiskarna implements ITiskarna {
    private final String nazev;
    private final int rychlost;
    protected Tiskarna(String nazev, int rychlost) {
        this.nazev = Objects.requireNonNull(nazev, "Název tiskárny nesmí být null.");
        if (rychlost <= 0) {
            throw new IllegalArgumentException("Rychlost musí být kladná.");}
        this.rychlost = rychlost;
    }
    @Override
    public final String getNazev() {
        return nazev;
    }
    @Override
    public final int spocitejCas(int pocetStran) {
        overPocetStran(pocetStran);
        if (pocetStran == 0) {
            return 0;
        }
        return (int) (((long) pocetStran + rychlost - 1) / rychlost);
    }

    protected static void overPocetStran(int pocetStran) {
        if (pocetStran < 0) {
            throw new IllegalArgumentException("Počet stran nesmí být záporný.");
        }
    }

    @Override
    public abstract int spocitejCenu(int pocetStran);
}

final class LaserovaTiskarna extends Tiskarna {
    LaserovaTiskarna(String nazev, int rychlost) {
        super(nazev, rychlost);
    }

    @Override
    public int spocitejCenu(int pocetStran) {
        overPocetStran(pocetStran);
        return Math.multiplyExact(pocetStran, 2);
    }
}

final class InkoustovaTiskarna extends Tiskarna {
    InkoustovaTiskarna(String nazev, int rychlost) {
        super(nazev, rychlost);
    }

    @Override
    public int spocitejCenu(int pocetStran) {
        overPocetStran(pocetStran);
        return Math.multiplyExact(pocetStran, 3);
    }
}

final class PlanTisku {
    private final int pocetStranPrvni;
    private final int pocetStranDruha;
    private final int celkovyCas;
    private final int celkovaCena;

    PlanTisku(int pocetStranPrvni, int pocetStranDruha, int celkovyCas, int celkovaCena) {
        this.pocetStranPrvni = pocetStranPrvni;
        this.pocetStranDruha = pocetStranDruha;
        this.celkovyCas = celkovyCas;
        this.celkovaCena = celkovaCena;
    }

    public int getPocetStranPrvni() {
        return pocetStranPrvni;
    }

    public int getPocetStranDruha() {
        return pocetStranDruha;
    }

    public int getCelkovyCas() {
        return celkovyCas;
    }

    public int getCelkovaCena() {
        return celkovaCena;
    }
}

final class TiskoveCentrum {
    private final List<ITiskarna> tiskarny = new ArrayList<>();

    public void pridejTiskarnu(ITiskarna tiskarna) {
        tiskarny.add(Objects.requireNonNull(tiskarna, "Tiskárna nesmí být null."));
    }


    public ITiskarna najdiNejlevnejsi(int pocetStran, int maxMinut) {
        Tiskarna.overPocetStran(pocetStran);
        if (maxMinut < 0) {
            throw new IllegalArgumentException("Časový limit nesmí být záporný.");
        }

        ITiskarna nejlepsi = null;
        int nejlepsiCena = Integer.MAX_VALUE;
        int nejlepsiCas = Integer.MAX_VALUE;

        for (ITiskarna tiskarna : tiskarny) {
            int cas = tiskarna.spocitejCas(pocetStran);
            if (cas > maxMinut) {
                continue;
            }

            int cena = tiskarna.spocitejCenu(pocetStran);
            if (nejlepsi == null || cena < nejlepsiCena
                    || (cena == nejlepsiCena && cas < nejlepsiCas)) {
                nejlepsi = tiskarna;
                nejlepsiCena = cena;
                nejlepsiCas = cas;
            }
        }
        return nejlepsi;
    }
    public PlanTisku najdiNejlepsiRozdeleni(
            ITiskarna prvni, ITiskarna druha, int pocetStran) {
        Objects.requireNonNull(prvni, "První tiskárna nesmí být null.");
        Objects.requireNonNull(druha, "Druhá tiskárna nesmí být null.");
        Tiskarna.overPocetStran(pocetStran);

        if (prvni == druha) {
            throw new IllegalArgumentException("Je třeba předat dvě různé tiskárny.");
        }

        PlanTisku nejlepsi = null;
        for (int stranPrvni = 0; stranPrvni <= pocetStran; stranPrvni++) {
            int stranDruha = pocetStran - stranPrvni;
            int casPrvni = prvni.spocitejCas(stranPrvni);
            int casDruha = druha.spocitejCas(stranDruha);
            int casDokonceni = Math.max(casPrvni, casDruha);
            int cena = Math.addExact(
                    prvni.spocitejCenu(stranPrvni),
                    druha.spocitejCenu(stranDruha));

            if (nejlepsi == null
                    || casDokonceni < nejlepsi.getCelkovyCas()
                    || (casDokonceni == nejlepsi.getCelkovyCas()
                        && cena < nejlepsi.getCelkovaCena())) {
                nejlepsi = new PlanTisku(stranPrvni, stranDruha, casDokonceni, cena);
            }
        }return nejlepsi;}}

public class Main {
    public static void main(String[] args) {
        TiskoveCentrum centrum = new TiskoveCentrum();
        ITiskarna snek = new LaserovaTiskarna("Šnek", 5);
        ITiskarna turbo = new LaserovaTiskarna("Turbo", 10);
        ITiskarna duhovka = new InkoustovaTiskarna("Duhovka", 20);

        centrum.pridejTiskarnu(snek);
        centrum.pridejTiskarnu(turbo);
        centrum.pridejTiskarnu(duhovka);

        System.out.println("=== 12 STRAN, LIMIT 2 MINUTY ===");
        ITiskarna vybrana = centrum.najdiNejlevnejsi(12, 2);
        if (vybrana == null) {
            System.out.println("Žádná tiskárna nestihne termín.");
        } else {
            System.out.println("Vybraná tiskárna: " + vybrana.getNazev());
            System.out.println("Čas: " + vybrana.spocitejCas(12) + " min");
            System.out.println("Cena: " + vybrana.spocitejCenu(12) + " Kč");
        }

        System.out.println("=== 12 STRAN, LIMIT 0 MINUT ===");
        vybrana = centrum.najdiNejlevnejsi(12, 0);
        if (vybrana == null) {
            System.out.println("Žádná tiskárna nestihne termín.");
        } else {
            System.out.println("Vybraná tiskárna: " + vybrana.getNazev());
            System.out.println("Čas: " + vybrana.spocitejCas(12) + " min");
            System.out.println("Cena: " + vybrana.spocitejCenu(12) + " Kč");
        }

        System.out.println("=== ROZŠÍŘENÍ A: 12 STRAN, LIMIT 10 MINUT ===");
        vybrana = centrum.najdiNejlevnejsi(12, 10);
        if (vybrana != null) {
            System.out.println("Vybraná tiskárna: " + vybrana.getNazev());
            System.out.println("Čas: " + vybrana.spocitejCas(12) + " min");
            System.out.println("Cena: " + vybrana.spocitejCenu(12) + " Kč");
        }

        System.out.println("=== ROZŠÍŘENÍ B: ROZDĚLENÍ MEZI TURBO A DUHOVKU ===");
        PlanTisku plan = centrum.najdiNejlepsiRozdeleni(turbo, duhovka, 12);
        System.out.println("Turbo: " + plan.getPocetStranPrvni() + " stran");
        System.out.println("Duhovka: " + plan.getPocetStranDruha() + " stran");
        System.out.println("Celkový čas: " + plan.getCelkovyCas() + " min");
        System.out.println("Celková cena: " + plan.getCelkovaCena() + " Kč");
    }
}
