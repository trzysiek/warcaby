import java.util.Scanner;
import java.util.Arrays; // TODO usunac

public class kamila {
    long bPionki1;
    long bPionki2;
    long czPionki1;
    long czPionki2;

    int wlkPlanszy = 8;
    long ilePionkowNaLonga = 6;
    long ileBitowNaPionka = 9;

    boolean czyBialy(long pionek) {
        // ostatnie 9 bitów 'pionek' opisuje naszego pionka
        return ((pionek >> 6) & 0x1) == 1;
    }

    boolean czyDamka(long pionek) {
        // ostatnie 9 bitów 'pionek' opisuje naszego pionka
        return ((pionek >> 7) & 0x1) == 1;
    }

    boolean czyWGrze(long pionek) {
        // ostatnie 9 bitów 'pionek' opisuje naszego pionka
        return ((pionek >> 8) & 0x1) == 1;
    }

    int pozX(long pionek) {
        return (int)pionek & 0x7;
    }

    int pozY(long pionek) {
        return (int)(pionek >> 3) & 0x7;
    }

    boolean naPlanszy(int x, int y) {
        return x >= 0 && x < wlkPlanszy && y >= 0 && y < wlkPlanszy;
    }

    long pionekNaXY(int x, int y) {
        // zwraca pionka jeśli jest. 0 jeśli nie ma (dobra wartość, bo wtedy czyWGrze(pionek) == false)
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            if (pozX(bPionki1 >> przes) == x && pozY(bPionki1 >> przes) == y && czyWGrze(bPionki1 >> przes))
                return bPionki1 >> przes;
            else if (pozX(bPionki2 >> przes) == x && pozY(bPionki2 >> przes) == y && czyWGrze(bPionki2 >> przes))
                return bPionki2 >> przes;
            else if (pozX(czPionki1 >> przes) == x && pozY(czPionki1 >> przes) == y && czyWGrze(czPionki1 >> przes))
                return czPionki1 >> przes;
            else if (pozX(czPionki2 >> przes) == x && pozY(czPionki2 >> przes) == y && czyWGrze(czPionki2 >> przes))
                return czPionki2 >> przes;
        }
        return 0;
    }

    boolean poleZajete(int x, int y) {
        // sprawdza czy jest pionek bedacy w grze na polu (x, y)
        return czyWGrze(pionekNaXY(x, y));
    }

    boolean czyPionekNaXY(long pionek, int x, int y) {
        // sprawdza czy pionek jest na (x, y) i w grze
        return pozX(pionek) == x && pozY(pionek) == y && czyWGrze(pionek);
    }

    long ustawPozycje(long pionki, long przes, int x, int y) {
        // ustawia pozycje pionka z przesunieciem bitowym 'przes' na (x, y)                
        long maskaPozycji = 0L;
        for (int i = 0; i < 6; ++i)
            maskaPozycji |= 1L << (przes + i);
        
        // najpierw zerujemy bity odpowiadające pozycji (x, y) pionka
        pionki &= ~maskaPozycji; 
        // potem ustawiamy odpowiednie wartości
        pionki |= (long)x << przes;
        pionki |= (long)y << (przes + 3);
        return pionki;
    }

    void zmienPozycjePiona(int x1, int y1, int x2, int y2) {
        // Zakładamy w tym miejscu, że ruch jest w 100% poprawny i możliwy do wykonania.
        // Sprawdzone wcześniej.
        int a[] = {x1, y1, x2, y2};
        System.out.println(Arrays.toString(a));
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;

            if (czyPionekNaXY(bPionki1 >> przes, x1, y1))
                bPionki1 = ustawPozycje(bPionki1, przes, x2, y2);
            else if (czyPionekNaXY(bPionki2 >> przes, x1, y1))
                bPionki2 = ustawPozycje(bPionki2, przes, x2, y2);
            else if (czyPionekNaXY(czPionki1 >> przes, x1, y1))
                czPionki1 = ustawPozycje(czPionki1, przes, x2, y2);
            else if (czyPionekNaXY(czPionki2 >> przes, x1, y1))
                czPionki2 = ustawPozycje(czPionki2, przes, x2, y2);
        }
    }

    void usunPionka(int x, int y) {
        // ustawiamy bit 'czyWGrze' na 0 -> pionek jest zbity.
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            long czyWGrzeBit = (1L << (przes + 8));

            if (czyPionekNaXY(bPionki1 >> przes, x, y))
                bPionki1 &= ~czyWGrzeBit;
            else if (czyPionekNaXY(bPionki2 >> przes, x, y))
                bPionki2 &= ~czyWGrzeBit;
            else if (czyPionekNaXY(czPionki1 >> przes, x, y))
                czPionki1 &= ~czyWGrzeBit;
            else if (czyPionekNaXY(czPionki2 >> przes, x, y))
                czPionki2 &= ~czyWGrzeBit;
        } 
    }

    void ustawPionkaNaBedacegoWGrze(int x, int y, boolean czyBialy) {
        // zmartwychwstaje pionka odpowiedniego koloru na (x, y)
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            long czyWGrzeBit = (1 << (przes + 8));

            if (czyBialy) {
                long p = bPionki1 >> przes;
                if (pozX(p) == x && pozY(p) == y) {
                    bPionki1 |= czyWGrzeBit;
                    return;
                }
                p = bPionki2 >> przes;
                if (pozX(p) == x && pozY(p) == y) {
                    bPionki2 |= czyWGrzeBit;
                    return;
                }
            }
            else {
                long p = czPionki1 >> przes;
                if (pozX(p) == x && pozY(p) == y) {
                    czPionki1 |= czyWGrzeBit;
                    return;
                }
                p = czPionki2 >> przes;
                if (pozX(p) == x && pozY(p) == y) {
                    czPionki2 |= czyWGrzeBit;
                    return;
                }  
            }
        } 
    }
    
    void promujPionaDoDamki(int x, int y) {
        // TODO
    }

    void bijPionaIZaktualizujPlansze(int x1, int y1, int x2, int y2) {
        // Zakładamy w tym miejscu, że bicie jest w 100% poprawne i możliwe do wykonania.
        // Sprawdzone wcześniej.
        zmienPozycjePiona(x1, y1, x2, y2);
        usunPionka((x1 + x2) / 2, (y1 + y2) / 2);
    }

    void cofnijAktualizacjePlanszyPoBiciuPionem(int x1, int y1, int x2, int y2, boolean turaBialego) {
        // przesun pionka z (x2, y2) na (x1, y1) i dodaj pionka pomiędzy
        // Wiemy że dodawany pionek już istnieje, trzeba tylko ustawić mu bit mówiący czy jest w grze.
        zmienPozycjePiona(x2, y2, x1, y1);
        ustawPionkaNaBedacegoWGrze((x1 + x2) / 2, (y1 + y2) / 2, !turaBialego);
    }

    boolean mogeZbicPionem(int x1, int y1, int x2, int y2, boolean turaBialego) {
        if (!naPlanszy(x2, y2))
            return false;
        if (poleZajete(x2, y2))
            return false;

        long bityPionek = pionekNaXY((x1 + x2) / 2, (y1 + y2) / 2);
        return czyWGrze(bityPionek) && czyBialy(bityPionek) != turaBialego;
    }

    boolean nieMaBiciaPionem(int x, int y, boolean turaBialego) {
        return !mogeZbicPionem(x, y, x + 2, y + 2, turaBialego) 
            && !mogeZbicPionem(x, y, x - 2, y + 2, turaBialego)
            && !mogeZbicPionem(x, y, x + 2, y - 2, turaBialego)
            && !mogeZbicPionem(x, y, x - 2, y - 2, turaBialego);
    }

    int pom = 0;

    boolean biciePionem(int x1, int y1, int x2, int y2, boolean turaBialego) {
        // Zwraca true jeśli możliwa jest seria bić pionkiem z (x1, y1) prowadząca do
        // (x2, y2), przy aktualnej planszy (zmieniamy ją w trakcie kolejnych bić i
        // przywracamy do stanu początkowego, jeśli okazało się, że pewna sekwencja bić
        // to ślepy zaułek.
        pom++;
        if (pom == 17)
            return false;
        int a[] = {x1, y1, x2, y2};
        System.out.println(Arrays.toString(a));
        if (nieMaBiciaPionem(x1, y1, turaBialego) && x1 == x2 && y1 == y2) {
            // wykonaliśmy całe poprawne bicie - koniec
            System.out.println("KONIEC");
            return true;
        }

        if (mogeZbicPionem(x1, y1, x1 + 2, y1 + 2, turaBialego)) {
            System.out.println("TAK! W PRAWO W GÓRĘ");
            bijPionaIZaktualizujPlansze(x1, y1, x1 + 2, y1 + 2);
            if (biciePionem(x1 + 2, y1 + 2, x2, y2, turaBialego))
                return true;
            cofnijAktualizacjePlanszyPoBiciuPionem(x1, y1, x1 + 2, y1 + 2, turaBialego);
        }

        if (mogeZbicPionem(x1, y1, x1 + 2, y1 - 2, turaBialego)) {
            System.out.println("TAK! W PRAWO W DÓŁ");
            bijPionaIZaktualizujPlansze(x1, y1, x1 + 2, y1 - 2);
            if (biciePionem(x1 + 2, y1 - 2, x2, y2, turaBialego))
                return true;
            cofnijAktualizacjePlanszyPoBiciuPionem(x1, y1, x1 + 2, y1 - 2, turaBialego);
        }

        if (mogeZbicPionem(x1, y1, x1 - 2, y1 + 2, turaBialego)) {
            System.out.println("TAK! W LEWO W GÓRĘ");
            bijPionaIZaktualizujPlansze(x1, y1, x1 - 2, y1 + 2);
            if (biciePionem(x1 - 2, y1 + 2, x2, y2, turaBialego))
                return true;
            cofnijAktualizacjePlanszyPoBiciuPionem(x1, y1, x1 - 2, y1 + 2, turaBialego);
        }

        if (mogeZbicPionem(x1, y1, x1 - 2, y1 - 2, turaBialego)) {
            System.out.println("TAK! W LEWO W DÓŁ\nPlansza po biciu to:");
            bijPionaIZaktualizujPlansze(x1, y1, x1 - 2, y1 - 2);
            rysujPlansze();
            if (biciePionem(x1 - 2, y1 - 2, x2, y2, turaBialego))
                return true;
            cofnijAktualizacjePlanszyPoBiciuPionem(x1, y1, x1 - 2, y1 - 2, turaBialego);
        }

        return false;
    }

    /*
    boolean probujBicPiona(int x1, int y1, int x2, int y2, int xKoniec, int yKoniec, boolean turaBialego) {
        if (x2 < 0 || x2 >= wlkPlanszy || y2 < 0 || y2 >= wlkPlanszy)
            // poza plansza
            return false;
        int xBityPion = (x1 + x2) / 2;
        int yBityPion = (y1 + y2) / 2;
        if (!poleZajete(xKoniec, yKoniec)) {
            long bityPionek = znajdzPiona(xBityPion, yBityPion);
            if (!czyWGrze(bityPionek) || czyBialy(bityPionek) == turaBialego)
                // nie ma pionka      || jest tego samego koloru co nasz
                return false;
            
            // wszystko sie zgadza - bijemy
            bijPionaIZaktualizujPlansze();


        }
    }*/

    boolean ruchPionem(int x1, int y1, int x2, int y2, boolean turaBialego) {
        // ruch pionkiem z pola (x1, y1) do (x2, y2) - wiemy, że oba pola w zasięgu planszy
        // Zwraca true, jak ruch się powiódł i plansza została zaktualizowana
        // False jak ruch jest błędny. Plansza wtedy pozostaje niezmieniona.
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
            // zwykly ruch o 1 pole, bez bicia
            boolean doDolu = y2 < y1;
            if (doDolu != turaBialego)
                // idziemy w zla strone
                return false;
            if (poleZajete(x2, y2))
                return false;
            
            // TODO jesli mamy bicie, to nie mozemy sie tak ruszyc!
            
            zmienPozycjePiona(x1, y1, x2, y2);
            //if ((turaBialego && x2 == wlkPlanszy - 1) || (!turaBialego && x2 == 0))
            //    promujPionaDoDamki(x2, y2);
            return true;
        }
        else {
            if (Math.abs(dx) % 2 != 0 || Math.abs(dy) % 2 != 0)
                // jak bijemy to zawsze skaczemy o 2
                return false;
            if (dx == 0 && dy == 0)
                return false;
            
            return biciePionem(x1, y1, x2, y2, turaBialego);
        }
    }

    boolean ruchDamka(int x1, int y1, int x2, int y2, boolean turaBialego) {
        // TODO
        return false;
    }

    boolean ruch(int x1, int y1, int x2, int y2, boolean turaBialego) {
        if (!naPlanszy(x1, y1) || !naPlanszy(x2, y2))
            return false;
        
        long pionekStartowy = pionekNaXY(x1, y1);
        if (!czyWGrze(pionekStartowy))
            // nie ma pionka na (x1, y1)
            return false;

        boolean bialy = czyBialy(pionekStartowy);
        if (turaBialego != bialy)
            // pionek jest zlego koloru
            return false;

        if (czyDamka(pionekStartowy)) {
            return ruchDamka(x1, y1, x2, y2, turaBialego);
        }
        else {
            return ruchPionem(x1, y1, x2, y2, turaBialego);
        }
    }

    void ustawStartowePolozenie() {
        long przes = ileBitowNaPionka;
        bPionki1 = 0571L | (0573L << przes) | (0575L << (przes * 2))
            | (0577L << (przes * 3)) | (0560L << (przes * 4)) | (0562L << (przes * 5));
        bPionki2 = 0564L | (0566L << przes) | (0551L << (przes * 2))
        | (0553L << (przes * 3)) | (0555L << (przes * 4)) | (0557L << (przes * 5));
        czPionki1 = 0400L | (0402L << przes) | (0404L << (przes * 2))
            | (0406L << (przes * 3)) | (0411L << (przes * 4)) | (0413L << (przes * 5));
        czPionki2 = 0415L | (0417L << przes) | (0420L << (przes * 2))
            | (0422L << (przes * 3)) | (0424L << (przes * 4)) | (0426L << (przes * 5));
    }

    boolean koniecGry() {
        //TODO implement
        return false;
    }

    /******************************* RYSOWANIE ***************************************
     *********************************************************************************/

    void rysujPionka(long pionek) {
        boolean bialy = czyBialy(pionek);
        boolean damka = czyDamka(pionek);

        // TODO dodac rysowanie unicode + damki
        if (!damka)
            System.out.print(bialy ? "X" : "O");
        else
            System.out.print("Not implemented");
    }

    void rysujPustePole(int x, int y) {
        System.out.print(" ");
    }

    void rysujPlansze() {
        System.out.print(" ");
        for (int i = 1; i <= wlkPlanszy; ++i)
            System.out.print(i);
        System.out.println();
        for (int y = wlkPlanszy - 1; y >= 0; --y) {
            System.out.print(y + 1);
            for (int x = 0; x < wlkPlanszy; ++x) {
                long pionek = pionekNaXY(x, y);
                /*if (y == 0) {
                    System.out.print(x);
                    System.out.println(czyWGrze(pionek));
                }*/
                if (czyWGrze(pionek))
                    rysujPionka(pionek);
                else
                    rysujPustePole(x, y);
            }
            System.out.print(y + 1);
            System.out.println();
        }
        System.out.print(" ");
        for (int i = 1; i <= wlkPlanszy; ++i)
            System.out.print(i);
        System.out.println("\n-------------");
    }

    public static void main(String[] args) {
        kamila gra = new kamila();
        gra.ustawStartowePolozenie();
        boolean turaBialego = true;
        boolean czyRysowacPlansze = true;

        Scanner scan = new Scanner(System.in);
        while (!gra.koniecGry()) {
            if (czyRysowacPlansze)
                gra.rysujPlansze();
            
            int x1 = scan.nextInt();
            int y1 = scan.nextInt();
            int x2 = scan.nextInt();
            int y2 = scan.nextInt();
            if (!gra.ruch(x1 - 1, y1 - 1, x2 - 1, y2 - 1, turaBialego)) {
                System.out.println("Bledny ruch!");
                czyRysowacPlansze = false;
            }
            else {
                turaBialego = !turaBialego;
                czyRysowacPlansze = true;
            }
        }
        scan.close();
    }
}
