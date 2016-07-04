/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

/**
 * @author vicky.thakor
 * @date 24th May, 2015
 */
public class SQLUtil {
            
    /**
     * @author vicky.thakor
     * @param query
     * @return {@link String} value or null in case of null query Replace ? with
     * @P0,
     * @P1, etc...
     */
    public static String replaceQuestionMarkWithP(String query) {
        if (query instanceof String) {
            int position;
            int count = 0;
            String frontPortion;
            String rearPortion;
            while (query.contains("?")) {
                position = query.indexOf("?");
                frontPortion = query.substring(0, position + 1);
                rearPortion = query.substring(position + 1, query.length());
                frontPortion = frontPortion.replace("?", " @P" + count + " ");
                count++;
                query = frontPortion + rearPortion;
            }
            return query;
        }
        return null;
    }
}
