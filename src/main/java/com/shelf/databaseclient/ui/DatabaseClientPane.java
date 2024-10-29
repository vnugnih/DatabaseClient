package com.shelf.databaseclient.ui;

import com.shelf.databaseclient.tools.*;
import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.concurrent.*;
import javafx.concurrent.Worker.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;

public class DatabaseClientPane  extends SplitPane {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    private static final String SELECT = "SELECT",
                                DIV_SQL = ";", 
                                DIV_TEXT = "'", 
                                MASK = "~~~!!!~~~";
    private static final int TIMEOUT = 10;
    
    private Result result;
    private String database, sql;
    
    private SettingStage stageSetting;
    private final ChoiceBox choiceboxDatabase = new ChoiceBox();
    private final TextArea textareaSQL = new TextArea();
    private final CheckBox checkboxTable = new CheckBox("Table");
    private final BorderPane paneResult = new BorderPane();
    private final BorderPane paneProgress = new BorderPane();

    private final WebView webview = new WebView();
    private final TextArea textarea = new TextArea();
    private TableView<String[]> tableview = new TableView<>();

    private final Button buttonExecute = new Button("Execute", new ImageView(new Image("resource/perform.png",21,21,true,true)) ),
                         buttonDownload = new Button("Download", new ImageView(new Image("resource/download.png",21,21,true,true)) ),
                         buttonSetting = new Button("Setting", new ImageView(new Image("resource/setting.png",21,21,true,true)) );
    
    private static DatabaseClientPane pane;
    
    public static synchronized DatabaseClientPane getInstance() {
        if (pane == null) pane = new DatabaseClientPane();
        return pane;
    }

