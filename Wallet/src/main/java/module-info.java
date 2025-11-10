module com.simonepugliese.wallet {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.simonepugliese to javafx.fxml, javafx.graphics;

    opens com.simonepugliese.Model to javafx.base;
}