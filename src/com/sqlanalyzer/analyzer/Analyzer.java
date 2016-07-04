/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.analyzer;

import com.sqlanalyzer.executionplans.SQLPlan;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author vicky.thakor
 * @date 24th May, 2016
 */
public interface Analyzer {
    public String name();
    public List<SQLPlan> getExecutionPlans(Connection connection, String sqlQuery);
    public String parse(String executionPlan);
    public Map<String, Object> metaData();
    public String stylesheet();
}
