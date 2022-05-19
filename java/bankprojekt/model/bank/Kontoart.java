package bankprojekt.model.bank;

/**
 * Prospekt f�r das Bankprojekt
 * @author Dorothea Hubrich
 *
 */
public enum Kontoart {
	/**
	 * ein Girokonto
	 */
	GIROKONTO("Mit ganz hohen Dispo"),
	/**
	 * ein Sparbuch
	 */
	SPARBUCH("Mit vielen Zinsen"),
	/**
	 * ein Festgeldkonto
	 */
	FESTGELDKONTO("Ham wa noch gar nich...");
	
	private String werbebotschaft;
	
	private Kontoart(String werbebotschaft)
	{
		this.werbebotschaft = werbebotschaft;
	}

	/**
	 * @return the werbebotschaft
	 */
	public String getWerbebotschaft() {
		return this.werbebotschaft;
	}
	
	@Override
	public String toString()
	{
		return this.name() + " " + this.werbebotschaft;
	}
	
}
