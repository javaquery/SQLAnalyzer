/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer;

import com.sqlanalyzer.analyzer.Analyzer;
import com.sqlanalyzer.database.service.DBService;
import com.sqlanalyzer.database.service.MSSQLServiceImpl;
import com.sqlanalyzer.database.service.MySQLServiceImpl;
import com.sqlanalyzer.database.service.PostgreSQLServiceImpl;
import com.sqlanalyzer.exception.SQLAnalyzerException;
import com.sqlanalyzer.executionplans.DefaultSQLPlan;
import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.util.CommonUtil;
import com.sqlanalyzer.util.Constants;
import com.sqlanalyzer.util.HTMLUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vicky.thakor
 */
public class SQLAnalyzer {

    private DBService dbService;
    private Configurator configurator;
    private static final DefaultConfigurator DEFAULT_CONFIGURATOR = new DefaultConfigurator();
    private Connection connection;
    private String sqlQuery = "NA";
    private String executionPlan;
    private Map<String, Object> metaData;
    private Analyzer analyzer;

    /* Save file */
    private boolean saveReport = false;
    private String path;
    private String prefix;
    private String suffix;

    public SQLAnalyzer() {
    }

    /**
     * Analyze ExecutionPlan or SQLQuery by using
     * {@link MSSQLServiceImpl}, {@link MySQLServiceImpl}, {@link PostgreSQLServiceImpl}
     *
     * @author vicky.thakor
     * @param dbService
     * @param configurator In case you hosted javascript, stylesheet and images required by {@link SQLAnalyzer} 
     */
    public SQLAnalyzer(Class<? extends DBService> dbService, Configurator configurator) {
        try {
            this.dbService = dbService.newInstance();
            if (configurator == null) {
                this.configurator = DEFAULT_CONFIGURATOR;
            } else {
                this.configurator = configurator;
            }
        } catch (Exception e) {
            throw new SQLAnalyzerException(Constants.DBSERVICE_INITIALIZATION_ERROR, e);
        }
    }

    /**
     * Prepare database {@link Connection} from given credentials in {@link DBService}
     * class or you can provide your own connection object using {@link SQLAnalyzer#usingConnection(java.sql.Connection)}.
     *
     * @author vicky.thakor
     * @since 1.0
     * @return
     */
    public SQLAnalyzer initDatabaseConnection() {
        if (dbService != null) {
            try {
                Class.forName(dbService.DatabaseDriver());
                connection = DriverManager.getConnection(dbService.DatabaseHost(), dbService.DatabaseUsername(), dbService.DatabasePassword());
            } catch (Exception e) {
                throw new SQLAnalyzerException(Constants.CONNECTION_ERROR, e);
            }
        }
        return this;
    }

    /**
     * Either use {@link SQLAnalyzer#initDatabaseConnection()} when you want to initialize 
     * connection object using credentials provided in {@link DBService} class  
     * or use this method when you want to set Connection explicitly.
     * @param connection 
     */
    public void usingConnection(Connection connection){
        this.connection = connection;
    }
    
    /**
     * Use to initialize database service.<br/>
     * Note: Use it only when you are not initializing it with constructor
     * SQLAnalyzer(DBService, Configurator).
     *
     * @param dbService
     */
    public void usingDBService(Class<? extends DBService> dbService) {
        try {
            this.dbService = dbService.newInstance();
        } catch (Exception e) {
            throw new SQLAnalyzerException(Constants.DBSERVICE_INITIALIZATION_ERROR, e);
        }
    }

    /**
     * Use to initialize configurator.<br/>
     * Note: Use it only when you are not initializing it with constructor
     * SQLAnalyzer(DBService, Configurator).
     * 
     * @param configurator 
     */
    public void usingConfigurator(Configurator configurator) {
        if (configurator == null) {
            this.configurator = DEFAULT_CONFIGURATOR;
        } else {
            this.configurator = configurator;
        }
    }

    /**
     * Generate report of given SQL Query.<br/>
     * Note: Either provide your database {@link Connection} object using
     * {@link SQLAnalyzer#usingConnection(java.sql.Connection)} or call {@link SQLAnalyzer#initDatabaseConnection()}
     * to get database {@link Connection} from your provided credentials.
     *
     * @author vicky.thakor
     * @since 1.0
     * @param sqlQuery
     * @return {@link SQLAnalyzer}
     */
    public SQLAnalyzer fromQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    /**
     * Generate report from ExecutionPlan 
     * (MSSQLServiceImpl - XML, MySQLServiceImpl - JSON, PostgreSQLServiceImpl - JSON)
     *
     * @author vicky.thakor
     * @param executionPlan
     * @return
     */
    public SQLAnalyzer fromExecutionPlan(String executionPlan) {
        this.executionPlan = executionPlan;
        return this;
    }

    /**
     * Save generated report to physical file.
     *
     * @author vicky.thakor
     * @param path
     * @param prefix
     * @param suffix
     * @return
     */
    public SQLAnalyzer save(String path, String prefix, String suffix) {
        saveReport = true;
        this.path = path;
        this.prefix = prefix;
        this.suffix = suffix;
        return this;
    }

