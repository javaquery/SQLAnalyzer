/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.database.mssql;

/**
 * @author vicky.thakor
 * @date 22nd June, 2016
 */
public class MSSQLIcon {
    private String operation;
    private String title;
    private String image;

    public MSSQLIcon(String operation, String title, String image) {
        this.operation = operation;
        this.title = title;
        this.image = image;
    }
    
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
