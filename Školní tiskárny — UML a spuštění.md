# Školní tiskárny — UML a spuštění

## UML diagram

```mermaid
classDiagram
    direction TB
    class ITiskarna {
        <<interface>>
        +getNazev() String
        +spocitejCas(pocetStran) int
        +spocitejCenu(pocetStran) int
    }
    class Tiskarna {
        <<abstract>>
        -nazev: String
        -rychlost: int
        +getNazev() String
        +spocitejCas(pocetStran) int
        +spocitejCenu(pocetStran) int*
    }
    class LaserovaTiskarna {
        +spocitejCenu(pocetStran) int
    }
    class InkoustovaTiskarna {
        +spocitejCenu(pocetStran) int
    }
    class TiskoveCentrum {
        -tiskarny: ArrayList~ITiskarna~
        +pridejTiskarnu(tiskarna) void
        +najdiNejlevnejsi(pocetStran, maxMinut) ITiskarna
        +najdiNejlepsiRozdeleni(prvni, druha, pocetStran) PlanTisku
    }
    class PlanTisku {
        -pocetStranPrvni: int
        -pocetStranDruha: int
        -celkovyCas: int
        -celkovaCena: int
    }

    ITiskarna <|.. Tiskarna
    Tiskarna <|-- LaserovaTiskarna
    Tiskarna <|-- InkoustovaTiskarna
    TiskoveCentrum o-- "0..*" ITiskarna : uchovává
    TiskoveCentrum ..> PlanTisku : vytváří
```

## Spuštění

V adresáři s `Main.java`:

```bash
javac Main.java
java Main
```

Všechny třídy jsou záměrně v jednom zdrojovém souboru, takže lze program snadno zkompilovat jedním příkazem. `TiskoveCentrum` pracuje s tiskárnami pouze přes rozhraní `ITiskarna`.
