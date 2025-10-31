module com.simonepugliese.wallet {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.simonepugliese to javafx.fxml;

    // Se i tuoi Controller fossero in un altro pacchetto, ad esempio com.simonepugliese.gui,
    // dovresti aprirlo separatamente:
    // opens com.simonepugliese.gui to javafx.fxml;

    // Potresti anche voler aprire altri pacchetti, in base a dove risiedono i tuoi Controller
    // e le tue classi iniettate. Ad esempio:
    opens com.simonepugliese.Manager to javafx.fxml;
    opens com.simonepugliese.Criptor to javafx.fxml;
    opens com.simonepugliese.Item to javafx.fxml;
    opens com.simonepugliese.Saver to javafx.fxml;

    // EXPORTS non è tipicamente richiesto per i controller, ma se vuoi
    // che altre applicazioni *esterne* usino le tue API (es. Item, Manager),
    // dovresti usare 'exports' al posto di 'opens'.
}
