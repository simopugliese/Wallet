module com.simonepugliese.wallet {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    exports com.simonepugliese;
    exports com.simonepugliese.Core;
    exports com.simonepugliese.Model;
    exports com.simonepugliese.Security;

    opens com.simonepugliese.Persistence to org.junit.platform.commons;
    opens com.simonepugliese to javafx.fxml, javafx.graphics;
    opens com.simonepugliese.Model to javafx.base;
}