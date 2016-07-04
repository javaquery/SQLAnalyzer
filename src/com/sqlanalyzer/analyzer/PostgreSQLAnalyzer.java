/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.analyzer;

import com.sqlanalyzer.exception.SQLAnalyzerException;
import com.sqlanalyzer.executionplans.PostgreSQLPlan;
import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.util.CommonUtil;
import com.sqlanalyzer.util.Constants;
import com.sqlanalyzer.util.HTMLUtil;
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
 * @date 13th June, 2016
 */
public class PostgreSQLAnalyzer implements Analyzer {

    protected PostgreSQLAnalyzer() {
    }

    private static final Logger logger = Logger.getLogger(MySQLAnalyzer.class.getName());
    private final StringBuilder stringBuilderHTMLReport = new StringBuilder("");
    private static final Map<String, String> operationIcon = new HashMap<String, String>(10);

    static {
        operationIcon.put("Hash Join", "-56px -185px");
        operationIcon.put("Hash JoinInner", "-56px -185px");
        operationIcon.put("Hash JoinRight", "-536px -137px");
        operationIcon.put("Seq Scan", "-680px -185px");
        operationIcon.put("Hash", "-536px -137px");
        operationIcon.put("Nested Loop", "-440px -185px");
        operationIcon.put("Nested LoopInner", "-440px -185px");
        operationIcon.put("Nested LoopSemi", "-536px -185px");
        operationIcon.put("Nested LoopLeft", "-536px -185px");
        operationIcon.put("Index Scan", "-968px -137px");
        operationIcon.put("Limit", "-104px -185px");
        operationIcon.put("Aggregate", "-8px -137px");
        operationIcon.put("Sort", "-824px -185px	");
        operationIcon.put("Result", "-632px -185px");
        operationIcon.put("ModifyTableInsert", "-8px -185px");
        operationIcon.put("ModifyTableDelete", "-392px -137px");
        operationIcon.put("ModifyTableUpdate", "-8px -233px");
        operationIcon.put("Unique", "-968px -185px");
        operationIcon.put("Append", "-56px -137px");
        operationIcon.put("SetOpIntersect", "-776px -137px");
        operationIcon.put("SetOpIntersect All", "-776px -137px");
        operationIcon.put("SetOpExcept", "-680px -137px");
        operationIcon.put("SetOpExcept All", "-680px -137px");
        operationIcon.put("Subquery Scan", "-872px -185px");
        operationIcon.put("Bitmap Index Scan", "-200px -137px");
        operationIcon.put("Bitmap Heap Scan", "-152px -137px");
        operationIcon.put("Index Only Scan", "-920px -137px");
        operationIcon.put("Values Scan", "-56px -233px");
        operationIcon.put("WorkTable Scan", "-152px -233px");
        operationIcon.put("Recursive Union", "-584px -185px");
        operationIcon.put("CTE Scan", "-344px -137px");
    }

    @Override
    public String name() {
        return "PostgreSQL";
    }

    @Override
    public List<SQLPlan> getExecutionPlans(Connection connection, String sqlQuery) {
        List<SQLPlan> listExecutionPlans = new ArrayList<SQLPlan>(0);
        if (connection != null) {
            try {
                PreparedStatement preparedStatementExecutionPlan = connection.prepareStatement("EXPLAIN (format json) " + sqlQuery);
                ResultSet resultSetExecutionPlan = preparedStatementExecutionPlan.executeQuery();
                if (resultSetExecutionPlan.next()) {
                    PostgreSQLPlan postgreSQLPlan = new PostgreSQLPlan();
                    postgreSQLPlan.setSQLPlan(resultSetExecutionPlan.getString(1));
                    listExecutionPlans.add(postgreSQLPlan);
                }
                resultSetExecutionPlan.close();
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
                JSONArray QueryPlan = new JSONArray(executionPlan);
                stringBuilderHTMLReport.append("<div class=\"").append(Constants.CSS_REPORT_NODE).append("\">\n");
                stringBuilderHTMLReport.append("<div style=\"position:relative\">\n");
                stringBuilderHTMLReport.append("<div id=\"").append(Constants.HTML_ID_NODE_PROPERTIES).append("\"></div>\n");
                stringBuilderHTMLReport.append("<div id =\"").append(Constants.HTML_ID_PARENT).append("0\" class = \"").append(Constants.CSS_GRAPHICAL_NODE).append(" ").append(Constants.CSS_WHITE_FADED_BOX).append("\"></div>\n");
                stringBuilderHTMLReport.append("<script>\n");
                if (QueryPlan.optJSONObject(0) != null) {
                    jsonParser(QueryPlan.optJSONObject(0), 0, 1);
                }
                stringBuilderHTMLReport.append("</script>\n");
                stringBuilderHTMLReport.append("</div>\n");
                stringBuilderHTMLReport.append("</div>\n");
            } catch (Exception e) {
                throw new SQLAnalyzerException(Constants.JSON_PARSE_ERROR + "\n" + executionPlan + "\n", e);
            }
        }
        return stringBuilderHTMLReport.toString();
    }

