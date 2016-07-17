/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.test;

import com.sqlanalyzer.database.service.MSSQLServiceImpl;

/**
 * @author vicky.thakor
 */
public class MSSQLApi extends MSSQLServiceImpl{

    @Override
    public String DatabaseDriver() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }

    @Override
    public String DatabaseHost() {
        return "jdbc:jtds:sqlserver://localhost:1433/sqlanalyzer";
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
