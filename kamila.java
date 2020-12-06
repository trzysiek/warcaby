import java.io.PrintWriter;
import java.util.Scanner;

public class kamila {
    long bPionki1;
    long bPionki2;
    long czPionki1;
    long czPionki2;

    int wlkPlanszy = 8;
    long ilePionkowNaLonga = 6;
    long ileBitowNaPionka = 9;

    PrintWriter printWriter = new PrintWriter(System.out, true);

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
            long czyWGrzeBit = 1L << (przes + 8);

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

    // TODO mozliwy bug. Nie patrzymy czy przywracamy damke czy pionka.
    // Moga byc obydwa zbite na tym samym polu!!!
    void ustawPionkaNaBedacegoWGrze(int x, int y, boolean czyBialy) {
        // zmartwychwstaje pionka odpowiedniego koloru na (x, y)
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            long czyWGrzeBit = (1L << (przes + 8));

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
        System.out.println("PROMUJEMY!");
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            long czyDamkaBit = 1L << (przes + 7);

            long p = bPionki1 >> przes;
            if (pozX(p) == x && pozY(p) == y && czyWGrze(p)) {
                bPionki1 |= czyDamkaBit;
                return;
            }
            p = bPionki2 >> przes;
            if (pozX(p) == x && pozY(p) == y && czyWGrze(p)) {
                bPionki2 |= czyDamkaBit;
                return;
            }
            p = czPionki1 >> przes;
            if (pozX(p) == x && pozY(p) == y && czyWGrze(p)) {
                czPionki1 |= czyDamkaBit;
                return;
            }
            p = czPionki2 >> przes;
            if (pozX(p) == x && pozY(p) == y && czyWGrze(p)) {
                czPionki2 |= czyDamkaBit;
                return;
            }
        }
    }

    boolean mamyBicie(boolean turaBialego) {
        long pionki1;
        long pionki2;
        if (turaBialego) {
            pionki1 = bPionki1;
            pionki2 = bPionki2;
        }
        else {
            pionki1 = czPionki1;
            pionki2 = czPionki2;
        }
        for (int i = 0; i < ilePionkowNaLonga; ++i) {
            long przes = ileBitowNaPionka * i;
            
            long p = pionki1 >> przes;
            if (czyWGrze(p)) {
                if (czyDamka(p) && !nieMaBiciaDamka(pozX(p), pozY(p), turaBialego))
                    return true;
                if (!czyDamka(p) && !nieMaBiciaPionem(pozX(p), pozY(p), turaBialego))
                    return true;
            }
            p = pionki2 >> przes;
            if (czyWGrze(p)) {
                if (czyDamka(p) && !nieMaBiciaDamka(pozX(p), pozY(p), turaBialego))
                    return true;
                if (!czyDamka(p) && !nieMaBiciaPionem(pozX(p), pozY(p), turaBialego))
                    return true;
            }
        }
        return false;
    }

    /**
     * Ruszam się z (x1, y1) na (x2, y2). Biję piona z pozycji (bityX, bityY).
     * Zakładamy będąc w tej funkcji, że bicie jest w 100% poprawne i
     * możliwe do wykonania. Sprawdzone wcześniej.
     */
    void bijPionaIZaktualizujPlansze(int x1, int y1, int x2, int y2, int bityX, int bityY) {
        // Zakładamy w tym miejscu, że bicie jest w 100% poprawne i możliwe do wykonania.
        // Sprawdzone wcześniej.
        zmienPozycjePiona(x1, y1, x2, y2);
        usunPionka(bityX, bityY);
    }

    /** Cofam się z (x2, y2) na (x1, y1). Wstawiam pionka na (bityX, bityY).
    *   Wiem, że ten pionek już tam istnieje, trzeba tylko mu ustawić bit że jest w grze. 
    */
    // TODO bug nie rozrozniamy miedzy pionem a damka. Mozemy ustawic bledny typ pionka
    void cofnijAktualizacjePlanszyPoBiciu(int x1, int y1, int x2, int y2,
            int bityX, int bityY, boolean turaBialego) {
        // przesun pionka z (x2, y2) na (x1, y1) i dodaj pionka pomiędzy
        // Wiemy że dodawany pionek już istnieje, trzeba tylko ustawić mu bit mówiący czy jest w grze.
        zmienPozycjePiona(x2, y2, x1, y1);
        ustawPionkaNaBedacegoWGrze(bityX, bityY, !turaBialego);
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

    boolean biciePionem(int x1, int y1, int x2, int y2, boolean turaBialego) {
        // Zwraca true jeśli możliwa jest seria bić pionkiem z (x1, y1) prowadząca do
        // (x2, y2), przy aktualnej planszy (zmieniamy ją w trakcie kolejnych bić i
        // przywracamy do stanu początkowego, jeśli okazało się, że pewna sekwencja bić
        // to ślepy zaułek.
        if (nieMaBiciaPionem(x1, y1, turaBialego) && x1 == x2 && y1 == y2) {
            if ((turaBialego && y2 == 0) || (!turaBialego && y2 == wlkPlanszy - 1))
                promujPionaDoDamki(x2, y2);
            return true;
        }

        for (int dx = -2; dx <= 2; dx += 4) {
            for (int dy = -2; dy <= 2; dy += 4) {
                if (mogeZbicPionem(x1, y1, x1 + dx, y1 + dy, turaBialego)) {
                    //System.out.println("TAK! W PRAWO W GÓRĘ");
                    bijPionaIZaktualizujPlansze(x1, y1, x1 + dx, y1 + dy, x1 + dx / 2, y1 + dy / 2);
                    if (biciePionem(x1 + dx, y1 + dy, x2, y2, turaBialego))
                        return true;
                    cofnijAktualizacjePlanszyPoBiciu(
                        x1, y1, x1 + dx, y1 + dy, x1 + dx / 2, y1 + dy / 2, turaBialego
                    );
                }
            }
        }
        return false;
    }

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
            
            if (mamyBicie(turaBialego)) {
                // TODO jak bicie damka to tez.
                // TODO jak kazdym innym pionem bicie, to tez :(
                printWriter.println("Masz możliwe bicie. Musisz go użyć.");
                return false;
            }
            
            zmienPozycjePiona(x1, y1, x2, y2);
            if ((turaBialego && y2 == 0) || (!turaBialego && y2 == wlkPlanszy - 1))
                promujPionaDoDamki(x2, y2);
            return true;
        }
        else {
            if (Math.abs(dx) % 2 != 0 || Math.abs(dy) % 2 != 0)
                // jak bijemy to zawsze skaczemy o 2
                return false;
            if (dx == 0 && dy == 0)
                return false;
            // DODAC PROMOWANIE PRZY BICIU
            return biciePionem(x1, y1, x2, y2, turaBialego);
        }
    }

    /**
     * true <=> 2 pola są na 1 przekątnej && nie ma nic po drodze
     */
    boolean mogeRuszycDamkaBezBicia(int x1, int y1, int x2, int y2) {
        if (Math.abs(x2 - x1) != Math.abs(y2 - y1))
            return false;

        int dx = (x2 > x1) ? 1 : -1;
        int dy = (y2 > y1) ? 1 : -1;
        int aktX = x1 + dx;
        int aktY = y1 + dy;
        while (aktX != x2) {
            if (poleZajete(aktX, aktY))
                return false;
            aktX += dx;
            aktY += dy;
        }
        return !poleZajete(aktX, aktY);
    }

    /**
     * dx, dy = [+-]1. Oznacza kierunek.
     * Zwraca -1 jak nie ma możliwego bicia w tym kierunku.
     * Zwraca n, jeśli jest bicie, a najbliższy pionek, który bijemy, jest
     * odległy o n pól.
     */
    int mogeZbicDamka(int x, int y, int dx, int dy, boolean turaBialego) {
        //System.out.printf("Zaczynam mogeZbicDamka, dx=%d, dy=%d\n", dx,dy);
        int aktX = x;
        int aktY = y;
        for (int i = 1; i <= wlkPlanszy; ++i) {
            aktX += dx;
            aktY += dy;
            if (!naPlanszy(aktX, aktY))
                return -1;
            //System.out.printf("aktX=%d, aktY=%d\n",aktX+1,aktY+1);
            long pionek = pionekNaXY(aktX, aktY);
            if (czyWGrze(pionek)) {
                //System.out.printf("Mam pionka: (%d, %d). czyBialy(pionek): %b, turaBialego: %b\n", aktX+1,aktY+1,czyBialy(pionek), turaBialego);
                if (czyBialy(pionek) == turaBialego)
                    // pierwszy pionek w tym kierunku jest tego samego koloru co ruch. Zle.
                    return -1;
                else if (naPlanszy(aktX + dx, aktY + dy) && !poleZajete(aktX + dx, aktY + dy)) {
                    //System.out.printf("WTF PRZECIEZ DZIALA: %d %d to: %d\n", aktX, x, (dx == 1) ? (aktX - x) : (x - aktX));
                    return (dx == 1) ? (aktX - x) : (x - aktX);
                }
                else {
                    //System.out.printf("WTF? Jak to nie moge sie ruszyc\n");
                    return -1;
                }
            }
        }
        return -1;
    }

    boolean nieMaBiciaDamka(int x, int y, boolean turaBialego) {
        return mogeZbicDamka(x, y, 1, 1, turaBialego) == -1
            && mogeZbicDamka(x, y, 1, -1, turaBialego) == -1
            && mogeZbicDamka(x, y, -1, 1, turaBialego) == -1
            && mogeZbicDamka(x, y, -1, -1, turaBialego) == -1;
    }

    boolean bicieDamka(int x1, int y1, int x2, int y2, boolean turaBialego) {
        if (nieMaBiciaDamka(x1, y1, turaBialego) && x1 == x2 && y1 == y2)
            return true;
        
        System.out.printf("bicieDamka: z (%d, %d) na (%d, %d). Jest bicie/nie jestem na koncowej pozycji.\n", x1+1,y1+1,x2+1,y2+1);
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                // ruszam się w kierunku (dx, dy)

                int oIlePol = mogeZbicDamka(x1, y1, dx, dy, turaBialego);
                System.out.printf("Kierunek (%d, %d). OIlePol: %d\n", dx,dy,oIlePol);
                // -1 jeśli nie mogę zbić w tym kierunku
                // n jeśli biję pionka odległego o n pól
                // Mogę wtedy stanąć na polach w tym samym kierunku odległych o (n+1), (n+2), ...
                // dopóki wszystkie są wolne. Jeśli jest zajęte przez pionka przeciwnego koloru,
                // to teoretycznie mogłabym to zbić, ale rozbijam to na 2 ruchy, bo będzie prościej.
                if (oIlePol == -1)
                    continue;
                
                for (int i = 1; i < wlkPlanszy; ++i) {
                    int skaczemyX = x1 + dx * (oIlePol + i);
                    int skaczemyY = y1 + dy * (oIlePol + i);
                    if (!naPlanszy(skaczemyX, skaczemyY) || poleZajete(skaczemyX, skaczemyY))
                        break;
                    
                    bijPionaIZaktualizujPlansze(x1, y1, skaczemyX, skaczemyY, x1 + dx * oIlePol, y1 + dy * oIlePol);
                    if (bicieDamka(skaczemyX, skaczemyY, x2, y2, turaBialego))
                        return true;
                    cofnijAktualizacjePlanszyPoBiciu(
                        x1, y1, skaczemyX, skaczemyY, x1 + dx * oIlePol, y1 + dy * oIlePol, turaBialego
                    );
                }
            }
        }
        return false;
    }

    boolean ruchDamka(int x1, int y1, int x2, int y2, boolean turaBialego) {
        System.out.printf("%d %d %d %d\n", x1,y1,x2,y2);
        if (x1 == x2 && y1 == y2)
            return false;
        if (nieMaBiciaDamka(x1, y1, turaBialego)) {
            System.out.printf("nie ma bicia? co?\n");
            if (mogeRuszycDamkaBezBicia(x1, y1, x2, y2)) {
                if (mamyBicie(turaBialego)) {
                    printWriter.println("Mamy bicie do wykonania! Trzeba je zrobić!");
                    return false;
                }
                zmienPozycjePiona(x1, y1, x2, y2);
                return true;
            }
            else
                return false;
        }
        else
            return bicieDamka(x1, y1, x2, y2, turaBialego);
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

    boolean koniecGry() {
        return false;
    }

    void ustawStartowePolozenie() {
        long przes = ileBitowNaPionka;
        czPionki1 = 0400L | (0402L << przes) | (0404L << (przes * 2))
            | (0406L << (przes * 3)) | (0411L << (przes * 4)) | (0413L << (przes * 5));
        czPionki2 = 0415L | (0417L << przes) | (0420L << (przes * 2))
            | (0422L << (przes * 3)) | (0424L << (przes * 4)) | (0426L << (przes * 5));
        bPionki1 = 0571L | (0573L << przes) | (0575L << (przes * 2))
            | (0577L << (przes * 3)) | (0560L << (przes * 4)) | (0562L << (przes * 5));
        bPionki2 = 0564L | (0566L << przes) | (0551L << (przes * 2))
            | (0553L << (przes * 3)) | (0555L << (przes * 4)) | (0557L << (przes * 5));
    }

    /******************************* RYSOWANIE ***************************************
     *********************************************************************************/

     // TODO fix biale czarne cos jest nie tak
    void rysujPionka(long pionek) {
        boolean bialy = czyBialy(pionek);
        boolean damka = czyDamka(pionek);

        // podmieniłam unicode'y dla czarnego i bialego pionka - w treści były na opak
        char czPion = '\u2659';
        char bPion = '\u265F';
        char czDamka = '\u2655';
        char bDamka = '\u265B';
        if (!damka)
            printWriter.print((bialy ? bPion : czPion) + " ");
        else
            printWriter.print((bialy ? bDamka : czDamka) + " ");
    }

    void rysujPustePole(int x, int y) {
        char biale = '\u2B1B';
        char czarne = '\u2B1C';
        printWriter.print(((x + y) % 2 == 1) ? czarne : biale);
    }

    void rysujPlansze() {
        printWriter.print(" ");
        for (int i = 1; i <= wlkPlanszy; ++i)
            printWriter.print(i + " ");
        printWriter.println();
        for (int y = wlkPlanszy - 1; y >= 0; --y) {
            printWriter.print(y + 1);
            for (int x = 0; x < wlkPlanszy; ++x) {
                long pionek = pionekNaXY(x, y);
                if (czyWGrze(pionek))
                    rysujPionka(pionek);
                else
                    rysujPustePole(x, y);
            }
            printWriter.print(y + 1);
            printWriter.println();
        }
        printWriter.print(" ");
        for (int i = 1; i <= wlkPlanszy; ++i)
            printWriter.print(i + " ");
        printWriter.println("\n------------------");
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
                gra.printWriter.println("Bledny ruch!");
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

// TODO
// 1. koniecGry
// 2. damka fix test_blad_kiedy_polowicznie_bijemy_damka_2.in = 4. multiple bicie damka nie dziala :(
// 3. pousuwać komentarze