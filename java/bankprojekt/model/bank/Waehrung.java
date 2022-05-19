package bankprojekt.model.bank;



public enum Waehrung {
	/**
	 * Die Währungen sind Euro BGN , LTL , KM ,
	 * ,Und jede hat einen  Wert, die nach Euro umgerechnet ist
	 */
	
	Euro(1), BGN(1.95583), LTL(3.4528), KM(1.95583);
	
	/**
	 * eine Private Variable, die wir in ihr einen Beitrag spreichern werden
	 */

	private double KursZuEuro;
	
	
	/**
     *  Private Konstruktor ,der keine Objekte erzeugen koennen
     * @param betrag .Betrag fuer Umrechnung die Waehrung in Euro
     */
	private Waehrung(double betrag) {
		this.KursZuEuro = betrag;
	}	
	
	/**
     *  Die Methode rechnet die Euro in LTL KM um	
     * @param betrag ,wie viel Euro umgerechnet wird
     * @return lierfer der Wechselkurs
     */

	
	public double euroInWaehrungUmrechnen(double betrag) {
		return betrag * KursZuEuro;
	}
	
	
	/**
	 *   Die rechnung eines Wechelkurses in Euro
	 * @param betrag
	 * @return lierfert den WechselKurs
	 */
	public double waehrungInEuroUmrechnen(double betrag) {
		
			return  betrag / KursZuEuro;	
	}
	
	public static double waehrungZuWahrung(double betrag, Waehrung von , Waehrung zu){
		return zu.euroInWaehrungUmrechnen(von.waehrungInEuroUmrechnen(betrag));
		
	}
	
	

}
