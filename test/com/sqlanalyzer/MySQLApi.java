/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer;

import com.sqlanalyzer.database.service.MySQLServiceImpl;

/**
 * @author vicky.thakor
 */
public class MySQLApi extends MySQLServiceImpl{

    @Override
    public String DatabaseDriver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String DatabaseHost() {
        return "jdbc:mysql://localhost:3306/sqlanalyzer";
    }

    @Override
    public String DatabaseUsername() {
        return "root";
    }

    @Override
    public String DatabasePassword() {
        return "root";
    }
}
