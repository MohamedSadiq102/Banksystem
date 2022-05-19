package bankprojekt.model.konten;

import bankprojekt.model.bank.Waehrung;
import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.kunden.Kunde;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Serializable;

/**
 * stellt ein allgemeines Konto dar
 */
public abstract class Konto implements Comparable<Konto>, Serializable
{
	/** 
	 * der Kontoinhaber
	 */
	private Kunde inhaber;

	private Waehrung w = Waehrung.Euro;

	/**
	 * die Kontonummer
	 */
	private final long nummer;

	protected double gewuenschterBetrag;

	/**
	 * der aktuelle Kontostand
	 */
	private double kontostand;

	/**
	 * der aktuelle Kontostand Property
	 */
	private ReadOnlyDoubleWrapper kontostandProperty = new ReadOnlyDoubleWrapper();

	public ReadOnlyDoubleProperty kontostandProperty() {
		return this.kontostandProperty.getReadOnlyProperty();
	}


	/**
	 * setzt den aktuellen Kontostand
	 * @param kontostand neuer Kontostand
	 */
	protected void setKontostand(double kontostand) {
		if(Double.isNaN(kontostand)) {
			throw new IllegalArgumentException();
		}
		this.kontostandProperty.set(kontostand);
	}

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), k�nnen keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers w�ren (abheben, Inhaberwechsel)
	 */
	private boolean gesperrt;

	/**
	 * Wenn das Konto gesperrt ist (gesperrt = true), k�nnen keine Aktionen daran mehr vorgenommen werden,
	 * die zum Schaden des Kontoinhabers waeren (abheben, Inhaberwechsel)
	 */