    /**
     * Parse execution plan JSON.
     * @param obj
     * @param childOf
     * @param parentOf 
     */
    private void jsonParser(JSONObject obj, int childOf, int parentOf) {
        if (obj != null) {
            if (obj.has("Plan")) {
                /* Root node */
                JSONObject rootNode = obj.getJSONObject("Plan");
                prepareChild(childOf, parentOf, rootNode);
                childOf = parentOf;
                /* Child of root node */
                if (rootNode.has("Plans")) {
                    JSONArray childNodes = rootNode.optJSONArray("Plans");
                    if (childNodes != null) {
                        for (int i = 0; i < childNodes.length(); i++) {
                            String dummyParent = parentOf + "" + i;
                            parentOf = Integer.parseInt(dummyParent);
                            jsonParser(childNodes.optJSONObject(i), childOf, parentOf);
                        }
                    }
                }
            } else if (obj.has("Plans")) {
                prepareChild(childOf, parentOf, obj);
                JSONArray childNodes = obj.optJSONArray("Plans");
                childOf = parentOf;
                if (childNodes != null) {
                    for (int i = 0; i < childNodes.length(); i++) {
                        String dummyParent = parentOf + "" + i;
                        parentOf = Integer.parseInt(dummyParent);
                        jsonParser(childNodes.optJSONObject(i), childOf, parentOf);
                    }
                }
            } else {
                prepareChild(childOf, parentOf, obj);
            }
        }
    }

