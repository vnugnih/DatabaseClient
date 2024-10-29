package com.shelf.databaseclient.ui;

import com.shelf.databaseclient.tools.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

public class SettingStage extends Stage {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    private final TextArea textarea = new TextArea();
    private final Button buttonSave = new Button("Save", new ImageView(new Image("resource/save.png",21,21,true,true)) ),
                         buttonCancel = new Button("Cancel", new ImageView(new Image("resource/cancel.png",21,21,true,true)) ); 
    
    public SettingStage(){
        super();
        
        initOwner( DatabaseClient.getInstance().getStage() );
        setTitle( "Setting" );
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);
        getIcons().add( new javafx.scene.image.Image(Setup.FILE_ICON) );

        buttonSave.setOnAction( (actionEvent) -> save() );
        buttonCancel.setOnAction( (actionEvent) -> close() );
        this.setOnCloseRequest((actionEvent) -> close() );
        textarea.setPrefSize(600, 400);
        
        FlowPane paneButton = new FlowPane(8,8);
        paneButton.setStyle("-fx-padding: 8px;");
        paneButton.getChildren().addAll( buttonSave, buttonCancel );
        
        BorderPane pane = new BorderPane();
        pane.setCenter( textarea );
        pane.setBottom( paneButton );
        pane.setBorder(Tools.getBorder());
        
        Scene scene = new Scene( pane );
        scene.getStylesheets().add( Setup.FILE_SKIN );        
        setScene(scene);
        sizeToScene();
    }
    
    private void save(){
        try{
            Setup.saveSetup( textarea.getText() );
            DatabaseClientPane.getInstance().load();
            close();
        }catch(Exception e){
            LOG.error(e);
            new SimpleAlert(Alert.AlertType.ERROR, e).show();
        }
    }
    
    public void load(){
        try{
            textarea.setText( Setup.loadSetup() );
        }catch(Exception e){
            LOG.error(e);
            new SimpleAlert(Alert.AlertType.ERROR, e).show();
        }
    }

}
