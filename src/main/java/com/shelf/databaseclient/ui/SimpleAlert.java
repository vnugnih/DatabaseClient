package com.shelf.databaseclient.ui;

import com.shelf.databaseclient.tools.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class SimpleAlert extends Alert {
    
    public static final ButtonType OK = new ButtonType("OK"),
                                   CANCEL = new ButtonType("Cancel"),
                                   YES = new ButtonType("Yes"),
                                   NO = new ButtonType("No");
    private static final int MAX_LENGHT = 500;
    
    public SimpleAlert( Alert.AlertType type, String message, ButtonType... buttons){
        super(type, message, buttons );
        setTitle( type==Alert.AlertType.ERROR ? "Error" : 
                  type==Alert.AlertType.WARNING ? "Warning" : "Information" );
        getDialogPane().setBorder( Tools.getBorder() );
        setHeaderText(null);
        ((Stage)getDialogPane().getScene().getWindow()).getIcons().add( new javafx.scene.image.Image(Setup.FILE_ICON) );
    }

    public SimpleAlert( Alert.AlertType type, String message ){
        this(type, "", SimpleAlert.OK );
        setContentText(message);
    }
    
    public SimpleAlert( Alert.AlertType type, Exception exception ){
        this(type, "", SimpleAlert.OK );
        if( exception.getMessage() == null ){
            setHeaderText( exception.toString() );
            StringBuilder builder = new StringBuilder("StackTrace: ");
            for (StackTraceElement element : exception.getStackTrace()) 
                builder.append(" -> ").append(element);
            setContentText( builder.length() > MAX_LENGHT ? 
                            builder.toString().substring(0,MAX_LENGHT)+"..." :
                            builder.toString() );
        }
        else{
            setContentText( exception.getMessage() );
        }
    }
    
}
