package bankprojekt.model.konten;

import bankprojekt.model.bank.Bank;
import bankprojekt.model.bank.Waehrung;
import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.kunden.Kunde;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Sparbuch
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */
public class Sparbuch extends Konto {
	/**
	 * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
	 */
	private double zinssatz;
	
	
	private Waehrung w = Waehrung.Euro;

	/**
	 * Monatlich erlaubter Gesamtbetrag f�r Abhebungen
	 */
	public static final double ABHEBESUMME = 2000;
	
	/**
	 * Betrag, der im aktuellen Monat bereits abgehoben wurde
	 */
	private double bereitsAbgehoben = 0;
	/**
	 * Monat und Jahr der letzten Abhebung
	 */
	private LocalDate zeitpunkt = LocalDate.now();

	// fuer Benachritigung
	private double besitzt = 500;

	private double besitzJetzt = 500;

	private double abgehobenJetzt = 0;
	
	/**
	* ein Standard-Sparbuch
	*/
	public Sparbuch() {
		zinssatz = 0.03;
	}

	/**
	* ein Standard-Sparbuch, das inhaber geh�rt und die angegebene Kontonummer hat
	* @param inhaber der Kontoinhaber
	* @param kontonummer die Wunsch-Kontonummer
	* @throws IllegalArgumentException wenn inhaber null ist
	*/
	public Sparbuch(Kunde inhaber, long kontonummer , Waehrung w) {
		super(inhaber, kontonummer, Waehrung.Euro);
		zinssatz = 0.03;
		this.w = w;
	}
	
	@Override
	public String toString()
	{
    	String ausgabe = "-- SPARBUCH --" + System.lineSeparator() +
    	super.toString()
    	+ "Zinssatz: " + this.zinssatz * 100 +"%" + System.lineSeparator();
    	return ausgabe;
	}

	@Override
	public boolean abheben (double betrag) throws GesperrtException {
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Betrag ung�ltig");
		}
		if(this.isGesperrt())
		{
			GesperrtException e = new GesperrtException(this.getKontonummer());
			throw e;
		}
		LocalDate heute = LocalDate.now();
		if(heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear())
		{
			this.bereitsAbgehoben = 0;
		}
		if (getKontostand() - betrag >= 0.50 && 
				 bereitsAbgehoben + betrag <= Sparbuch.ABHEBESUMME)
		{
			setKontostand(getKontostand() - betrag);
			bereitsAbgehoben += betrag;
			this.zeitpunkt = LocalDate.now();
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void waehrungswechsel (Waehrung neu) {
		bereitsAbgehoben = Waehrung.waehrungZuWahrung(bereitsAbgehoben,
				this.getaktuelleWaehrung(), neu);
		super.waehrungswechsel(neu);
	}


	/**
	 * Die Methode sorgt dafuer, dass der besitzer des Sparnuches innerhalb des Monats nicht
	 * mehr als die hoechst Betrag abziehen kann
	 */
	@Override
	public boolean monatHoechstBetrag() {
		LocalDate heute = LocalDate.now();
		if(heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
			this.bereitsAbgehoben = 0;
		}
		if (getKontostand() - gewuenschterBetrag >= 0.50 &&
				bereitsAbgehoben + gewuenschterBetrag <= ABHEBESUMME)
		{
			bereitsAbgehoben += gewuenschterBetrag;
			this.zeitpunkt = LocalDate.now();
			return true;
		}
		else
			return false;
	}

	/**
	 * Vermindert den Kontostand, wenn man vom Sparbuch noch Geld abheben kann in dem jeweiligen
	 * Monat
	 */
	@Override
	public void vermindernDesKontostands() {
		if (monatHoechstBetrag() == true)
			super.vermindernDesKontostands();
	}



	public void benachrichtigung(){

		if (bereitsAbgehoben < abgehobenJetzt){
			System.out.println("Sie haben bis jetzt " + abgehobenJetzt + " abgehoben und" +
					"können nur noch " + (ABHEBESUMME - bereitsAbgehoben) + "abheben");
		}

		if (besitzt < besitzJetzt){
			System.out.println("Ihr Kontostand ist gestiegen");
		}
		if (besitzt == besitzJetzt){
			System.out.println("Ihr Kontostand hat keine Veränderung");
		}else{
			System.out.println("Ihr Kontostand ist weniger als vorher");
		}

	}



	/**
	 * aktualisiert die Daten und gibt an, wie viel der Kunde bereits abgehoben hat,
	 * wie viel er noch abziehen darf und wie sich der Kontostand verändert hat (oder ob
	 * es sich nicht verändert hat)
	 */
	//@Override
	public void aktualisieren(Bank bank) {
		besitzt = besitzJetzt;
		besitzJetzt = bank.getKontostand();
		bereitsAbgehoben = abgehobenJetzt;
		abgehobenJetzt = bereitsAbgehoben;

		benachrichtigung();
	}
}
