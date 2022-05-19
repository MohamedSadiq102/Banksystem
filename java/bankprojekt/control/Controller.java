package bankprojekt.control;


import bankprojekt.model.bank.Waehrung;
import bankprojekt.model.exceptions.GesperrtException;
import bankprojekt.model.konten.Girokonto;
import bankprojekt.model.kunden.Kunde;
import bankprojekt.view.KontoOberflaeche;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
/**
 * Controller
 *
 * @author Mohamed Sadiq , Fadl Elareny
 *
 */

public class Controller extends Application {
    @FXML
    private Text ueberschrift;

    @FXML
    private Text txtNummer;

    @FXML
    private Text txtStand;

    @FXML
    private Text nummer;

    @FXML
    private Text stand;

    @FXML
    private Text txtGesperrt;

    @FXML
    private CheckBox gesperrt;

    @FXML
    private Text txtAdresse;

    @FXML
    private TextArea adresse;

    @FXML
    private Text meldung;

    @FXML
    private TextField betrag;

    @FXML
    private Button einzahlen;

    @FXML
    private Button abheben;


    @FXML
    void initialize() {
        assert ueberschrift != null : "fx:id=\"ueberschrift\" was not injected: check your FXML file 'KontoOberflaeche.fxml'.";
        assert txtNummer != null : "fx:id=\"txtNummer\" was not injected: check your FXML file 'KontoOberflaeche.fxml'.";
        assert txtStand != null : "fx:id=\"txtStand\" was not injected: check your FXML file 'KontoOberflaeche.fxml'.";
        assert nummer != null : "fx:id=\"nummer\" was not injected: check your FXML file 'KontoOberflaeche.fxml'.";
        try {
            g1.abheben(300);
        } catch (GesperrtException e) {
        }


        //do bindings and stuff
        nummer.setText(getGirokontoModel().getKontonummerFormatiert());
        stand.textProperty().bind(getGirokontoModel().kontostandProperty().asString("%.2f"));
        gesperrt.selectedProperty().bindBidirectional(getGirokontoModel().gesperrtProperty());
        adresse.textProperty().bindBidirectional(getGirokontoModel().getInhaber().adresseProperty());
    }

    /**
     * Das Model mit den aktuellen Daten
     */
    private Girokonto g1 = new Girokonto(Kunde.MUSTERMANN, 123456789L, 500.0, Waehrung.Euro);

    public Girokonto getGirokontoModel() {
        return this.g1;
    }

    /**
     * Das Hauptfenster der Anwendung
     */
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("KontoOberflaeche.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 320, 343);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Konto Veraendern");
        primaryStage.show();

    }

    @FXML
    public void einzahlenClick() {
        try {
            g1.einzahlen(Double.parseDouble(betrag.getText()));
        } catch (NumberFormatException e) {
            System.err.println("Fehlerhafte Eingabe");
        }

    }

    public void abhebenClick() {
        try {
            g1.abheben(Double.parseDouble(betrag.getText()));
        } catch (NumberFormatException e) {
            System.err.println("Fehlerhafte Eingabe");
        } catch (GesperrtException e) {
            System.err.println("Konto ist gesperrt");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

//    public void einzahlenClick(ActionEvent actionEvent) {
//    }
//
//    public void abhebenClick(ActionEvent actionEvent) {
//    }
}

