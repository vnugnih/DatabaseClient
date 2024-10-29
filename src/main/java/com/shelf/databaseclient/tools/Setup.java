package com.shelf.databaseclient.tools;

import java.io.*;
import java.time.*;
import java.util.*;

public class Setup {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    public static final String FILE_SETUP = Tools.getCurrentPath()+"database-client-setup.json";
    public static final String FILE_LOG4J = Tools.getCurrentPath()+"log4j2.xml";
    public static final LocalDateTime DATE_MIN = LocalDateTime.of(0001,01,01,00,00,00);
    public static final LocalDateTime DATE_MAX = LocalDateTime.of(9999,12,31,23,59,59);
    public static final String FILE_ICON = "resource/database.png";
    public static final String FILE_SKIN = "resource/user-skin.css";
    public static final double SCREEN_PART = 0.8;
    public static final double DIV_DOUBLE = 1000000.0;
    public static final String FONT_FAMILY = "Roboto";
    public static final int FONT_SIZE = 13;

    public static String loadSetup(){
        try { 
            File file = new File(Setup.FILE_SETUP);
            if( file.exists() && file.canRead() )
                return new String( Tools.readFile( file ), Tools.getCoding() );
        } catch (Exception e) { LOG.error(e); }
        return "{}";
    }
    
    public static void saveSetup( String setup ){
        try { 
            File file = new File(Setup.FILE_SETUP);
            Tools.writeFile( file, setup.getBytes(Tools.getCoding()) );
            LOG.info("Setup.saveSetup");
        } catch (Exception e) { LOG.error(e); }
    }
    
    public static synchronized HashMap getSetup(){
        return (HashMap)Tools.JsonToHashMap( loadSetup() );
    }
    
    public static HashMap getSetup( String name ){
        return (HashMap)getSetup().getOrDefault( name, new HashMap() );
    }
    
    public static ArrayList<String> getSetupKey(){
        ArrayList<String> array = new ArrayList<>();
        new TreeMap(getSetup()).keySet().forEach((key) -> array.add( (String)key ) );
        return array;
    }
    
    public static void loadLogConfig(){
        org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        context.setConfigLocation(  new java.io.File(Setup.FILE_LOG4J).toURI() );
    }
    
}