//	private boolean gesperrt;

	private BooleanProperty gesperrtProperty = new SimpleBooleanProperty();

	public BooleanProperty gesperrtProperty() {
		return this.gesperrtProperty;
	}

	public void setGesperrtPropertyValue(boolean b) {
		this.gesperrtProperty.set(b);
	}

	public boolean getGesperrtPropertyValue() {
		return this.gesperrtProperty.getValue().booleanValue();
	}

	private BooleanProperty isKontostandPlusProperty = new SimpleBooleanProperty();

	public BooleanProperty isKontostandPlusProperty() {
		if (this.getKontostand()<0) {
			isKontostandPlusProperty.set(false);
		}else {
			isKontostandPlusProperty.set(true);
		}
		return this.isKontostandPlusProperty;
	}

	/**
	 * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
	 * der anf�ngliche Kontostand wird auf 0 gesetzt.
	 *
	 * @param inhaber der Inhaber
	 * @param kontonummer die gew�nschte Kontonummer
	 * @throws IllegalArgumentException wenn der Inhaber null
	 */
	public Konto(Kunde inhaber, long kontonummer, Waehrung w) {
		if(inhaber == null)
			throw new IllegalArgumentException("Inhaber darf nicht null sein!");
		this.inhaber = inhaber;
		this.nummer = kontonummer;
		this.kontostand = 0;
		this.gesperrtProperty.set(false);//this.gesperrt = false;
		this.w =w;
		this.kontostandProperty.set(0);
	}
	
	/**
	 * setzt alle Eigenschaften des Kontos auf Standardwerte
	 */
	public Konto() {
		this(Kunde.MUSTERMANN, 1234567, Waehrung.Euro);
	}

	/**
	 * liefert den Kontoinhaber zur�ck
	 * @return   der Inhaber
	 */
	public final Kunde getInhaber() {
		return this.inhaber;
	}
	
	/**
	 * setzt den Kontoinhaber
	 * @param kinh   neuer Kontoinhaber
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn kinh null ist
	 */
	public final void setInhaber(Kunde kinh) throws GesperrtException {
		if (kinh == null)
			throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
		if(this.gesperrtProperty.getValue().booleanValue())
			throw new GesperrtException(this.nummer);        
		this.inhaber = kinh;

	}
	
	/**
	 * liefert den aktuellen Kontostand
	 * @return   double
	 */
	public final double getKontostand() {

		return kontostandProperty.getValue().doubleValue();
	}

	/**
	 * liefert die Kontonummer zur�ck
	 * @return   long
	 */
	public final long getKontonummer() {
		return nummer;
	}

	/**
	 * liefert zur�ck, ob das Konto gesperrt ist oder nicht
	 * @return true, wenn das Konto gesperrt ist
	 */
	public final boolean isGesperrt() {
		return gesperrtProperty.getValue().booleanValue();
//		return gesperrt;
	}
	
	/**
	 * Erh�ht den Kontostand um den eingezahlten Betrag.
	 *
	 * @param betrag double
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 */
	public void einzahlen(double betrag) {
		if (betrag < 0 || Double.isNaN(betrag)) {
			throw new IllegalArgumentException("Falscher Betrag");
		}
		setKontostand(getKontostand() + betrag);
	}
	
	/**
	 * Gibt eine Zeichenkettendarstellung der Kontodaten zur�ck.
	 */
	@Override
	public String toString() {
		String ausgabe;
		ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
				+ System.getProperty("line.separator");
		ausgabe += "Inhaber: " + this.inhaber;
		ausgabe += "Aktueller Kontostand: " + this.kontostandProperty.getValue().doubleValue() + "  "+ this.w.name();
		ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
		return ausgabe;
	}

	/**
	 * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist.
	 *
	 * @param betrag double
	 * @throws GesperrtException wenn das Konto gesperrt ist
	 * @throws IllegalArgumentException wenn der betrag negativ ist
	 * @return true, wenn die Abhebung geklappt hat, 
	 * 		   false, wenn sie abgelehnt wurde
	 */
	public abstract boolean abheben(double betrag) 
								throws GesperrtException;
	
	/**
	 * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr m�glich.
	 */
	public final void sperren() {
		this.gesperrtProperty.set(true);
	}

	/**
	 * entsperrt das Konto, alle Kontoaktionen sind wieder m�glich.
	 */
	public final void entsperren() {
		this.gesperrtProperty.set(false);
	}
	
	
	/**
	 * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
	 * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
	 */
	public final String getGesperrtText()
	{
		if (this.gesperrtProperty.getValue().booleanValue())
		{
			return "GESPERRT";
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * liefert die ordentlich formatierte Kontonummer
	 * @return auf 10 Stellen formatierte Kontonummer
	 */
	public String getKontonummerFormatiert()
	{
		return String.format("%10d", this.nummer);
	}
	
	/**
	 * liefert den ordentlich formatierten Kontostand
	 * @return formatierter Kontostand mit 2 Nachkommastellen und W�hrungssymbol �
	 */
	public String getKontostandFormatiert()
	{
		return String.format("%10.2f  " + w.name() , this.getKontostand());
	}
	
	/**
	 * Vergleich von this mit other; Zwei Konten gelten als gleich,
	 * wen sie die gleiche Kontonummer haben
	 * @param other das Vergleichskonto
	 * @return true, wenn beide Konten die gleiche Nummer haben
	 */
	@Override
	public boolean equals(Object other)
	{
		if(this == other)
			return true;
		if(other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		if(this.nummer == ((Konto)other).nummer)
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode()
	{
		return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
	}

	@Override
	public int compareTo(Konto other)
	{
		if(other.getKontonummer() > this.getKontonummer())
			return -1;
		if(other.getKontonummer() < this.getKontonummer())
			return 1;
		return 0;
	}
	
	/**
	 * gibt this auf der Konsole aus
	 * 
	 * NICHT AUFRUFEN: Widerspricht der Regel: Trenne
	 * Verarbeitung von der Ein-/Ausgabe!
	 */
	public void ausgeben()
	{
		System.out.println(this.toString());
	}
	
	
	/**
	 * Die Mehtode fuer Abhebung das Geld
	 * 
	 * @param betrag, den man abgezogen wird
	 * @param w       fuer Waehrung
	 * @return true ,wenn man abheben kann.ansonst false
	 * @throws GesperrtException
	 */

	public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
		betrag = Waehrung.waehrungZuWahrung(betrag, w, this.w);
		return abheben(betrag);
	}

	/**
	 * Die Methode fuer Einzahlung in Einem Konto
	 * @param betrag,den man eingezogen wird
	 * @param w fur Waehrung
	 */
	public void einzahlen(double betrag, Waehrung w) throws IllegalArgumentException {
		betrag = Waehrung.waehrungZuWahrung(betrag, w, this.w);
		einzahlen(betrag);
	}
    /**
     *  Die Methode ,die sagt,welche Waehrung gerade verwendet
     * @return die aktuelle Waehrung
     */
	public Waehrung getaktuelleWaehrung() {
		return w;
	}
      /**
       *  Der Methode fur Aenderung der aktuellen Waehrung 
       *  und das Konto wird umgerechnet
       * @param neu fur die neu Waehrung
       */

	
	public void waehrungswechsel(Waehrung neu) {
		this.kontostandProperty.set(Waehrung.waehrungZuWahrung(getKontostand(),w ,neu));
//		kontostand = Waehrung.waehrungZuWahrung(kontostand, w, neu);
		this.w = neu;

	}

	/**
	 * Es wird überprüft, ob der Betrag positiv ist, falls ja true sonst false
	 * @return
	 */
	public boolean istDerUebertrageneBetragPositiv(){
		if (gewuenschterBetrag > 0){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Der Kontosntand wird vermindert aufrund einer abhebung
	 */
	public void vermindernDesKontostands(){
		setKontostand(getKontostand() - gewuenschterBetrag);
	}


	/**
	 * Die Methode prüft, ob man  genug Geld hat und liefert nur false (nicht genug Geld)
	 * Bemerkung : In der Subklasse Girokonto abgeändert
	 * @return
	 */
	public  boolean kontoGedeckt(){
		return false;
	}

	/**
	 * Die Methode überpüft, ob man den höscht Betrag überschreitet und liefert nur false
	 * Bemerkung : In der Subklasse Sparbuch abgeändert
	 * @return
	 */
	public boolean monatHoechstBetrag() {
		return false;
	}

	public abstract void aktualisieren();
}
