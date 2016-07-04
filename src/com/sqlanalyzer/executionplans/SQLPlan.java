/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.executionplans;

import java.util.List;
import java.util.Map;

public interface SQLPlan {
    public String getSQLPlan();
    public void setSQLPlan(String SQLPlan);
    public String getHTMLReport();
    public void setHTMLReport(String HTMLReport);
    public Map<String, Object> metaData();
    public List<String> reportFiles();
}
