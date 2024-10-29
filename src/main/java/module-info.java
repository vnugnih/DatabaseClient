module com.shelf.databaseclient {
    requires java.sql;
    requires javafx.controls;
    requires javafx.web;
    requires json.simple;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    exports com.shelf.databaseclient.ui;
}
