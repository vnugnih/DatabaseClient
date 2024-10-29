package com.shelf.databaseclient.tools;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class Tools {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    public static final String EMPTY = "";

    public static String getCoding() { return "UTF-8"; }
    public static String getCodingWin() { return "Cp1251"; }

    public static boolean isWindows(){ return System.getProperty("os.name").toLowerCase().contains("win"); }
    public static boolean isLinux(){   return System.getProperty("os.name").toLowerCase().contains("nix") || System.getProperty("os.name").toLowerCase().contains("nux"); }
    public static boolean isMac(){     return System.getProperty("os.name").toLowerCase().contains("mac"); }
    public static boolean isARM(){ return System.getProperty("os.arch").toLowerCase().contains("arm"); }
    public static boolean isX86(){ return System.getProperty("os.arch").toLowerCase().contains("x86"); }

    public static String getPath( String path ){
        return isWindows() ? path.replace("/", "\\") : path.replace("\\", "/");
    }

    public static String getCurrentPath(){
        return getPath( System.getProperty("user.dir")+"\\");
    }
    
    public static LocalDateTime formatDate( String date, String format ) { 
        return date == null ? null : LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
    }

    public static String formatDate( LocalDateTime date, String format ) { 
        return date == null ? null : date.format( DateTimeFormatter.ofPattern(format) );
    }

    public static String formatDate( String format ) {
        return formatDate( LocalDateTime.now(), format );
    }

    public static String formatDate() {
        return formatDate( LocalDateTime.now(), "yyyy.MM.dd HH:mm:ss.SSS" );
    }

    public static boolean isDate( String text, String format ) {
        try{
            LocalDateTime date = formatDate( text, format );
            return date != null && date.isAfter(Setup.DATE_MIN) && date.isBefore(Setup.DATE_MAX);
        }catch(Exception e){}
        return false;
    }
    
    public static boolean isEmpty( String text ){
        return text == null || text.trim().equals(EMPTY);
    }

    public static boolean isInteger( String text){
        try{
            Integer.parseInt( Tools.prepareNumber(text) );
            return true;
        }catch(Exception e){}
        return false;
    }
    
    public static boolean isLong( String text){
        try{
            Long.parseLong( Tools.prepareNumber(text) );
            return true;
        }catch(Exception e){}
        return false;
    }
    
    public static boolean isDouble( String text){
        try{
            Double.parseDouble( Tools.prepareNumber(text) );
            return true;
        }catch(Exception e){}
        return false;
    }

    public static int toInteger( String text ){
        return  Tools.isEmpty(text) || !Tools.isInteger( text ) ? 0 : Integer.parseInt( Tools.prepareNumber(text) );
    }

    public static long toLong( String text ){
        return  Tools.isEmpty(text) || !Tools.isLong( text ) ? 0 : Long.parseLong( Tools.prepareNumber(text) );
    }

    public static double toDouble( String text, double round ){
        return  Tools.isEmpty(text) || !Tools.isDouble( text ) ? 0.0 : Tools.round( Double.parseDouble( Tools.prepareNumber(text) ), round );
    }

    public static double toDouble( String text ){
        return  Tools.isEmpty(text) || !Tools.isDouble( text ) ? 0 : Double.parseDouble( Tools.prepareNumber(text) );
    }

    public static double round( double value, double round ){
        return Math.round( value * round ) / round;
    }
    
    public static double round( double value ){
        return Tools.round( value, Setup.DIV_DOUBLE );
    }
    
    public static String prepareNumber( String text){
        return text.trim().replace(',', '.').replace(" ", "");
    }
    
    public static byte[] readFile( File file) throws Exception {
        try (RandomAccessFile rafFile = new RandomAccessFile(file, "r")) {
            byte data[] = new byte[(int) rafFile.length()];
            rafFile.read(data);
            return data;
        } catch (Exception e) { throw e; }
    }

    public static void writeFile( File file, byte[] data ) {
        try ( RandomAccessFile rafFile = new RandomAccessFile(file, "rw") ) {
            rafFile.setLength(0);
            rafFile.write(data, 0, data.length);
        } catch (Exception e) { LOG.error(e); }
    }

    public static String StringToHTML(String text){
        return text == null ? null : text.replace("&", "&amp;")
                                         .replace("<", "&lt;").replace(">", "&gt;")
                                         .replace("'", "&apos;").replace("\"", "&quot;");
    }

    public static String StringToJson( String text ){
        return text == null ? null : text.replace("\'", "&apos;").replace("\"", "&quot;");
    }

    public static String JsonToString( String text ){
        return text == null ? null : text.replace( "&apos;", "\'").replace( "&quot;", "\"");
    }
    
    public static HashMap JsonToHashMap( String text ) { 
        try{
            return (HashMap)new org.json.simple.parser.JSONParser().parse( text ); 
        }catch(Exception e){ LOG.error(e); }
        return new HashMap();
    }
    
    public static String HashMapToJson( HashMap map ) { 
        return org.json.simple.JSONObject.toJSONString( map ); 
    }

    public static String cropString(String text, int length ){
        return text.length() > length ? text.substring(0, length) : String.format("%-"+length+"s",text);
    }

    public static String getThreadInfo( Thread thread ){
        return "thread=" + thread + 
               (thread == null ? "" : ", state="+thread.getState().name()+", id="+thread.getId());
    }
    
    public static Border getBorder(){
        return new Border( new BorderStroke(Color.LIGHTGRAY,BorderStrokeStyle.SOLID,null,null) );
    }

    public static void setFocus(javafx.scene.Node node){
        new Thread( new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                javafx.application.Platform.runLater( () -> node.requestFocus() );
                return null;
            } }, "SetFocusThread"  ).start();            
    }
    
}