    /**
     * Prepare child node.
     * @param childOf
     * @param parentOf
     * @param nodeDetails 
     */
    private void prepareChild(int childOf, int parentOf, JSONObject nodeDetails) {
        String css = childOf != 0 ? Constants.CSS_POSTGRE_SQL_NODE_CHILD : Constants.CSS_POSTGRE_SQL_NODE;
        stringBuilderHTMLReport.append("$(\"#").append(Constants.HTML_ID_PARENT).append(childOf).append("\")");
        stringBuilderHTMLReport.append(".append(\"<div id = \\\"").append(Constants.HTML_ID_PARENT).append(parentOf).append("\\\" class = \\\"").append(css).append("\\\">");
        stringBuilderHTMLReport.append(prepareNodeIcon(parentOf, nodeDetails));
        stringBuilderHTMLReport.append("</div><br/>\");\n");

        if (childOf != 0) {
            String sourceNode = Constants.HTML_ID_POSTGRE_SQL_PARENT + parentOf;
            String targetNode = Constants.HTML_ID_POSTGRE_SQL_PARENT + childOf;
            stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(sourceNode, targetNode, jsPlumb.LeftMiddle, jsPlumb.RightMiddle)).append("\n");
        }
    }

    /**
     * Prepare node icon.
     * @param parentID
     * @param nodeDetails
     * @return 
     */
    private String prepareNodeIcon(int parentID, JSONObject nodeDetails) {
        String nodeType = null, nodeName = null, operation = null, command = null, joinType = null;
        StringBuilder sbNodeAttribute = new StringBuilder();

        if (nodeDetails.has("Relation Name")) {
            nodeType = nodeDetails.optString("Node Type");
            nodeName = nodeDetails.optString("Relation Name");
            if (nodeDetails.has("Index Name")) {
                nodeName = nodeDetails.optString("Index Name").replace("\"", "");
            }

            if (nodeDetails.has("Strategy")) {
                nodeName = nodeDetails.optString("Strategy") + nodeName;
            }
        } else if(nodeDetails.has("Index Name")){
            nodeType = nodeDetails.optString("Node Type");
            nodeName = nodeDetails.optString("Index Name").replace("\"", "");
        } else {
            nodeType = nodeDetails.optString("Node Type");
            nodeName = nodeType;

            if (nodeDetails.has("Strategy")) {
                nodeName = nodeDetails.optString("Strategy") + " " + nodeName;
            }
        }

        operation = nodeDetails.optString("Operation");
        command = nodeDetails.optString("Command");
        joinType = nodeDetails.optString("Join Type");
        
        Iterator iteratorNodeProperties = nodeDetails.keys();
        while (iteratorNodeProperties.hasNext()) {
            String strKey = (String) iteratorNodeProperties.next();
            /* replace `Node Type` to `Node_Type` space is not supported in html tag attributes*/
            String strKeyAttribute = strKey.replace(" ", "_").replace("\"", "");

            if (!"Plans".equalsIgnoreCase(strKey)) {
                String strValue = nodeDetails.optString(strKey);

                /* There are few values which contains `"` in JSON so replace it with blank */
                strValue = strValue.replace("\"", "");
                sbNodeAttribute.append(strKeyAttribute).append(" = ").append("\\\"<b>").append(strKey).append("</b>: ").append(strValue).append("\\\" ");
            }
        }

        if("ModifyTable".equalsIgnoreCase(nodeType)
                && nodeType != null && !nodeType.trim().isEmpty()){
            /* ModifyTableInsert, ModifyTableDelete */
            nodeType += operation;
        }else if("SetOp".equalsIgnoreCase(nodeType)
                && command != null && !command.trim().isEmpty()){
            nodeName += " " + command;
            nodeType += command;
        }else if("Hash Join".equalsIgnoreCase(nodeType) 
                && joinType != null && !joinType.trim().isEmpty()){
            nodeName += " " + joinType + " Join";
            nodeType += joinType;
        }else if("Nested Loop".equalsIgnoreCase(nodeType) 
                && joinType != null && !joinType.trim().isEmpty()){
            nodeName += " " + joinType + " Join";
            nodeType += joinType;
        }
        
        String iconPosition = operationIcon.get(nodeType);
        iconPosition = CommonUtil.value(iconPosition, "-296px -56px");

        String nodeID = parentID == 0 ? "rootnode" : Constants.HTML_ID_POSTGRE_SQL_PARENT + parentID;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table class = \\\"").append(Constants.CSS_POSTGRE_SQL_NODE_TABLE).append("\\\" id = \\\"").append(nodeID).append("\\\">");
        stringBuilder.append("<tr>").append("<td class = \\\"").append(Constants.CSS_POSTGRE_SQL_NODE_FIXED_WIDTH).append("\\\">");
        stringBuilder.append("<div class  = \\\"").append(Constants.CSS_NODE_IMAGE).append(" ").append(Constants.CSS_POSTGRE_SQL_NODE_ICON).append("\\\" ");
        stringBuilder.append("style = \\\"background: url('").append(HTMLUtil.CSS_ICON_IMAGE).append("') no-repeat ").append(iconPosition).append("\\\" ");
        stringBuilder.append("title = \\\"").append(nodeName).append("\\\" ").append(sbNodeAttribute).append(" ></div>");
        stringBuilder.append("</td>").append("</tr>");
        stringBuilder.append("<tr>").append("<td class=\\\"").append(Constants.CSS_POSTGRE_SQL_NODE_NAME).append("\\\">").append(nodeName).append("<br/></td></tr>");
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    @Override
    public Map<String, Object> metaData() {
        return new HashMap<String, Object>(0);
    }
    
    @Override
    public String stylesheet() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("  ").append("<style>\n");
        stringBuilder.append("      .").append(Constants.CSS_GRAPHICAL_NODE).append("{height:500px;max-height:500px;overflow:scroll;white-space: nowrap;position:relative}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE).append("{padding-bottom:20px;display:inline-block;}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE_CHILD).append("{padding-bottom:20px;display:inline-block;padding-left:95px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE_TABLE).append("{width:95px !important;height:95px !important;display:inline-block;vertical-align:top;text-align:center;font-size:12px;margin:0px auto}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE_FIXED_WIDTH).append("{width:95px}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE_ICON).append("{position:relative;margin:0px auto;width:32px;height:32px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_POSTGRE_SQL_NODE_NAME).append("{width:95px;white-space:pre-wrap;}\n");
        stringBuilder.append("  ").append("</style>\n");
        return stringBuilder.toString();
    }

}
