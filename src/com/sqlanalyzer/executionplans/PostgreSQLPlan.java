/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.executionplans;

import com.sqlanalyzer.io.SQLAnalyzerFile;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vicky.thakor
 * @date 13th June, 2016
 */
public class PostgreSQLPlan implements SQLPlan {

    private String SQLPlan;
    private String HTMLReport;
    private final Map<String, Object> metaData = new HashMap<String, Object>(0);
    private SQLAnalyzerFile reportFile;

    @Override
    public String getSQLPlan() {
        return this.SQLPlan;
    }

    @Override
    public void setSQLPlan(String SQLPlan) {
        this.SQLPlan = SQLPlan;
    }

    @Override
    public String getHTMLReport() {
        return this.HTMLReport;
    }

    @Override
    public void setHTMLReport(String HTMLReport) {
        this.HTMLReport = HTMLReport;
    }

    @Override
    public Map<String, Object> metaData() {
        return metaData;
    }

    @Override
    public SQLAnalyzerFile getReportFile() {
        return this.reportFile;
    }

    @Override
    public void setReportFile(SQLAnalyzerFile reportFile) {
        this.reportFile = reportFile;
    }
}