    public DatabaseClientPane(){
        super();
        setStyle("-fx-border-style: solid; -fx-border-color: grey; -fx-border-width: 1; -fx-padding: 8px; ");

        choiceboxDatabase.setStyle(" -fx-font-weight: bold;");
        checkboxTable.setSelected(true);
        paneProgress.setPrefSize(30, 30);
        paneResult.setStyle("-fx-padding: 4px 4px 4px 4px;");
        paneResult.setBorder( Tools.getBorder() );
        webview.setContextMenuEnabled( true );
        webview.getEngine().getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                public void changed(ObservableValue ov, State oldState, State newState) {
                    if (newState == State.SUCCEEDED) paneResult.setCenter( webview );
                }
            } );
        tableview.setTableMenuButtonVisible(true);
        tableview.setOnMouseClicked( (mouseEvent) -> actionClicked( mouseEvent, (String[])tableview.getSelectionModel().getSelectedItem() ) );
        textarea.setEditable( false );
        buttonExecute.setOnAction((actionEvent) -> execute() );
        buttonDownload.setOnAction((actionEvent) -> download() );
        buttonSetting.setOnAction((actionEvent) -> setting() );
        setOnKeyReleased( (KeyEvent) -> actionHotKey(KeyEvent) );        

        FlowPane paneDatabase = new FlowPane(8,8);
        paneDatabase.setStyle("-fx-padding: 8px;");
        paneDatabase.getChildren().addAll( new Label("Database"), choiceboxDatabase );

        FlowPane paneExecute = new FlowPane(8,8);
        paneExecute.setStyle("-fx-padding: 0px;");
        paneExecute.getChildren().addAll( checkboxTable, buttonExecute );

        FlowPane paneOther = new FlowPane(8,8);
        paneOther.setStyle("-fx-padding: 0px;");
        paneOther.getChildren().addAll( buttonDownload, buttonSetting );
        paneOther.setAlignment(Pos.CENTER_RIGHT);

        BorderPane panePerform = new BorderPane();
        panePerform.setStyle("-fx-padding: 8px;");
        panePerform.setLeft( paneExecute);
        panePerform.setCenter( paneProgress );
        panePerform.setRight( paneOther );

        BorderPane paneSet = new BorderPane();
        paneSet.setStyle("-fx-padding: 4px;");
        paneSet.setTop( paneDatabase );
        paneSet.setCenter( textareaSQL );
        paneSet.setBottom( panePerform );
        paneSet.setBorder( Tools.getBorder() );
        
        getItems().addAll( new SplitPane( paneSet ), new SplitPane( paneResult ) );
        setOrientation( Orientation.VERTICAL );
        setDividerPositions( 0.4, 0.6 );
        load();
    }
    
    public void load(){
        choiceboxDatabase.getItems().clear();
        choiceboxDatabase.getItems().addAll( Setup.getSetupKey() );
    }

    private void disable( boolean set){
        checkboxTable.setDisable(set);
        buttonExecute.setDisable(set);
        if( set ){
            paneResult.setCenter( null );
            paneProgress.setCenter( new ProgressIndicator() );
            MessagePane.getInstance().startTime();
        }
        else{ 
            paneProgress.setCenter( new Label() );
            MessagePane.getInstance().stopTime();
        }
    }
    
    private ArrayList<String> parseSQL( String sql ) throws Exception {
        ArrayList<String> sql_array = new ArrayList<>();
        boolean text = false;
        String sql_edit = "";
        for(int index = 0; index < sql.length(); index++){
            String symbol = sql.substring( index, index+1 );
            if( symbol.equals(DIV_TEXT) ) text = !text;
            sql_edit += symbol.equals(DIV_SQL) && text ? MASK : symbol;
        }
        for(String line : sql_edit.split(DIV_SQL)){
            line = line.trim().replace(MASK,DIV_SQL);
            if( !Tools.isEmpty(line) ) sql_array.add( line );
        }
        if( sql_array.isEmpty() ) throw new Exception("SQL command is empty");
        return sql_array;
    }
    
    private boolean isSelect( ArrayList<String> sql_array ) throws Exception {
        for( String line : sql_array )
            if( line.toUpperCase().startsWith(SELECT) )
                return true;
        return false;
    }
    
    private String getSelect( ArrayList<String> sql_array ) throws Exception {
        for( String line : sql_array )
            if( line.toUpperCase().startsWith(SELECT) )
                return line;
        throw new Exception("SQL command is empty (select)");
    }

    private void setError( Exception e ){
        textarea.appendText( e.getMessage() );
        paneResult.setCenter(textarea);
    }
    
    private void execute(){
        try{
            
            result = null;
            textarea.clear();
            System.gc();
            database = (String)choiceboxDatabase.getValue();
            if( Tools.isEmpty(database) ) throw new Exception("Database is not selected");
            sql = textareaSQL.getSelectedText();
            if( Tools.isEmpty(sql) ) sql = textareaSQL.getText();
            if( Tools.isEmpty(sql) ) throw new Exception("SQL text is empty");
            
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater( () -> disable( true ) ); 
                    try( Database db = new Database(database) ){
                        Platform.runLater( () -> MessagePane.getInstance().setStatus(" Connect ") );
                        db.connect();
                        Platform.runLater( () -> MessagePane.getInstance().setStatus(" Execute ") );
                        ArrayList<String> sql_array = parseSQL( sql );
                        if( isSelect(sql_array) )  executeSelect( db, getSelect(sql_array) );
                        else                       executeUpdate( db, sql_array );
                        Platform.runLater( () -> MessagePane.getInstance().setStatus(" Ready "));
                    }catch(Exception e){
                        LOG.error(e);
                        Platform.runLater( () -> MessagePane.getInstance().setStatus(" Error "));
                        Platform.runLater( () -> setError(e) );
                    }
                    Platform.runLater( () -> disable( false ) ); 
                    return null;
                } };
            new Thread(task,"ExecuteThread").start();            
            
        }catch(Exception e){
            LOG.error(e);
            MessagePane.getInstance().setStatus(" Error ");
            setError(e);
        }
    }
    
    private void executeSelect( Database db, String select) throws Exception {
        result = db.executeQuery( select );
        if( checkboxTable.isSelected() ){
            tableview = result.getTable( tableview );
            Platform.runLater( () -> {
                paneResult.setCenter( tableview );
                tableview.scrollTo(0);
            });
        }
        else{
            String html = result.getHTML();
            Platform.runLater( () -> webview.getEngine().loadContent( html ) );
        }
        Platform.runLater( () -> MessagePane.getInstance().setMessage(" Row count "+result.getData().size() ) );
    }
    
    private void executeUpdate( Database db, ArrayList<String> update ) throws Exception {
        Platform.runLater( () -> paneResult.setCenter( textarea ) );
        for( String command : update){
            Result result = db.executeUpdate( command );
            Platform.runLater( () -> textarea.appendText( command+"  ->  update "+result.getUpdate()+" row\n" ) );
            Thread.sleep(TIMEOUT);
        }
        db.commit();
        Platform.runLater( () -> textarea.appendText( "commit\n" ) );
        Platform.runLater( () -> MessagePane.getInstance().setMessage(" Update count "+update.size() ) );
    }
    
    private void download(){
        try{
            if( result == null ) throw new Exception("Result is empty");
            File file = new File( Tools.getCurrentPath()+"data-"+Tools.formatDate("yyyyMMddHHmmss")+".csv" );
            Tools.writeFile( file, result.getCSV().getBytes(Tools.getCodingWin()) );
            SimpleAlert alert = new SimpleAlert(
                    Alert.AlertType.INFORMATION, 
                    "File saved:\n"+file.getPath()+"\nOpen file?",         
                    SimpleAlert.OK, SimpleAlert.CANCEL );
            Optional<ButtonType> result_alert = alert.showAndWait();
            if (result_alert.isPresent() && result_alert.get() == SimpleAlert.OK){
                ArrayList<String> command = new ArrayList<>(
                        Arrays.asList( "cmd.exe", "/c", "start", "\"\"", "\""+file.getPath()+"\""  )
                );
                new ProcessBuilder(command).start();
            } 
        }catch(Exception e){
            LOG.error(e);
            MessagePane.getInstance().setStatus(" Error ");
            MessagePane.getInstance().setMessage(e.getMessage());
        }
    }

    private void setting(){
        try{
            if( stageSetting == null ) stageSetting = new SettingStage();
            stageSetting.load();
            stageSetting.showAndWait();
        }catch(Exception e){
            LOG.error(e);
            MessagePane.getInstance().setStatus(" Error ");
            MessagePane.getInstance().setMessage(e.getMessage());
        }
    }

    private void actionHotKey(KeyEvent event){
        if( event.getCode() == KeyCode.F5 ){ 
            execute();
            event.consume(); 
        }
    }

    private void actionClicked( MouseEvent mouseEvent, String[] record ){
        try{
            if(mouseEvent.getClickCount() == 2){
                if( result == null ) throw new Exception("Result is empty");
                if( record == null )  throw new Exception("Record not selected");
                ViewStage stageView = new ViewStage();
                stageView.set( result.getName(), record );
                stageView.show();
            }
        }catch(Exception e){
            LOG.error(e);
            new SimpleAlert(Alert.AlertType.ERROR, e).show();
        }
    }

}
