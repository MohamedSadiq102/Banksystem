package bankprojekt.model.bank;

import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.konten.*;
import bankprojekt.model.kunden.Kunde;

import java.io.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bank extends Abheben implements Cloneable, Serializable {
	/**
	 * Die Bankleitzahl der Bank
	 */
	private long bankleitzahl;
	
	private static final long FIRST_KONTONUMMER = 0l;

	/**
	 * speicher enthaelt die Kontonummer und den Besitzer der Kontonummer, der durch
	 * die Nummer identfizierbar ist
	 */
	private TreeMap<Long, Konto> konten;
	/**
	 * enthaelt alle gueltige Kontonummer
	 */
	private List<Long> alleGueltigenKontonummern;
	/**
	 * In der Liste werden die erstellten Kontonummern gespeichert, damit man anhand
	 * der Liste merken kann, ob eine neu erstellte Kontonummer in der Liste
	 * enthalten ist und wenn ja wird sie nicht in die Liste hinzugefuegt
	 */
	private List<Long> merke;

	private long limit = 100;

	private long unterGrenze = 1;

	private List<Kontobeobachter> kontobenachtrigung = new LinkedList<>();

	/**
	 * Konstruktor
	 */
	public Bank(long bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
		this.konten = new TreeMap<>();

	}

	/**
	 *
	 * @return die bankleitzahl
	 */
	public long getBankleitzahl() {
		return this.bankleitzahl;
	}

	/**
	 * erstellt ein Girokonto f�r den angegebenenKunden
	 * 
	 * @param inhaber
	 * @return die Kontonummer des Girokontos
	 */
	public long girokontoErstellen(Kunde inhaber) {
		long kontonummer = generiereLong();
		Girokonto girokonto = new Girokonto(inhaber, kontonummer, 500, Waehrung.Euro);
		konten.put(kontonummer, girokonto);
		return kontonummer;
	}

	/**
	 * erstellt ein Sparkonto f�r den angegebenenKunden
	 * 
	 * @param inhaber
	 * @return die Kontonummer des Sparbuches
	 */
	public long sparbuchErstellen(Kunde inhaber) {
		long kontonummer = generiereLong();
		Sparbuch sparbuch = new Sparbuch(inhaber, kontonummer, Waehrung.Euro);
		konten.put(kontonummer, sparbuch);
		return kontonummer;
	}

	/**
	 * generiert eine zufaellie Kontonummer zwischen den Werten origin und bound
	 * Falls die Kontonummer schon existiert wird die Methode so lange aufgerufen,
	 * bis eine nicht vorhandene Kotonummer erstelltwird
	 * 
	 * @return die Kontonummer
	 */
	public long generiereLong() {
		if(konten.isEmpty())
			return FIRST_KONTONUMMER;
		
		else
			return konten.lastKey() +1;
	}

	/**
	 * liefert eine Auflistungvon Kontoinformationen aller Konten
	 * 
	 * @return
	 */
	public String getAlleKonten() {
		StringBuilder sb = new StringBuilder();
		for(Konto konto : konten.values()) {
			sb.append(konto.getKontonummer() + ":" 
		  + konto.getKontostand() + System.lineSeparator());
		}
		return sb.toString();
	}
	

	/**
	 * liefert eine Liste aller g�ltigen Kontonummern in der Bank
	 * 
	 * @return liefert alle gueltigen Kontonummern
	 */
	public List<Long> getAlleKontonummern() {
		
		return new LinkedList(konten.keySet());
	}
	
	private void testIfKontonummerExists(long kontonummer) throws KontonummerNotFoundExption {
		if(! konten.containsKey(kontonummer)) {
			throw new KontonummerNotFoundExption(kontonummer);
		}
	}

	/**
	 * hebt den Betrag vom Konto mit der Nummer von ab und gibt zur�ck, ob die
	 * Abhe-bung geklappt hat.
	 */
	public boolean geldAbheben(long von, double betrag) throws KontonummerNotFoundExption , GesperrtException {
		testIfKontonummerExists(von);
		Konto k = konten.get(von);
		return k.abheben(betrag);
	}

	/**
	 * zahlt den angegebenen Betrag auf das Konto mit der Nummer auf ein
	 */
	public void geldEinzahlen(long auf, double betrag) throws KontonummerNotFoundExption  {
		testIfKontonummerExists(auf);
		Konto k = konten.get(auf);
		k.einzahlen(betrag);
	}

	/**
	 * l�scht das Konto mit der angegebenen nummer und gibt zur�ck, ob die L�schung
	 * ge-klappt hat
	 */
	public boolean kontoLoeschen(long kontonummer) {
		boolean geloescht = false;
		if (!konten.containsKey(kontonummer)) {
			return false;
		}
		konten.remove(kontonummer);
		return true;
			
	}

	/**
	 * liefert den Kontostand des Kontos mit der angegebenen nummerzur�ck
	 */
	public double getKontostand(long kontonummer) throws KontonummerNotFoundExption {
		testIfKontonummerExists(kontonummer);
		Konto k = konten.get(kontonummer);
		return k.getKontostand();
	}

	/**
	 * �berweist den genannten Betrag vom Girokontomit der Nummer vonKontonrzum
	 * Girokontomit der Nummer nachKontonrund gibt zur�ck, ob die �berweisung
	 * ge-klappt hat
	 */
	public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) throws GesperrtException {

		if (konten.get(vonKontonr).isGesperrt() == true || konten.get(nachKontonr).isGesperrt() == true) {
			return false;
		}
		if (konten.get(vonKontonr).getKontostand() < betrag) {
			return false;
		}

		if (!(konten.get(vonKontonr) instanceof Girokonto) || !(konten.get(nachKontonr) instanceof Konto)) {
			return false;
	      }
		return true;
	}
	
	public boolean isGesperrt(long kontonummer ) throws KontonummerNotFoundExption{
		testIfKontonummerExists(kontonummer);
		Konto k = konten.get(kontonummer);
		return k.isGesperrt();
	}

	public String ausgabe(){
		return "1) Girokonto, 2) Sparbuch";
	}

	/**
	 * die Methode sperrt alle Konten, deren Kontostand im Minus ist
	 */
	public void pleitegeierSperren() {
		konten.entrySet().parallelStream().filter(a -> a.getValue().getKontostand() < 0)
				.forEach(a -> a.getValue().sperren());
	}

	/**
	 * liefert eine Liste aller Kunden, die auf einem Konto einen Konto- stand
	 * haben, der ein Minimum betraegt
	 *
	 * @param minimum
	 * @return
	 */
	public List<Kunde> getKundenMitVollemKonto(double minimum) {
		List<Kunde> list = konten.entrySet().parallelStream().filter(a -> a.getValue().getKontostand() >= minimum)
				.map(a -> a.getValue().getInhaber()).collect(Collectors.toList());
		return list;
	}

	/**
	 * Die Methode liefert die Namen und Geburtstage aller Kunden der Bank. Doppelte
	 * Namen sollen dabei aussortiert werden. Die Liste ist nach dem Geburtsdatum
	 * sortiert
	 *
	 * @return Kundengeburtstage als String
	 */
	public String getKundengeburtstage() {

		String reduceStartStr = "Geburtstagliste" + System.getProperty("line.separator");
		BinaryOperator<String> reduceToString = (alt, neu) -> alt + neu + System.getProperty("line.separator");

		Comparator<Kunde> compare = (kunde1, kunde2) -> kunde1.getGeburtstag().compareTo(kunde2.getGeburtstag());

		Function<Kunde, String> mapKundeToString = kunde -> kunde.getName() + " (" + kunde.getGeburtstag() + ")";

		String geburtstagsliste = konten.values().stream().map(Konto::getInhaber) // map Konten Stream auf Kunden
				// Stream, alternativ inline: konto
				// ->
				// konto.getInhaber()
				.distinct() // nur veschiedene Kunden
				.sorted(compare) // sortiere die unterschiedlichen Kunden nach ihrem Geburtstdatum
				.map(mapKundeToString) // map Kunde auf String Stream, alternativ inline: kunde -> kunde.getName() + "
				// (" + kunde.getGeburtstag() + ")"
				.distinct() // nochmal distinct auf den String (inkl. Geburtsdatum, da Wahrscheinlichkeit
				// hoch das es 2 x Hans Mueller gibt)
				.reduce(reduceStartStr, reduceToString); // reduziere Stream auf einen String

		return geburtstagsliste;
	}

	public List<Long> getKontonummernLuecken()  {
		List<Long> list = Stream.iterate(0L, x -> x + 1).limit(getAlleKontonummern().size())
				.filter(x -> !konten.containsKey(x)).collect(Collectors.toList());

		Long[] array = new Long[getAlleKontonummern().size()];
		Arrays.setAll(array, i -> new Long(i));
		list = Arrays.stream(array).filter(x -> !konten.containsKey(x)).collect(Collectors.toList());
		return list;
	}


	public void getAlleReichenKunden(double minimum) {
		konten.values().stream().map(Konto::getInhaber).distinct()
				.filter(aktKu -> konten.values().stream().filter(k -> k.getInhaber() == aktKu)
						.map(k -> k.getKontostand()).reduce(0.0, (a, b) -> a + b) > 150)
				.forEach(ku -> System.out.println(ku.getName()));
	}

	/**
	 * erstellt eine Kopie vom Bank Objekt
	 *
	 * @return
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Object objDest = null;
		byte[] byteData = null;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
			oos.close();

			byteData = bos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteData))) {

			objDest = ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return objDest;
	}


	@Override
	public boolean abheben(double betrag) throws GesperrtException {
		return false;
	}

	@Override
	public void aktualisieren() {

	}


	/**
	 * Meldet konto am Subjekt ein
	 * @param konto
	 */
	public void anmelden(Kontobeobachter konto){
		this.kontobenachtrigung.add((Kontobeobachter) konto);
	}

	/**
	 * meldet Konto am Subjekt wieder ab
	 * @param konto
	 */

	public void abmelden(Kontobeobachter konto){
		this.kontobenachtrigung.remove(konto);
	}


	private void benachrichtigen(long kontonummer) {
		if(konten.containsKey(kontonummer)) {
			konten.get(kontonummer).aktualisieren();
		}
	}}
