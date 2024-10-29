package com.shelf.databaseclient.tools;

import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.*;
import javafx.util.Callback;

public class Result extends HashMap {
    
    public static final int OBJECT = 0;
    public static final int LONG = 1;
    public static final int DOUBLE = 2;
    public static final int STRING = 3;
    public static final int ERROR = -1;

    public Result setName( String[] value){ put("name", value); return this;}
    public String[] getName(){ return (String[])getOrDefault("name", new String[]{}); }

    public Result setType( int[] value){ put("type", value); return this;}
    public int[] getType(){ return (int[])getOrDefault("type", new int[]{}); }

    public Result setData( ArrayList<String[]> value){ put("data", value); return this; }
    public ArrayList<String[]> getData(){ return (ArrayList<String[]>)getOrDefault("data", new ArrayList<String[]>()); }
    
    public Result setUpdate( Integer value){ put("update", value); return this; }
    public Integer getUpdate(){ return (Integer)getOrDefault("update", ERROR); }
    
    public String getHTML(){
        StringBuilder builder = new StringBuilder();
        builder.append( getHTMLHeader() )
               .append("<body>\n")
               .append("<table>\n");
        builder.append("<tr>\n");
        for(String name : getName() )
            builder.append("<th>").append(name).append("</th>\n");
        builder.append("</tr>\n");
        for(String[] row : getData() ){
            builder.append("<tr>\n");
            for(int index = 0; index < row.length; index++ )
                builder.append("<td")
                       .append( getType()[index] == LONG || getType()[index] == DOUBLE ? " align='right'>" : ">")
                       .append( Tools.StringToHTML( row[index]) )
                       .append("</td>\n");
            builder.append("</tr>\n");
        }
        builder.append("</table>\n")
               .append("</body>")
               .append("</html>");
        return builder.toString();
    }
    
    public String getCSV(){
        StringBuilder builder = new StringBuilder();
        for(String name : getName() )
            builder.append("\"").append( name ).append("\";");
        builder.append("\n");
        for(String[] row : getData() ){
            for(String data : row )
                builder.append("\"").append( data ).append("\";");
            builder.append("\n");
        }
        return builder.toString();
    }
    
    public TableView getTable( TableView tableview ){
        tableview.getColumns().clear();
        for( int index = 0; index < getName().length; index++ ){
            TableColumn tablecolumn = new TableColumn( getName()[index] );
            final int column = index;
            if( getType()[index] == LONG ){
                tablecolumn.setCellValueFactory( 
                        new Callback< CellDataFeatures<String[], Number>, ObservableValue<Number> >() {
                            public ObservableValue<Number> call(CellDataFeatures<String[], Number> c) {
                                return new SimpleLongProperty( Tools.toLong(c.getValue()[column]) );
                            }}); 
                tablecolumn.setStyle("-fx-alignment: baseline-right;");
            }
            else if( getType()[index] == DOUBLE ){
                tablecolumn.setCellValueFactory( 
                        new Callback< CellDataFeatures<String[], Number>, ObservableValue<Number> >() {
                            public ObservableValue<Number> call(CellDataFeatures<String[], Number> c) {
                                return new SimpleDoubleProperty( Tools.toDouble(c.getValue()[column]) );
                            }}); 
                tablecolumn.setStyle("-fx-alignment: baseline-right;");
            }
            else
                tablecolumn.setCellValueFactory( 
                        new Callback< CellDataFeatures<String[], String>, ObservableValue<String> >() {
                            public ObservableValue<String> call(CellDataFeatures<String[], String> c) {
                                return new SimpleStringProperty( c.getValue()[column] );
                            }}); 
            tableview.getColumns().add(tablecolumn);
        }
        tableview.setItems( FXCollections.observableArrayList( getData() ) );
        return tableview;
    }
    
    public static String getHTMLHeader(){
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>\n")
               .append("<html lang='ru' xmlns='http://www.w3.org/1999/xhtml'>\n" )
               .append("<head>\n")
               .append("<title>Report</title>\n")
               .append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n")
               .append("<style type='text/css'>\n")
               .append("body { font-family: ").append(Setup.FONT_FAMILY).append("; font-size: ").append(Setup.FONT_SIZE).append("px; } \n")                
               .append("table { font-size: ").append(Setup.FONT_SIZE).append("px; border-spacing: 0px; border-collapse: collapse; } \n") //width: 100%; 
               .append("th { padding: 1px 2px 1px 2px; vertical-align: top; border: 1px solid black; text-align: center; font-weight: bold; } \n")
               .append("td { padding: 1px 2px 1px 2px; vertical-align: top; border: 1px solid black; } \n")
               .append("</style>\n")
               .append("</head>\n");
        return builder.toString();
    }

}
