/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.test;

import com.sqlanalyzer.database.PostgreSQL;
import java.sql.Connection;

/**
 * @author vicky.thakor
 */
public class PostgreSQLApi extends PostgreSQL{

   @Override
    public String DatabaseDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String DatabaseHost() {
        return "jdbc:postgresql://localhost:5432/sqlanalyzer";
    }

    @Override
    public String DatabaseUsername() {
        return "postgres";
    }

    @Override
    public String DatabasePassword() {
        return "root";
    }

    @Override
    public Connection DatabaseConnection() {
        return null;
    }
    
}
