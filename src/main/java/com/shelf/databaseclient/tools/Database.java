package com.shelf.databaseclient.tools;

import java.sql.*;
import java.util.*;

public class Database implements AutoCloseable {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
    
    private final Properties properties = new Properties();
    private Connection connection;
    private Statement statement;
    
    public Database( String name ){
        HashMap map = (HashMap)Setup.getSetup(name);
        properties.put("driver", map.getOrDefault("driver", "") );
        properties.put("url", map.getOrDefault("url", "") );
        properties.put("user", map.getOrDefault("user", "") );
        properties.put("password", map.getOrDefault("password", "") );
        properties.put("useUnicode", "true");
        LOG.info("Database name: {}",name);
    }
    
    public void connect() throws Exception {
        Class.forName(properties.getProperty("driver")).newInstance();
        connection = DriverManager.getConnection(properties.getProperty("url"), properties);
        connection.setAutoCommit(false);
        statement = connection.createStatement();
    }

    @Override
    public void close(){
        try { 
            if (statement != null)  statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) { LOG.error(e); }
    }
    
    public void commit() throws Exception {
        if (connection != null) connection.commit();
    }

    public void rollback() throws Exception {
        if (connection != null) connection.rollback();
    }

    public synchronized Result executeUpdate( String sql ) throws Exception {
        int result = Result.ERROR;
        try {
            result = statement.executeUpdate(sql);
            LOG.info("Database.executeUpdate: {}",sql);
        } catch (Exception e) {  throw new Exception(e.toString() + " sql:" + sql); }
        return new Result().setUpdate(result);
    }
    
    public synchronized Result executeQuery( String sql ) throws Exception {
        Result result = new Result();
        try( ResultSet resultSet = statement.executeQuery(sql)){
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String name[] = new String[columnCount];
            int type[] = new int[columnCount];
            for (int column = 1; column <= columnCount; column++){
                name[column - 1] = metaData.getColumnName(column);
                type[column - 1] = 
                        metaData.getColumnType(column) == Types.BIGINT || 
                        metaData.getColumnType(column) == Types.INTEGER || 
                        metaData.getColumnType(column) == Types.SMALLINT ? 
                        Result.LONG :
                        metaData.getColumnType(column) == Types.DECIMAL || 
                        metaData.getColumnType(column) == Types.DOUBLE || 
                        metaData.getColumnType(column) == Types.FLOAT || 
                        metaData.getColumnType(column) == Types.NUMERIC || 
                        metaData.getColumnType(column) == Types.REAL ? 
                        Result.DOUBLE :
                        metaData.getColumnType(column) == Types.CHAR || 
                        metaData.getColumnType(column) == Types.LONGVARCHAR || 
                        metaData.getColumnType(column) == Types.VARCHAR ? 
                        Result.STRING :
                        Result.OBJECT;
            }
            result.setName( name );
            result.setType( type );
            ArrayList<String[]> data = new ArrayList<>();
            while (resultSet.next()) {
                String row[] = new String[columnCount];
                for (int column = 1; column <= columnCount; column++) 
                    row[column - 1] = resultSet.getString(column);
                data.add(row);
            }
            result.setData(data);
            LOG.info("Database.executeQuery: {}",sql);
        } catch (Exception e) {
            throw new Exception(e.toString() + " sql:" + sql);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "Database{"+properties+"}";
    }

}
