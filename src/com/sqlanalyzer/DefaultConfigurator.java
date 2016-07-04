/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer;

/**
 * @author vicky.thakor
 * @date 2nd May, 2016
 */
public class DefaultConfigurator implements Configurator{

    public static String jQuerySource = "js/jquery-1.8.2.min.js";
    public static String jsPlumbSource = "js/jquery.jsPlumb-1.3.3-all.js";
    public static String iconImage = "SQLAnalyzerIconImage.png";
    
    @Override
    public String jQuerySource() {
        return jQuerySource;
    }

    @Override
    public String jsPlumbSource() {
        return jsPlumbSource;
    }

    @Override
    public String styleSheet() {
        return null;
    }

    @Override
    public String iconImage() {
        return iconImage;
    }
}
