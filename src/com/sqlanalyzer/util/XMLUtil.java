/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

import com.sqlanalyzer.exception.SQLAnalyzerException;
import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author vicky.thakor
 * @date 15th May, 2016
 */
public class XMLUtil {

    /**
     * Get XML document from String or File.
     * @param fromString
     * @param fromFile
     * @return 
     */
    public static Document getDocument(String fromString, String fromFile) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if(fromString != null && !fromString.trim().isEmpty()){
                InputSource inputSource = new InputSource(new StringReader(fromString));
                return documentBuilder.parse(inputSource);
            }else if(fromFile != null && !fromFile.trim().isEmpty()){
                File file = new File(fromFile);
                if(file.exists()){
                    return documentBuilder.parse(file);
                }
            }
        } catch (Exception e) {
            throw new SQLAnalyzerException(Constants.XML_PARSE_ERROR, e);
        }
        return null;
    }
}
