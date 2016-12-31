/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

import com.sqlanalyzer.exception.SQLAnalyzerException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Vicky
 */
public class FileUtil {

    /**
     * Read file from package.
     * @param path
     * @return 
     */
    public String readFile(String path) {
        StringBuilder stringBuilder = new StringBuilder("");
        String line;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            /* Read resource file */
            inputStream = getClass().getResourceAsStream(path);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.getProperty("line.separator"));
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLAnalyzerException("", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    throw new SQLAnalyzerException("", e);
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    throw new SQLAnalyzerException("", e);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println();
    }
}
