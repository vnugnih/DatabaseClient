package com.shelf.databaseclient.ui;

import com.shelf.databaseclient.tools.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DatabaseClient extends Application {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
    
    private Stage stageRoot;
    private BorderPane paneRoot;
    private static DatabaseClient application;
    
    public static DatabaseClient getInstance(){  return application; }

    @Override
    public void init() throws Exception {
        LOG.info("start application {}", this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try{
            
            paneRoot = new BorderPane();
            paneRoot.setBorder(Tools.getBorder());
            
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            double width_screen =  Math.round(screen.getWidth()*Setup.SCREEN_PART),
                   height_screen = Math.round(screen.getHeight()*Setup.SCREEN_PART);
            Scene scene = new Scene(paneRoot, Math.min( width_screen, screen.getWidth()), 
                                              Math.min( height_screen, screen.getHeight() ) ); 
            scene.getStylesheets().add( Setup.FILE_SKIN );
            this.stageRoot = stage;
            stageRoot.setOnCloseRequest((windowEvent) -> Platform.exit());
            stageRoot.getIcons().add( new javafx.scene.image.Image(Setup.FILE_ICON) );
            stageRoot.setScene(scene);
            stageRoot.setTitle( toString() );
            stageRoot.setResizable(true);
            stageRoot.sizeToScene();
            stageRoot.show();
            
            application = this;

            paneRoot.setCenter( DatabaseClientPane.getInstance() );
            paneRoot.setBottom( MessagePane.getInstance() );

        } catch(Exception e){
            LOG.error(e);
            new SimpleAlert(Alert.AlertType.ERROR, e).show();
        }
    }

    @Override
    public void stop() throws Exception {
        LOG.info("stop application {}", this);
    }
    
    public Stage getStage(){ return stageRoot; }

    public static String getName(){ return "DatabaseClient"; }
    public static String getVersion(){ return "1.01"; } 

    @Override
    public String toString(){
        return getName()+" "+getVersion();
    }

    public static void main(String[] args) {
        Setup.loadLogConfig();
        Application.launch( DatabaseClient.class, args );        
    }
    
}
