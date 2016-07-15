package com.sqlanalyzer.database.service;

/**
 * DBService connection properties
 * @author vicky.thakor
 */
public interface DBService{
    /* Driver to connect datDatabaseConnectorabase. */
    public String DatabaseDriver();
    
    /* DBService host */
    public String DatabaseHost();
    
    /* Username for database connection */
    public String DatabaseUsername();
    
    /* Password for database connection */
    public String DatabasePassword();
}