    /**
     * Get analyzed report of query executed on database. <br/> <br/>
     * 
     * <b>Note: Report can be generated only when same query previously executed on that database.
     * In case of Analyzer can't find the execution plan. Execute same query multiple time so 
     * database will cache the execution plan and then report can be generated.</b>
     *
     * @author vicky.thakor
     * @return
     */
    public List<SQLPlan> generateReport() {
        analyzer = (Analyzer) dbService;
        List<SQLPlan> sqlPlans = new ArrayList<SQLPlan>(0);
        if (executionPlan != null && !executionPlan.trim().isEmpty()) {
            SQLPlan sqlPlan = new DefaultSQLPlan();
            /* Build HTML report from SQLPlan */
            String htmlReport = analyzer.parse(executionPlan);

            /* Set generated report */
            sqlPlan.setSQLPlan(executionPlan);
            CommonUtil.putAllNullCheck(analyzer.metaData(), sqlPlan.metaData());
            sqlPlan.setHTMLReport(prepareHTMLReport(htmlReport));

            if (saveReport) {
                String filename = prefix + suffix + ".html";
                metaData = sqlPlan.metaData();
                sqlPlan.reportFiles().add(saveHTMLReport(htmlReport, filename));
            }
            sqlPlans.add(sqlPlan);
        } else if (sqlQuery != null && !sqlQuery.trim().isEmpty()) {
            if (connection != null) {
                sqlPlans = analyzer.getExecutionPlans(connection, sqlQuery);
                if (sqlPlans != null && !sqlPlans.isEmpty()) {
                    for (SQLPlan sqlPlan : sqlPlans) {
                        /* Build HTML report from SQLPlan */
                        String htmlReport = analyzer.parse(sqlPlan.getSQLPlan());

                        /* Set generated report */
                        CommonUtil.putAllNullCheck(analyzer.metaData(), sqlPlan.metaData());
                        sqlPlan.setHTMLReport(prepareHTMLReport(htmlReport));

                        if (saveReport) {
                            String filename = prefix + suffix + ".html";
                            metaData = sqlPlan.metaData();
                            sqlPlan.reportFiles().add(saveHTMLReport(htmlReport, filename));
                        }
                    }
                }
            } else {
                throw new SQLAnalyzerException(Constants.INIT_DATABASE_CONNECTION, null);
            }
        } else {
            throw new SQLAnalyzerException("Provide valid execution plan or sql query to analyze", null);
        }
        return sqlPlans;
    }

    /**
     * Prepare functional HTML report
     *
     * @author vicky.thakor
     * @date 2nd May, 2016
     * @param htmlReport
     * @return
     */
    private String prepareHTMLReport(String htmlReport) {
        HTMLUtil.CSS_ICON_IMAGE = CommonUtil.value(configurator.iconImage(), DEFAULT_CONFIGURATOR.iconImage());

        StringBuilder report = new StringBuilder("");
        report.append(HTMLUtil.prepareStyleTag(configurator.styleSheet(), null));
        report.append(HTMLUtil.prepareScriptTag(configurator.jQuerySource(), DEFAULT_CONFIGURATOR.jQuerySource()));
        report.append(HTMLUtil.prepareScriptTag(configurator.jsPlumbSource(), DEFAULT_CONFIGURATOR.jsPlumbSource()));
        report.append(HTMLUtil.javaScripts());
        report.append(analyzer.stylesheet());
        report.append(htmlReport);
        return report.toString();
    }

    /**
     * Save report to file.
     *
     * @author vicky.thakor
     * @param htmlReport
     * @param filename
     * @return
     */
    private String saveHTMLReport(String htmlReport, String filename) {
        HTMLUtil.CSS_ICON_IMAGE = CommonUtil.value(configurator.iconImage(), DEFAULT_CONFIGURATOR.iconImage());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>").append("\n");
        stringBuilder.append("  <head>").append("\n");
        stringBuilder.append("      <title>").append(filename).append("</title>");
        stringBuilder.append("      ").append(HTMLUtil.prepareStyleTag(configurator.styleSheet(), null)).append("\n");
        stringBuilder.append(analyzer.stylesheet()).append("\n");
        stringBuilder.append(HTMLUtil.customReportStyleSheet()).append("\n");
        stringBuilder.append("  </head>").append("\n");
        stringBuilder.append("  <body>").append("\n");
        stringBuilder.append(HTMLUtil.prepareReportSidebar()).append("\n");
        stringBuilder.append("      ").append(HTMLUtil.prepareScriptTag(configurator.jQuerySource(), DEFAULT_CONFIGURATOR.jQuerySource()));
        stringBuilder.append("      ").append(HTMLUtil.prepareScriptTag(configurator.jsPlumbSource(), DEFAULT_CONFIGURATOR.jsPlumbSource()));
        stringBuilder.append(HTMLUtil.javaScripts()).append("\n");
        stringBuilder.append("").append(HTMLUtil.prepareReportBody(prepareReportProperties(htmlReport))).append("\n");
        stringBuilder.append("  </body>\n");
        stringBuilder.append("</html>\n");

        /* Save report to file */
        CommonUtil.copyStaticContent(configurator, path);
        return CommonUtil.saveFile(path, filename, stringBuilder.toString());
    }

    /**
     * Prepare map for internal purpose.
     *
     * @author vicky.thakor
     * @param htmlReport
     * @return
     */
    private Map<String, Object> prepareReportProperties(String htmlReport) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        map.put(HTMLUtil.DATABASE_ENGINE, analyzer.name());
        map.put(HTMLUtil.GRAPHICAL_DATA, htmlReport);
        map.put(HTMLUtil.QUERY, sqlQuery);
        map.put(HTMLUtil.METADATA, metaData);
        return map;
    }
}
