/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.test;

import com.sqlanalyzer.SQLAnalyzer;
import com.sqlanalyzer.executionplans.SQLPlan;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vicky
 */
public class Test {

    public static void main(String[] args) {
//        MSSQLSample();
        MySQLSample();
//        PostgreSQLSample();
    }

    public static void MSSQLSample() {
        String sqlPlanPath = "D:\\SQLAnalyzer\\Test.sqlplan";
        String plan = readFile(sqlPlanPath);
        try {
            List<SQLPlan> sQLPlans = new SQLAnalyzer(MSSQLApi.class, null)
                    .fromExecutionPlan(plan)
                    .save("D:\\SQLAnalyzer\\MSSQL", "prefix", "suffix")
                    .generateReport();

            for (SQLPlan sqlPlan : sQLPlans) {
                System.out.println(sqlPlan.getHTMLReport());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MySQLSample() {
        List<SQLPlan> sQLPlans = new SQLAnalyzer(MySQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select um.image_small from user_master um left join profile_master pm on um.uid = um.uid")
                .save("D:\\SQLAnalyzer\\MySQL", "prefix", "suffix")
                .generateReport();
        for (SQLPlan sqlPlan : sQLPlans) {
            System.out.println(sqlPlan.getHTMLReport());
        }
    }

    public static void PostgreSQLSample() {
        List<SQLPlan> sQLPlans = new SQLAnalyzer(PostgreSQLApi.class, null)
                .initDatabaseConnection()
                .fromQuery("select um.image_small from user_master um left join profile_master pm on um.uid = um.uid")
                .save("D:\\SQLAnalyzer\\PostgreSQL", "prefix", "suffix")
                .generateReport();
        for (SQLPlan sqlPlan : sQLPlans) {
            System.out.println(sqlPlan.getHTMLReport());
        }
    }

    public static String readFile(String path) {
        /* Create object of File. */
        File objFile = new File(path);
        StringBuilder stringBuilder = new StringBuilder();
        /* Create object of FileInputStream */
        FileInputStream objFileInputStream = null;
        try {
            /**
             * A FileInputStream obtains input bytes from a file in a file
             * system. What files are available depends on the host environment.
             *
             * FileInputStream is meant for reading streams of raw bytes such as
             * image data. For reading streams of characters, consider using
             * FileReader.
             */
            objFileInputStream = new FileInputStream(objFile);

            /* Read content of File. */
            int byteOfData;
            while ((byteOfData = objFileInputStream.read()) != -1) {
                /* Print content of File. */
                stringBuilder.append((char) byteOfData);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            /* Close the FileInputStream */
            if (objFileInputStream != null) {
                try {
                    objFileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
