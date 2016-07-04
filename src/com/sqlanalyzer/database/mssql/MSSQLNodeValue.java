/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.database.mssql;

/**
 * @author vicky.thakor
 * @date 1st July, 2016
 */
public class MSSQLNodeValue {

    private final String caption;
    private final MSSQLIcon mssqlIcon;

    public MSSQLNodeValue(String caption, MSSQLIcon mssqlIcon) {
        this.caption = caption;
        this.mssqlIcon = mssqlIcon;
    }

    public String getCaption() {
        return caption;
    }

    public MSSQLIcon getMssqlIcon() {
        return mssqlIcon;
    }
}
