package bankprojekt.model.konten;

public class KontonummerNotFoundExption extends Exception {
	
	public KontonummerNotFoundExption(long kontonummer)
	{
		super("Zugriff auf konto Konto mit Kontonummer not found " + kontonummer);
	}

}
