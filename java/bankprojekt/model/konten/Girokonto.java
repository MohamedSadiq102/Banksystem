package bankprojekt.model.konten;

import bankprojekt.model.bank.Bank;
import bankprojekt.model.bank.Waehrung;
import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.kunden.Kunde;

import java.io.Serializable;

/**
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */

public class Girokonto extends  Konto implements Ueberweisungsfaehig {
	/**
	 * Wert, bis zu dem das Konto �berzogen werden darf
	 */
	private double dispo;

	private Waehrung w = Waehrung.Euro;

	private double kontostand = 500;

	private double kontostandAktuell = 500;

	/**
	 * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto
	 * von Herrn MUSTERMANN
	 */
	public Girokonto()
	{
		super(Kunde.MUSTERMANN, 99887766, Waehrung.Euro);
		this.dispo = 500;
	}
	
	/**
	 * erzeugt ein Girokonto mit den angegebenen Werten
	 * @param inhaber Kontoinhaber
	 * @param nummer Kontonummer
	 * @param dispo Dispo
	 * @throws IllegalArgumentException wenn der inhaber null ist oder der angegebene dispo negativ bzw. NaN ist
	 */
	public Girokonto(Kunde inhaber, long nummer, double dispo, Waehrung w)
	{
		super(inhaber, nummer, Waehrung.Euro);
		if(dispo < 0 || Double.isNaN(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht g�ltig!");
		this.dispo = dispo;
		this.w = Waehrung.Euro;
	}
	
	/**
	 * liefert den Dispo
	 * @return Dispo von this
	 */
	public double getDispo() {
		if (getaktuelleWaehrung() != this.w) {
			if (getaktuelleWaehrung() == Waehrung.BGN) {
				this.w = getaktuelleWaehrung();
				return this.w.euroInWaehrungUmrechnen(dispo);
			}
			if (getaktuelleWaehrung() == Waehrung.KM) {
				this.w = getaktuelleWaehrung();
				return this.w.euroInWaehrungUmrechnen(dispo);
			}
			if (getaktuelleWaehrung() == Waehrung.LTL) {
				this.w = getaktuelleWaehrung();
				return this.w.euroInWaehrungUmrechnen(dispo);
			}
			if (getaktuelleWaehrung() == Waehrung.Euro) {
				this.w = getaktuelleWaehrung();
				return this.w.waehrungInEuroUmrechnen(dispo);
			}
		}
		return dispo;
	}
	

	/**
	 * setzt den Dispo neu
	 * @param dispo muss gr��er sein als 0
	 * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
	 */
	public void setDispo(double dispo) {
		if(dispo < 0 || Double.isNaN(dispo))
			throw new IllegalArgumentException("Der Dispo ist nicht g�ltig!");
		this.dispo = dispo;
	}
	
	@Override
    public boolean ueberweisungAbsenden(double betrag,
                                        String empfaenger, long nachKontonr,
                                        long nachBlz, String verwendungszweck)
    				throws GesperrtException
    {
      if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (betrag < 0 || Double.isNaN(betrag) || empfaenger == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        if (getKontostand() - betrag >= - dispo)
        {
            setKontostand(getKontostand() - betrag);
            return true;
        }
        else
        	return false;
    }

    @Override
    public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz, String verwendungszweck)
    {
        if (betrag < 0 || Double.isNaN(betrag) || vonName == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        setKontostand(getKontostand() + betrag);
    }
    
    @Override
    public String toString()
    {
    	String ausgabe = "-- GIROKONTO --" + System.lineSeparator() +
    	super.toString()
    	+ "Dispo: " + this.dispo + System.lineSeparator();
    	return ausgabe;
    }

	@Override
	public boolean abheben(double betrag) throws GesperrtException{
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Betrag ung�ltig");
		}
		if(this.isGesperrt())
			throw new GesperrtException(this.getKontonummer());
		if (getKontostand() - betrag >= - dispo)
		{
			setKontostand(getKontostand() - betrag);
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void waehrungswechsel (Waehrung neu) {
		dispo = Waehrung.waehrungZuWahrung(dispo, this.getaktuelleWaehrung(), neu);
		super.waehrungswechsel(neu);
	}

	/**
	 * Die Methode schaut, ob das Konto gedeckt ist und liefert je nachdem true oder false

	 */
	@Override
	public boolean kontoGedeckt() {
		return getKontostand() - gewuenschterBetrag > -dispo;
	}

	@Override
	/**
	 * Die Methode vermindert den Kontostand, wenn das Konto gedckt ist
	 * (n�here Informationen in der Oberklasse)
	 */
	public void vermindernDesKontostands() {
		if(kontoGedeckt() == true) {
			super.vermindernDesKontostands();
		}
	}


	/**
	 * aktualisiert die Daten, um zu überprüfen, ob der Kontostand sich geändert hat
	 * @param bank
	 */
	//@Override
	public void aktualisieren(Bank bank) {
		kontostand = kontostandAktuell;
		kontostandAktuell = bank.getKontostand();

		benachrichtigen();
	}

	/**
	 * benachrichtigt den Kunden, wenn der Kontostand gestiegen oder gesunken ist
	 */
	public void  benachrichtigen(){
		if (kontostandAktuell < kontostand) {
			System.out.println("Ihr Kontostand ist weniger als vorher");
		}
		if(kontostand == kontostandAktuell){
			System.out.println("Ihr Kontostand hat keine Veränderung");
		}else{
			System.out.println("Ihr Kontostand ist gestiegen");
		}
	}
}
