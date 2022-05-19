package bankprojekt.model.bank;
/*
 * @author Mohamed Sadiq , Fadl Elareny
 */

import bankprojekt.model.konten.Konto;
import bankprojekt.model.kunden.Kunde;

import java.util.Scanner;

/**
 * Abstrakte Klasse, die das Abheben für Sparbuch und Girokonto verwendet und
 * (Diese Methoden müssen
 * in der jeweilige Subklasse überschrieben werden)
 */
public abstract class Abheben extends Konto {

    /**
     * Der abzuhiende Betrag
     */
    protected double gewuenschterBetrag;

    /**
     * Konstruktor
     * @param mustermann
     * @param i
     * @param eur
     */

    public Abheben(Kunde mustermann, long i, Waehrung eur) {

    }

    /**
     * leerer Konstruktor
     */
    public Abheben() {

    }

    /**
     * Die Template Methode, die die allgemeinen Schritte ausruft
     */
    protected final void TemplateMethode(){
        Scanner betrag = new Scanner(System.in);
        System.out.println("Den abzuhebenden Betrag angeben");
        gewuenschterBetrag = betrag.nextDouble();
        gesperrt();
        istDerUebertrageneBetragPositiv();
        vermindernDesKontostands();

    }

    /**
     * @return true = gesperrt, false = nicht gesperrt
     */
    public boolean gesperrt(){

       return isGesperrt() == true ;
    }


    /**
     * Es wird überprüft, ob der Betrag positiv ist, falls ja true sonst false
     */
    public boolean istDerUebertrageneBetragPositiv(){

        return  gewuenschterBetrag > 0;
    }

    /**
     * Der Kontosntand wird vermindert aufrund einer abhebung
     */
    public void vermindernDesKontostands(){
        setKontostand(getKontostand() - gewuenschterBetrag);
    }


    /**
     * Die Methode prüft, ob man  genug Geld hat und liefert nur false (nicht genug Geld)
     * @return false
     */
    public  boolean kontoGedeckt(){
        return false;
    }

    /**
     * Die Methode überpüft, ob man den höchst Betrag überschreitet und liefert nur false
     * @return false
     */
    public boolean monatHoechstBetrag(){
        return false;
    }






}