package com.shelf.databaseclient.ui;

import com.shelf.databaseclient.tools.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.scene.web.*;

public class ViewStage extends Stage {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    private final WebView webview = new WebView();
    private final Button buttonCancel = new Button("Cancel", new ImageView(new Image("resource/cancel.png",21,21,true,true)) ); 
    
    public ViewStage(){
        super();
        
        initOwner( DatabaseClient.getInstance().getStage() );
        setTitle( "View" );
        initModality(Modality.NONE);
        initStyle(StageStyle.DECORATED);
        getIcons().add( new javafx.scene.image.Image(Setup.FILE_ICON) );

        buttonCancel.setOnAction( (actionEvent) -> close() );
        this.setOnCloseRequest((actionEvent) -> close() );
        webview.setPrefSize(600, 400);
        
        FlowPane paneButton = new FlowPane(8,8);
        paneButton.setStyle("-fx-padding: 8px;");
        paneButton.getChildren().addAll( buttonCancel );
        
        BorderPane pane = new BorderPane();
        pane.setCenter( webview );
        pane.setBottom( paneButton );
        pane.setBorder(Tools.getBorder());
        pane.setOnKeyReleased((keyEvent) -> {if( keyEvent.getCode() == KeyCode.ESCAPE ) close();} );
        
        Scene scene = new Scene( pane );
        scene.getStylesheets().add( Setup.FILE_SKIN );        
        setScene(scene);
        sizeToScene();
    }
    
    public void set( String[] name, String[] data ){
        try{
            StringBuilder builder = new StringBuilder();
            builder.append( Result.getHTMLHeader() )
                    .append("<body>\n")
                    .append("<table>\n");
            for( int index = 0; index < name.length; index++ )
                builder.append("<tr>")
                       .append("<td>").append(name[index]).append("</td>")
                       .append("<td>").append(Tools.StringToHTML(data[index])).append("</td>")
                       .append("<tr>");
            builder.append("</table>\n")
                   .append("</body>")
                   .append("</html>");
            webview.getEngine().loadContent( builder.toString() );
        }catch(Exception e){
            LOG.error(e);
            new SimpleAlert(Alert.AlertType.ERROR, e).show();
        }
    }
    
}
