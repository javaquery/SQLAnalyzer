/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.executionplans;

import com.sqlanalyzer.io.SQLAnalyzerFile;
import java.util.Map;

/**
 * List&lt;String&gt; reportFiles changed to SQLAnalyzerFile reportFile();
 * @author vicky.thakor
 * @since 2016-12-22
 */
public interface SQLPlan {
    public String getSQLPlan();
    public void setSQLPlan(String SQLPlan);
    public String getHTMLReport();
    public void setHTMLReport(String HTMLReport);
    public Map<String, Object> metaData();
    public SQLAnalyzerFile getReportFile();
    public void setReportFile(SQLAnalyzerFile file);
}
