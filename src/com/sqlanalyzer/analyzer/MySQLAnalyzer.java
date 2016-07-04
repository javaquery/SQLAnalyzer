/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.analyzer;

import com.sqlanalyzer.exception.SQLAnalyzerException;
import com.sqlanalyzer.executionplans.MySQLPlan;
import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.util.Constants;
import com.sqlanalyzer.util.jsPlumb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author vicky.thakor
 * @date 24th May, 2016
 */
public class MySQLAnalyzer implements Analyzer {

    protected MySQLAnalyzer() {
    }

    private static final Logger logger = Logger.getLogger(MySQLAnalyzer.class.getName());
    private final StringBuilder stringBuilderHTMLReport = new StringBuilder("");

    enum MySQLOperation {

        ordering_operation, grouping_operation, nested_loop, table;
    }

    enum MySQLTableProperties {

        table_name, access_type, possible_keys, key, used_key_parts,
        key_length, ref, rows, filtered, index_condition,
        attached_condition, using_join_buffer, message;
    }

    @Override
    public String name() {
        return "MYSQL";
    }

    @Override
    public List<SQLPlan> getExecutionPlans(Connection connection, String sqlQuery) {
        List<SQLPlan> listExecutionPlans = new ArrayList<SQLPlan>(0);
        if (connection != null) {
            try {
                PreparedStatement preparedStatementVersion = connection.prepareStatement("SELECT @@VERSION");
                ResultSet resultSetVersion = preparedStatementVersion.executeQuery();
                if (resultSetVersion.next()) {
                    String strDatabaseVersion = resultSetVersion.getString(1);

                    int firstIndex = strDatabaseVersion.indexOf(".");
                    int splitIndex = strDatabaseVersion.indexOf(".", firstIndex + 1) < 0 ? firstIndex : strDatabaseVersion.indexOf(".", firstIndex + 1);
                    strDatabaseVersion = strDatabaseVersion.substring(0, splitIndex);
                    double doubleVersion = Double.valueOf(strDatabaseVersion);
                    /**
                     * MySQL 5.6 and above supports EXPLAIN with JSON format.
                     */
                    if (doubleVersion >= 5.6) {
                        PreparedStatement preparedStatementExecutionPlan = connection.prepareStatement("EXPLAIN format = JSON " + sqlQuery);
                        ResultSet resultSetExecutionPlan = preparedStatementExecutionPlan.executeQuery();
                        if (resultSetExecutionPlan.next()) {
                            MySQLPlan mySQLPlan = new MySQLPlan();
                            mySQLPlan.setSQLPlan(resultSetExecutionPlan.getString(1));
                            listExecutionPlans.add(mySQLPlan);
                        }
                        resultSetExecutionPlan.close();
                    } else {
                        logger.info(Constants.MYSQL_VERSION_NOTE);
                    }
                }
                resultSetVersion.close();
            } catch (SQLException ex) {
                throw new SQLAnalyzerException(Constants.FETCH_EXECUTION_PLAN_ERROR, ex);
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    throw new SQLAnalyzerException(Constants.CONNECTION_CLOSE_ERROR, ex);
                }
            }
        }
        return listExecutionPlans;
    }

    @Override
    public String parse(String executionPlan) {
        if (executionPlan != null && !executionPlan.trim().isEmpty()) {
            try {
                JSONObject QueryPlan = new JSONObject(executionPlan);
                stringBuilderHTMLReport.append("<div class=\"").append(Constants.CSS_REPORT_NODE).append("\">");
                stringBuilderHTMLReport.append("<div style=\"position:relative\">");
                stringBuilderHTMLReport.append("<div id=\"").append(Constants.HTML_ID_NODE_PROPERTIES).append("\"></div>");
                stringBuilderHTMLReport.append("<div class=\"").append(Constants.CSS_GRAPHICAL_NODE).append(" ").append(Constants.CSS_WHITE_FADED_BOX).append("\">");
                int parentNode = jsonParser(QueryPlan.optJSONObject("query_block"), 0, stringBuilderHTMLReport);
                parentNode = parentNode + 1;
                stringBuilderHTMLReport.append("<script>");
                stringBuilderHTMLReport.append("$(\"#").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode - 1).append("\").prepend(\"<div id=\\\"").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode).append("\\\"><div class=\\\"").append(Constants.CSS_MYSQL_QUERY_BLOCK).append("\\\" id=\\\"").append(Constants.HTML_ID_PARENT).append((parentNode)).append("\\\">query_block</div></div>\");");
                if (parentNode == 1) {
                    /* In case of single table return */
                    stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript("parent0", "parent1", jsPlumb.TopCenter, jsPlumb.BottomCenter));
                }
                stringBuilderHTMLReport.append("</script>");
                stringBuilderHTMLReport.append("</div>");
                stringBuilderHTMLReport.append("</div>");
                stringBuilderHTMLReport.append("</div>");
            } catch (Exception e) {
                throw new SQLAnalyzerException(Constants.JSON_PARSE_ERROR + "\n" + executionPlan + "\n", e);
            }
        }
        return stringBuilderHTMLReport.toString();
    }

    /**
     * Parse execution plan JSON.
     * @param obj
     * @param parentNode
     * @param stringBuilderHTMLReport
     * @return 
     */
    private int jsonParser(JSONObject obj, int parentNode, StringBuilder stringBuilderHTMLReport) {
        if (obj != null) {
            if (obj.has(MySQLOperation.table.toString())) {
                parseTableNode(obj.optJSONObject("table"), parentNode, stringBuilderHTMLReport);
            } else if (obj.has(MySQLOperation.nested_loop.toString())) {
                JSONArray jsonArrayTable = obj.optJSONArray(MySQLOperation.nested_loop.toString());
                for (int i = 0; i < jsonArrayTable.length(); i++) {
                    parentNode = i;
                    parentNode = jsonParser(jsonArrayTable.optJSONObject(i), parentNode, stringBuilderHTMLReport);

                    stringBuilderHTMLReport.append("<script>");
                    if ((i + 1) == jsonArrayTable.length()) {
                        stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_PARENT + parentNode, Constants.HTML_ID_PARENT + (parentNode + 1), jsPlumb.TopCenter, jsPlumb.BottomCenter));
                    } else {
                        stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_PARENT + parentNode, Constants.HTML_ID_PARENT + (parentNode + 1), jsPlumb.RightMiddle, jsPlumb.LeftMiddle));
                    }
                    stringBuilderHTMLReport.append("</script>");
                }
            } else if (obj.has(MySQLOperation.grouping_operation.toString())) {
                JSONObject jsonObjectGroupingOperation = obj.optJSONObject(MySQLOperation.grouping_operation.toString());
                parentNode = jsonParser(jsonObjectGroupingOperation, parentNode, stringBuilderHTMLReport);

                StringBuilder nodeAttribute = new StringBuilder("");
                Iterator<String> iteratorProperties = jsonObjectGroupingOperation.keys();
                while (iteratorProperties.hasNext()) {
                    /* Get key from JSONObject */
                    String property = iteratorProperties.next();
                    if (!(jsonObjectGroupingOperation.get(property) instanceof JSONObject)
                            && !(jsonObjectGroupingOperation.get(property) instanceof JSONArray)) {
                        /* Get value of key */
                        String propertyValue = jsonObjectGroupingOperation.optString(property);
                        nodeAttribute.append(property).append(" = ").append(" \\\"<b>").append(property).append("</b>: ").append(propertyValue.replace("\"", "")).append("\\\"").append(" ");
                    }
                }

                stringBuilderHTMLReport.append("<script>");
                if (parentNode == 0) {
                    /* In case of single table return */
                    stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_PARENT + "0", Constants.HTML_ID_PARENT + "1", jsPlumb.TopCenter, jsPlumb.BottomCenter));
                }
                parentNode = parentNode + 1;

                stringBuilderHTMLReport.append("$(\"#").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode - 1).append("\").prepend(\"<div id=\\\"").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode).append("\\\"><div class=\\\"" + Constants.CSS_MYSQL_GROUP_BY + "\\\" id=\\\"").append(Constants.HTML_ID_PARENT).append(" ").append(Constants.CSS_NODE_IMAGE).append((parentNode)).append("\\\" ").append(nodeAttribute).append(">GROUP</div></div>\");");
                stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_PARENT + parentNode, Constants.HTML_ID_PARENT + (parentNode + 1), jsPlumb.TopCenter, jsPlumb.BottomCenter));
                stringBuilderHTMLReport.append("</script>");
            } else if (obj.has(MySQLOperation.ordering_operation.toString())) {
                JSONObject jsonObjectOrderingOperation = obj.optJSONObject(MySQLOperation.ordering_operation.toString());
                parentNode = jsonParser(jsonObjectOrderingOperation, parentNode, stringBuilderHTMLReport);

                StringBuilder nodeAttribute = new StringBuilder("");
                Iterator<String> iteratorProperties = jsonObjectOrderingOperation.keys();
                while (iteratorProperties.hasNext()) {
                    /* Get key from JSONObject */
                    String property = iteratorProperties.next();

                    if (!(jsonObjectOrderingOperation.get(property) instanceof JSONObject)
                            && !(jsonObjectOrderingOperation.get(property) instanceof JSONArray)) {
                        /* Get value of key */
                        String propertyValue = jsonObjectOrderingOperation.optString(property);
                        nodeAttribute.append(property).append(" = ").append(" \\\"<b>").append(property).append("</b>: ").append(propertyValue.replace("\"", "")).append("\\\"").append(" ");
                    }
                }

                stringBuilderHTMLReport.append("<script>");
                if (parentNode == 0) {
                    /* In case of single table return */
                    stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_PARENT + "0", Constants.HTML_ID_PARENT + "1", jsPlumb.TopCenter, jsPlumb.BottomCenter));
                }
                parentNode = parentNode + 1;
                stringBuilderHTMLReport.append("$(\"#").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode - 1).append("\").prepend(\"<div id=\\\"").append(Constants.HTML_ID_MYSQL_TABLE).append(parentNode).append("\\\"><div class=\\\"").append(Constants.CSS_MYSQL_ORDER_BY).append(" ").append(Constants.CSS_NODE_IMAGE).append("\\\" id=\\\"").append(Constants.HTML_ID_PARENT).append((parentNode)).append("\\\" ").append(nodeAttribute).append(">ORDER</div></div>\");");
                stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript("parent" + parentNode, "parent" + (parentNode + 1), jsPlumb.TopCenter, jsPlumb.BottomCenter));
                stringBuilderHTMLReport.append("</script>");
            }
        }
        return parentNode;
    }

    /**
     * Prepare table node.
     *
     * @param objTableNode
     * @param parentNode
     * @param stringBuilder
     */
    private void parseTableNode(JSONObject objTableNode, int parentNode, StringBuilder stringBuilder) {
        if (objTableNode != null) {
            stringBuilder.append("<div class=\"" + Constants.CSS_MYSQL_TABLE_BLOCK + "\"").append(" id=\"" + Constants.HTML_ID_MYSQL_TABLE + "").append(parentNode).append("\">");
            if (parentNode <= 0) {
                stringBuilder.append("<div id=\"").append(Constants.HTML_ID_PARENT).append(parentNode).append("\" class=\"" + Constants.CSS_MYSQL_CONNECTOR_DOT + "\"></div>");
            } else {
                stringBuilder.append("<div class=\"" + Constants.CSS_MYSQL_NESTED_LOOP + "\" id=\"" + Constants.HTML_ID_PARENT + "").append(parentNode).append("\"><div style=\"height:7px\"></div>nested loop</div>");
            }
            /* Impossible WHERE noticed after reading const tables */
            boolean messageFound = false;
            int rowCount = 0;

            /* Attach runtime style to node */
            String nodeStyle = "";
            StringBuilder nodeAttribute = new StringBuilder("");
            String nodeContent = "";

            /* Loop through all properties of JSONObject("table") */
            Iterator<String> iteratorProperties = objTableNode.keys();
            while (iteratorProperties.hasNext()) {
                /* Get key from JSONObject */
                String property = iteratorProperties.next();
                /* Get value of key */
                String propertyValue = objTableNode.optString(property);

                nodeAttribute.append(property).append(" = ").append(" \"<b>").append(property).append("</b>: ").append(propertyValue.replace("\"", "")).append("\"").append(" ");

                if ("rows".equalsIgnoreCase(property) && propertyValue.matches("[0-9]*")) {
                    rowCount = Integer.valueOf(propertyValue);
                }

                if (MySQLTableProperties.access_type.toString().equalsIgnoreCase(property)) {
                    if ("ALL".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Full Table Scan";
                        /* rgb(245, 110, 110): nearly `red` */
                        nodeStyle = Constants.CSS_MYSQL_RED_BACKGROUND;
                    } else if ("range".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Index Range Scan";
                        /* rgb(215, 169, 26): nearly `brown` */
                        nodeStyle = Constants.CSS_MYSQL_BROWN_BACKGROUND;
                    } else if ("const".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Single Row(Constant)";
                        /* rgb(26, 107, 215): nearly `blue` */
                        nodeStyle = Constants.CSS_MYSQL_BLUE_BACKGROUND;
                    } else if ("ref".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Non-Unique Key Lookup";
                        nodeStyle = Constants.CSS_MYSQL_GREEN_BACKGROUND;
                    } else if ("eq_ref".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Unique Key Lookup";
                        nodeStyle = Constants.CSS_MYSQL_GREEN_BACKGROUND;
                    } else if ("index".equalsIgnoreCase(propertyValue)) {
                        nodeContent += "Full Index Scan";
                        /* rgb(245, 110, 110): nearly `red` */
                        nodeStyle = Constants.CSS_MYSQL_RED_BACKGROUND;
                    }
                } else if (MySQLTableProperties.message.toString().equalsIgnoreCase(property)) {
                    nodeContent += "Impossible Query";
                    /* rgb(245, 110, 110): nearly `red` */
                    nodeStyle = Constants.CSS_MYSQL_RED_BACKGROUND;
                    messageFound = true;
                }
            }

            String prepareTableBlock = "<div id=\"" + Constants.HTML_ID_CHILD + "" + parentNode + "\" class=\"" + Constants.CSS_MYSQL_TABLE_BLOCK_DATA + " " + Constants.CSS_NODE_IMAGE + "\" style=\"" + nodeStyle + "\" " + nodeAttribute + ">";
            /* Display row count top-right corner */
            prepareTableBlock += "<div class=\"" + Constants.CSS_MYSQL_ROW_COUNT + "\">" + rowCount + " row(s)</div>";
            prepareTableBlock += nodeContent;
            prepareTableBlock += "</div>";

            /* Append TableBlock to stringBuilder */
            stringBuilder.append(prepareTableBlock);
            stringBuilder.append("<script>");
            stringBuilder.append(jsPlumb.getjsPlumbScript(Constants.HTML_ID_CHILD + parentNode, Constants.HTML_ID_PARENT + parentNode, jsPlumb.TopCenter, jsPlumb.BottomCenter));

            stringBuilder.append("</script>");

            if (objTableNode.has(MySQLTableProperties.table_name.toString())) {
                stringBuilder.append(objTableNode.opt(MySQLTableProperties.table_name.toString()));
            }

            if (objTableNode.has(MySQLTableProperties.key.toString())) {
                stringBuilder.append("<div style=\"font-size:11px;font-weight:bold\">").append(objTableNode.opt(MySQLTableProperties.key.toString())).append("</div>");
            }

            if (messageFound) {
                stringBuilder.append("<div style=\"font-size:11px;font-weight:bold\">Impossible WHERE noticed after reading const tables</div>");
            }

            stringBuilder.append("</div>");
        }
    }

    @Override
    public Map<String, Object> metaData() {
        return new HashMap<String, Object>(0);
    }
    
    @Override
    public String stylesheet() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("  ").append("<style>\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_ROW_COUNT).append("{position:absolute;top:-15px;right:0px;color:black;font-size:11px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_GRAPHICAL_NODE).append("{font-size:13px;height:500px;max-height:500px;overflow:scroll;position:relative}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_CONNECTOR_DOT).append("{border-radius:10px;-webkit-border-radius:10px;-moz-border-radius:10px;box-shadow:0 0 8px rgba(0, 0, 0, .8);-webkit-box-shadow:0 0 8px rgba(0, 0, 0, .8);-moz-box-shadow:0 0 8px rgba(0, 0, 0, .8);margin:0px auto;margin-bottom:99px;width:10px;height:10px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_NESTED_LOOP).append("{margin:0px auto;margin-bottom:77px;border:2px solid gray;width:50px;height:50px;text-align:center;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_TABLE_BLOCK).append("{display:inline-block;text-align:center;margin-right:70px}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_TABLE_BLOCK_DATA).append("{color:white;padding:5px;border:1px solid black;width:140px;height:15px;text-align:center;position:relative;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_GROUP_BY).append("{margin:0px auto;border:2px solid brown;padding:7px;margin-bottom:60px;width:50px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_ORDER_BY).append("{margin:0px auto;border:2px solid red;padding:7px;margin-bottom:60px;width:50px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MYSQL_QUERY_BLOCK).append("{margin:0px auto;border:1px solid black;padding:7px;margin-bottom:60px;width:70px;background-color:lightgray;}\n");
        stringBuilder.append("  ").append("</style>\n");
        return stringBuilder.toString();
    }
}
