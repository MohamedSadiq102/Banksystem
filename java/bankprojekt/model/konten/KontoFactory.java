package bankprojekt.model.konten;

import bankprojekt.model.bank.Abheben;
import bankprojekt.model.bank.Waehrung;
import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.kunden.Kunde;

/**
 * KontoFactory
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */
public abstract class KontoFactory extends Abheben {

    public KontoFactory (Kunde mustermann, long kontonummer, Waehrung w){

    }

    public KontoFactory() {
    }

    @Override
    public boolean abheben(double betrag) throws GesperrtException {
        return false;
    }

}
