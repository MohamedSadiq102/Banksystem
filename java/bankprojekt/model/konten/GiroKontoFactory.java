package bankprojekt.model.konten;

/**
 * GiroKontoFactory
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */

public class GiroKontoFactory extends KontoFactory {


    //@Override
    public Konto Kontoerstellen(Kunde kunde, long kontonummer, Waehrung w) {
        return new Girokonto(kunde, kontonummer, 0, w);
    }
}
