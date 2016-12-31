/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer;

import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.test.MSSQLApi;
import com.sqlanalyzer.util.FileUtil;
import java.awt.Desktop;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

/**
 * @author vicky.thakor
 * @since 2016-12-31
 */
public class SQLAnalyzerTest {

    @Test
    public void MSSQLQuery() throws IOException {
        List<SQLPlan> sqlPlans = new SQLAnalyzer(MSSQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select * from user_master this_ inner join message messages1_ on this_.id=messages1_.user_id inner join creditcard creditcard2_ on this_.id=creditcard2_.user_id where this_.email = 'vicky.thakor@javaquery.com'")
                .save("D:\\SQLAnalyzer\\MSSQL", "MSSQL", "Query")
                .generateReport();

        for (SQLPlan sqlPlan : sqlPlans) {
            Desktop.getDesktop().open(sqlPlan.getReportFile());
        }
    }

    @Test
    public void MSSQLPlan() throws IOException {
        String plan = new FileUtil().readFile("/com/sqlanalyzer/samplefile/MSSQLExecutionPlan1.sqlplan");
        List<SQLPlan> sQLPlans = new SQLAnalyzer(MSSQLApi.class, null)
                .fromExecutionPlan(plan)
                .save("D:\\SQLAnalyzer\\MSSQL", "MSSQL", "Plan")
                .generateReport();

        for (SQLPlan sqlPlan : sQLPlans) {
            Desktop.getDesktop().open(sqlPlan.getReportFile());
        }
    }

    @Test
    public void MySQLQuery() throws IOException {
        List<SQLPlan> sQLPlans = new SQLAnalyzer(MySQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select * from user_master this_ inner join message messages1_ on this_.id=messages1_.user_id inner join creditcard creditcard2_ on this_.id=creditcard2_.user_id where this_.email='vicky.thakor@javaquery.com'")
                .save("D:\\SQLAnalyzer\\MySQL", "MySQL", "Query")
                .generateReport();

        for (SQLPlan sqlPlan : sQLPlans) {
            Desktop.getDesktop().open(sqlPlan.getReportFile());
        }
    }

    @Test
    public void PostgreSQL() throws IOException {
        List<SQLPlan> sQLPlans = new SQLAnalyzer(PostgreSQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select * from user_master this_ inner join message messages1_ on this_.id=messages1_.user_id inner join creditcard creditcard2_ on this_.id=creditcard2_.user_id where this_.email='vicky.thakor@javaquery.com'")
                .save("D:\\SQLAnalyzer\\PostgreSQL", "PostgreSQL", "Query")
                .generateReport();

        for (SQLPlan sqlPlan : sQLPlans) {
            Desktop.getDesktop().open(sqlPlan.getReportFile());
        }
    }
}
