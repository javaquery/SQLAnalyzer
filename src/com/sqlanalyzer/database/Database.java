package com.sqlanalyzer.database;

import java.sql.Connection;

/**
 * Database connection properties
 * @author vicky.thakor
 */
public interface Database {
    /* Driver to connect datDatabaseConnectorabase. */
    public String DatabaseDriver();
    
    /* Database host */
    public String DatabaseHost();
    
    /* Username for database connection */
    public String DatabaseUsername();
    
    /* Password for database connection */
    public String DatabasePassword();
    
    /* Provide your custom database connection object */
    public Connection DatabaseConnection();
}
