package bankprojekt.model.konten;

/**
 * SparbuchFactory
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */

public class SparbuchFactory extends KontoFactory {

    //@Override
    public Konto Kontoerstellen(Kunde kunde, long kontoNummer, Waehrung w)
    {
        return new Sparbuch(kunde , kontoNummer, w);
    }


}
