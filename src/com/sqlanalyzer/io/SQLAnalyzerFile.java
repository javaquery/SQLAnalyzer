/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.io;

import java.io.File;
import java.net.URI;

/**
 * SQLAnalyzer file.
 * @author vicky.thakor
 * @since 
 */
public class SQLAnalyzerFile extends File{

    public SQLAnalyzerFile(String pathname) {
        super(pathname);
    }

    public SQLAnalyzerFile(String parent, String child) {
        super(parent, child);
    }
    
    public SQLAnalyzerFile(File parent, String child) {
        super(parent, child);
    }

    public SQLAnalyzerFile(URI uri) {
        super(uri);
    }
}
