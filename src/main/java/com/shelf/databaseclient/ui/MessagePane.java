package com.shelf.databaseclient.ui;

import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MessagePane  extends BorderPane {

    private static final int TIMEOUT = 10;
    
    private final Label labelMessage = new Label(),
                        labelStatus = new Label(),
                        labelTime = new Label();
    private Task<Void> task;
    private boolean work;
    
    private static MessagePane pane;
    
    public static synchronized MessagePane getInstance() {
        if (pane == null) pane = new MessagePane();
        return pane;
    }

    public MessagePane(){
        super();
        setPrefHeight(10);
        setStyle("-fx-padding: 0px;"+ //1px
                 "-fx-border-style: solid; -fx-border-color: grey; -fx-border-width: 2px; "+
                 "-fx-background-color: rgb(220,230,242);");
        labelMessage.setStyle("-fx-border-style: solid; -fx-border-color: grey; -fx-border-width: 0px; "+
                              "-fx-text-fill: rgb(31,73,125); -fx-font-weight: bold;");
        labelStatus.setStyle("-fx-border-style: solid; -fx-border-color: grey; -fx-border-width: 0px 2px 0px 0px; "+
                             "-fx-text-fill: rgb(31,73,125); -fx-font-weight: bold;");
        labelTime.setStyle("-fx-border-style: solid; -fx-border-color: grey; -fx-border-width: 0px 0px 0px 2px; "+
                           "-fx-text-fill: rgb(31,73,125); -fx-font-weight: bold;");
        setCenter(labelMessage);
        setLeft(labelStatus);
        setRight(labelTime);
    }
    
    public void setMessage( String text ){
        if( !labelMessage.getText().equals(text) ) labelMessage.setText( text );
    }

    public void setStatus( String text ){
        if( !labelStatus.getText().equals(text) ) labelStatus.setText( text );
    }
    
    public void startTime(){
        setStatus("");
        setMessage("");
        work = true;
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                long start = System.currentTimeMillis();
                while( work ){
                    Thread.sleep(TIMEOUT);
                    Platform.runLater( () -> 
                        labelTime.setText( String.format("%10.2f", (System.currentTimeMillis()-start)/1000.0).replace(",",".")+" s " )
                    );
                }
                return null;
            } };
        new Thread(task,"TimeThread").start();
    }
    
    public void stopTime(){
        work = false;
    }
    
}
