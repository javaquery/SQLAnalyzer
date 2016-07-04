/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer;

import java.util.Map;

/**
 * @author vicky.thakor
 * @date 24th May, 2016
 */
public interface SQLReport {
    public String getSQLPlan();
    public void setSQLPlan(String SQLPlan);
    public Map<String, Object> getMetaData();
    public void setMetaData(Map<String, Object> MetaData);
}
