/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.analyzer;

import com.sqlanalyzer.database.mssql.MSSQLExecutionPlanEnum;
import com.sqlanalyzer.database.mssql.MSSQLIcon;
import com.sqlanalyzer.database.mssql.MSSQLMetaType;
import com.sqlanalyzer.database.mssql.MSSQLNodeKey;
import com.sqlanalyzer.database.mssql.MSSQLNodeValue;
import com.sqlanalyzer.database.mssql.MSSQLUtil;
import com.sqlanalyzer.exception.SQLAnalyzerException;
import com.sqlanalyzer.executionplans.MSSQLPlan;
import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.util.CommonUtil;
import com.sqlanalyzer.util.Constants;
import com.sqlanalyzer.util.HTMLUtil;
import com.sqlanalyzer.util.SQLUtil;
import com.sqlanalyzer.util.XMLUtil;
import com.sqlanalyzer.util.jsPlumb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author vicky.thakor
 * @date 24th May, 2016
 */
public class MSSQLAnalyzer implements Analyzer {

    protected MSSQLAnalyzer() {
    }

    private static final Logger logger = Logger.getLogger(MySQLAnalyzer.class.getName());
    private final Map<String, Object> metaData = new HashMap<String, Object>(0);
    private final StringBuilder stringBuilderHTMLReport = new StringBuilder("");
    private static final String REL_OP = "RelOp";
    private static final String NODE_ID = "NodeId";
    private static final Map<String, MSSQLIcon> operationIcon = new TreeMap<String, MSSQLIcon>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<MSSQLNodeKey, MSSQLNodeValue> xmlNodes = new HashMap<MSSQLNodeKey, MSSQLNodeValue>();

    static {
        operationIcon.put("ComputeScalar", new MSSQLIcon("ComputeScalar", "Compute Scalar", "-104px -8px"));
        operationIcon.put("TableSpool", new MSSQLIcon("TableSpool", "Table Spool", "-56px -56px"));
        operationIcon.put("Sequence", new MSSQLIcon("Sequence", "Sequence", "-728px -8px"));
        operationIcon.put("TableValuedFunction", new MSSQLIcon("TableValuedFunction", "Table Valued Function", "-152px -56px"));
        operationIcon.put("TableInsert", new MSSQLIcon("TableInsert", "Table Insert", "-968px -8px"));
        operationIcon.put("Top", new MSSQLIcon("Top", "Top", "-200px -56px"));
        operationIcon.put("Filter", new MSSQLIcon("Filter", "Filter", "-248px -8px"));
        operationIcon.put("SequenceProject", new MSSQLIcon("SequenceProject", "Sequence Project", "-776px -8px"));
        operationIcon.put("Segment", new MSSQLIcon("Segment", "Segment", "-632px -8px"));
        operationIcon.put("RIDLookup", new MSSQLIcon("RIDLookup", "RID Lookup", "-584px -8px"));
        operationIcon.put("Concatenation", new MSSQLIcon("Concatenation", "Concatenation", "-152px -8px"));
        operationIcon.put("TableDelete", new MSSQLIcon("TableDelete", "Table Delete", "-920px -8px"));
        operationIcon.put("TableUpdate", new MSSQLIcon("TableUpdate", "Table Update", "-104px -56px"));
        operationIcon.put("StreamAggregate", new MSSQLIcon("StreamAggregate", "Stream Aggregate", "-872px -8px"));
        operationIcon.put("TableScan", new MSSQLIcon("TableScan", "Table Scan", "-8px -56px"));
        operationIcon.put("Sort", new MSSQLIcon("Sort", "Sort", "-824px -8px"));
        operationIcon.put("NestedLoops", new MSSQLIcon("NestedLoops", "Nested Loops", "-488px -8px"));
        operationIcon.put("Merge", new MSSQLIcon("Merge", "Merge", "-440px -8px"));
        operationIcon.put("HashMatch", new MSSQLIcon("HashMatch", "Hash Match", "-344px -8px"));
        operationIcon.put("ClusteredIndexScan", new MSSQLIcon("ClusteredIndexScan", "Clustered Index Scan", "-8px -8px"));
        operationIcon.put("ClusteredIndexSeek", new MSSQLIcon("ClusteredIndexSeek", "Clustered Index Seek", "-56px -8px"));
        operationIcon.put("IndexSeek", new MSSQLIcon("IndexSeek", "Index Seek", "-392px -8px"));
        operationIcon.put("KeyLookup", new MSSQLIcon("KeyLookup", "Key Lookup", "-344px -56px"));
        operationIcon.put("DistributeStreams", new MSSQLIcon("DistributeStreams", "Distribute Streams", "-200px -8px"));
        operationIcon.put("RepartitionStreams", new MSSQLIcon("RepartitionStreams", "Repartition Streams", "-536px -8px"));
        operationIcon.put("GatherStreams", new MSSQLIcon("GatherStreams", "Gather Streams", "-296px -8px"));
        operationIcon.put("NonclusteredIndexScan", new MSSQLIcon("NonclusteredIndexScan", "Non Clustered Index Scan", "-456px -56px"));
        operationIcon.put("Select", new MSSQLIcon("Select", "Select", "-680px -8px"));
        operationIcon.put("Update", new MSSQLIcon("Update", "Update", "-248px -56px"));
        operationIcon.put("Delete", new MSSQLIcon("Delete", "Delete", "-248px -56px"));
        operationIcon.put("TSQLIcon", new MSSQLIcon("TSQLIcon", "", "-248px -56px"));
        operationIcon.put("WarningIcon", new MSSQLIcon("WarningIcon", "Warning", "-392px -56px"));
        operationIcon.put("ParallelIcon", new MSSQLIcon("ParallelIcon", "Parallel", "-424px -56px"));
        operationIcon.put("IconNotFound", new MSSQLIcon("IconNotFound", "", "-296px -56px"));
    
        // XML Nodes
        xmlNodes.put(new MSSQLNodeKey("Table Scan", "Table Scan"), new MSSQLNodeValue("Table Scan", operationIcon.get("TableScan")));
        xmlNodes.put(new MSSQLNodeKey("Sort", null), new MSSQLNodeValue("Sort", operationIcon.get("Sort")));
        xmlNodes.put(new MSSQLNodeKey("Sort", "Sort"), new MSSQLNodeValue("Sort", operationIcon.get("Sort")));
        xmlNodes.put(new MSSQLNodeKey("Distinct Sort", "Sort"), new MSSQLNodeValue("Sort<br/>(Distinct Sort)", operationIcon.get("Sort")));
        xmlNodes.put(new MSSQLNodeKey("Left Outer Join", "Nested Loops"), new MSSQLNodeValue("Nested Loops<br/>(Left Outer Join)", operationIcon.get("NestedLoops")));
        xmlNodes.put(new MSSQLNodeKey("Right Outer Join", "Nested Loops"), new MSSQLNodeValue("Nested Loops<br/>(Right Outer Join)", operationIcon.get("NestedLoops")));
        xmlNodes.put(new MSSQLNodeKey("Inner Join", "Nested Loops"), new MSSQLNodeValue("Nested Loops<br/>(Inner Join)", operationIcon.get("NestedLoops")));
        xmlNodes.put(new MSSQLNodeKey("Left Anti Semi Join", "Nested Loops"), new MSSQLNodeValue("Nested Loops<br/>(Left Anti Semi Join)", operationIcon.get("NestedLoops")));
        xmlNodes.put(new MSSQLNodeKey("Left Outer Join", "Merge Join"), new MSSQLNodeValue("Merge<br/>(Left Outer Join)", operationIcon.get("Merge")));
        xmlNodes.put(new MSSQLNodeKey("Right Outer Join", "Merge Join"), new MSSQLNodeValue("Merge<br/>(Right Outer Join)", operationIcon.get("Merge")));
        xmlNodes.put(new MSSQLNodeKey("Left Outer Join", "Hash Match"), new MSSQLNodeValue("Hash Match<br/>(Left Outer Join)", operationIcon.get("HashMatch")));
        xmlNodes.put(new MSSQLNodeKey("Right Outer Join", "Hash Match"), new MSSQLNodeValue("Hash Match<br/>(Right Outer Join)", operationIcon.get("HashMatch")));
        xmlNodes.put(new MSSQLNodeKey("Clustered Index Scan", "Clustered Index Scan"), new MSSQLNodeValue("Clustered Index Scan", operationIcon.get("ClusteredIndexScan")));
        xmlNodes.put(new MSSQLNodeKey("Clustered Index Seek", "Clustered Index Seek"), new MSSQLNodeValue("Clustered Index Seek", operationIcon.get("ClusteredIndexSeek")));
        xmlNodes.put(new MSSQLNodeKey("Index Seek", "Index Seek"), new MSSQLNodeValue("Index Seek", operationIcon.get("IndexSeek")));
        xmlNodes.put(new MSSQLNodeKey("Compute Scalar", "Compute Scalar"), new MSSQLNodeValue("Compute Scalar", operationIcon.get("ComputeScalar")));
        xmlNodes.put(new MSSQLNodeKey("Lazy Spool", "Table Spool"), new MSSQLNodeValue("Table Spool<br/>(Lazy Spool)", operationIcon.get("TableSpool")));
        xmlNodes.put(new MSSQLNodeKey("Distribute Streams", "Parallelism"), new MSSQLNodeValue("Parallelism<br/>(Distribute Streams)", operationIcon.get("DistributeStreams")));
        xmlNodes.put(new MSSQLNodeKey("Repartition Streams", "Parallelism"), new MSSQLNodeValue("Parallelism<br/>(Repartition Streams)", operationIcon.get("RepartitionStreams")));
        xmlNodes.put(new MSSQLNodeKey("Gather Streams", "Parallelism"), new MSSQLNodeValue("Parallelism<br/>(Gather Streams)", operationIcon.get("GatherStreams")));
        xmlNodes.put(new MSSQLNodeKey("Sequence", "Sequence"), new MSSQLNodeValue("Sequence", operationIcon.get("Sequence")));
        xmlNodes.put(new MSSQLNodeKey("Table-valued function", "Table-valued function"), new MSSQLNodeValue("Table Valued Function", operationIcon.get("TableValuedFunction")));
        xmlNodes.put(new MSSQLNodeKey("Insert", "Table Insert"), new MSSQLNodeValue("Table Insert", operationIcon.get("TableInsert")));
        xmlNodes.put(new MSSQLNodeKey("Top", "Top"), new MSSQLNodeValue("Top", operationIcon.get("Top")));
        xmlNodes.put(new MSSQLNodeKey("Filter", "Filter"), new MSSQLNodeValue("Filter", operationIcon.get("Filter")));
        xmlNodes.put(new MSSQLNodeKey("Compute Scalar", "Sequence Project"), new MSSQLNodeValue("Sequence Project<br/>(Compute Scalar)", operationIcon.get("SequenceProject")));
        xmlNodes.put(new MSSQLNodeKey("Segment", "Segment"), new MSSQLNodeValue("Segment", operationIcon.get("Segment")));
        xmlNodes.put(new MSSQLNodeKey("RID Lookup", "RID Lookup"), new MSSQLNodeValue("RID Lookup", operationIcon.get("RIDLookup")));
        xmlNodes.put(new MSSQLNodeKey("Concatenation", "Concatenation"), new MSSQLNodeValue("Concatenation", operationIcon.get("Concatenation")));
        xmlNodes.put(new MSSQLNodeKey("Delete", "Table Delete"), new MSSQLNodeValue("Table Delete", operationIcon.get("TableDelete")));
        xmlNodes.put(new MSSQLNodeKey("Update", "Table Update"), new MSSQLNodeValue("Table Update", operationIcon.get("TableUpdate")));
        xmlNodes.put(new MSSQLNodeKey("Aggregate", "Stream Aggregate"), new MSSQLNodeValue("Stream Aggregate<br/>(Aggregate)", operationIcon.get("StreamAggregate")));
        xmlNodes.put(new MSSQLNodeKey("Inner Join", "Hash Match"), new MSSQLNodeValue("Hash Match<br/>(Inner Join)", operationIcon.get("HashMatch")));
        xmlNodes.put(new MSSQLNodeKey("Key Lookup", "Clustered"), new MSSQLNodeValue("Key Lookup (Clustered)", operationIcon.get("KeyLookup")));
        xmlNodes.put(new MSSQLNodeKey("Index Scan", "Index Scan"), new MSSQLNodeValue("Index Scan (NonClustered)", operationIcon.get("NonclusteredIndexScan")));
    }
    
    @Override
    public List<SQLPlan> getExecutionPlans(Connection connection, String sqlQuery) {
        List<SQLPlan> listExecutionPlans = new ArrayList<SQLPlan>(0);
        if (connection != null) {
            sqlQuery = sqlQuery.replace("'", "''");
            sqlQuery = SQLUtil.replaceQuestionMarkWithP(sqlQuery);
            /**
             * Microsoft SQL Server can process String of 2000 character only in
             * `LIKE` clause. We'll trim last 2000 Characters as it contains
             * `WHERE`, `JOIN`, etc... clause and that makes query unique.
             */
            if (sqlQuery != null && !sqlQuery.isEmpty() && sqlQuery.length() > 2000) {
                int position = sqlQuery.length() - 2000;
                sqlQuery = sqlQuery.substring(position, sqlQuery.length());
            }

            sqlQuery = "WITH XMLNAMESPACES (default 'http://schemas.microsoft.com/sqlserver/2004/07/showplan') "
                    + "SELECT "
                    + "Cast('<?SQL ' + st.text + ' ?>' as xml) sql_text, "
                    + "pl.query_plan, "
                    + "ps.execution_count, "
                    + "ps.last_execution_time, "
                    + "ps.last_elapsed_time, "
                    + "ps.last_logical_reads, "
                    + "ps.last_logical_writes "
                    + "FROM sys.dm_exec_query_stats ps with (NOLOCK) "
                    + "Cross Apply sys.dm_exec_sql_text(ps.sql_handle) st "
                    + "Cross Apply sys.dm_exec_query_plan(ps.plan_handle) pl "
                    + "WHERE st.text like '%" + sqlQuery + "' AND  st.text NOT LIKE '%sys.dm_exec_query_stats%'";

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                ResultSet resultSetExecutionPlan = preparedStatement.executeQuery();
                while (resultSetExecutionPlan.next()) {
                    Map<String, Object> meta = new HashMap<String, Object>(0);
//                    meta.put(MSSQLMetaType.SQL_TEXT.name(), resultSetExecutionPlan.getString(1));
//                    meta.put(MSSQLMetaType.QUERY_PLAN.name(), resultSetExecutionPlan.getString(2));
                    meta.put(MSSQLMetaType.EXECUTION_COUNT.name(), resultSetExecutionPlan.getString(3));
                    meta.put(MSSQLMetaType.LAST_EXECUTION_TIME.name(), resultSetExecutionPlan.getString(4));
                    meta.put(MSSQLMetaType.LAST_ELAPSED_TIME.name(), resultSetExecutionPlan.getString(5));
                    meta.put(MSSQLMetaType.LAST_LOGICAL_READS.name(), resultSetExecutionPlan.getString(6));
                    meta.put(MSSQLMetaType.LAST_LOGICAL_WRITE.name(), resultSetExecutionPlan.getString(7));

                    MSSQLPlan mssqlPlan = new MSSQLPlan();
                    mssqlPlan.setSQLPlan(resultSetExecutionPlan.getString(2));
                    mssqlPlan.metaData().putAll(meta);
                    listExecutionPlans.add(mssqlPlan);
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
        Document document = XMLUtil.getDocument(executionPlan, null);
        xmlParser(document);
        return stringBuilderHTMLReport.toString();
    }

    /**
     * Parse XML document.
     * @param document
     */
    public void xmlParser(Document document) {
        if (document != null) {
            try {
                NodeList nodeListQueryStatement = document.getElementsByTagName("StmtSimple");
                NodeList nodeListQueryPlan = document.getElementsByTagName("QueryPlan");
                NodeList nodeListMissingIndexGroup = document.getElementsByTagName("MissingIndexGroup");

                if (nodeListQueryPlan.getLength() == 1 && nodeListQueryStatement.getLength() > 0) {
                    setQueryStatistics(nodeListQueryStatement);

                    stringBuilderHTMLReport.append("<div class=\"").append(Constants.CSS_REPORT_NODE).append("\">");
                    stringBuilderHTMLReport.append("<div style=\"position:relative\">");
                    stringBuilderHTMLReport.append("<div id=\"").append(Constants.HTML_ID_NODE_PROPERTIES).append("\"></div>");
                    stringBuilderHTMLReport.append("<div id = \"").append(Constants.HTML_ID_PARENT).append("-1\"").append(" class = \"").append(Constants.CSS_GRAPHICAL_NODE).append(" ").append(Constants.CSS_WHITE_FADED_BOX).append("\">");

                    /* SELECT, UPDATE, DELETE */
                    stringBuilderHTMLReport.append(setRootNode());
                    stringBuilderHTMLReport.append("</div>\n");
                    stringBuilderHTMLReport.append("<script>\n");

                    /* Parse execution plan */
                    parseRecusrsive(nodeListQueryPlan, -1);

                    stringBuilderHTMLReport.append("</script>\n");
                    stringBuilderHTMLReport.append("</div>\n");
                    stringBuilderHTMLReport.append("</div>\n");

                    missingIndices(nodeListMissingIndexGroup);
                }
            } catch (Exception e) {
                throw new SQLAnalyzerException(null, e);
            }
        }
    }

    /**
     * Retrieve meta information of Execution plan.
     * @param nodeList 
     */
    private void setQueryStatistics(NodeList nodeList) {
        if (nodeList != null
                && nodeList.item(0) != null
                && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) nodeList.item(0);
            NamedNodeMap attributes = element.getAttributes();
            if (attributes.getNamedItem("StatementText") != null) {
                String statementText = attributes.getNamedItem("StatementText").getNodeValue();
                String statementType = attributes.getNamedItem("StatementType") != null ? attributes.getNamedItem("StatementType").getNodeValue() : "NA";
                String statementEstRows = attributes.getNamedItem("StatementEstRows") != null ? attributes.getNamedItem("StatementEstRows").getNodeValue() : "NA";
                String statementSubTreeCost = attributes.getNamedItem("StatementSubTreeCost") != null ? attributes.getNamedItem("StatementSubTreeCost").getNodeValue() : "NA";
                String statementOptmLevel = attributes.getNamedItem("StatementOptmLevel") != null ? attributes.getNamedItem("StatementOptmLevel").getNodeValue() : "NA";
                String satementOptmEarlyAbortReason = attributes.getNamedItem("StatementOptmEarlyAbortReason") != null ? attributes.getNamedItem("StatementOptmEarlyAbortReason").getNodeValue() : "NA";
                String queryHash = attributes.getNamedItem("QueryHash") != null ? attributes.getNamedItem("QueryHash").getNodeValue() : "NA";
                String queryPlanHash = attributes.getNamedItem("QueryPlanHash") != null ? attributes.getNamedItem("QueryPlanHash").getNodeValue() : "NA";

                metaData.put("StatementText", statementText);
                metaData.put("StatementType", statementType);
                metaData.put("StatementEstRows", statementEstRows);
                metaData.put("StatementSubTreeCost", statementSubTreeCost);
                metaData.put("StatementOptmLevel", statementOptmLevel);
                metaData.put("StatementOptmEarlyAbortReason", satementOptmEarlyAbortReason);
                metaData.put("QueryHash", queryHash);
                metaData.put("QueryPlanHash", queryPlanHash);
            }
        }
    }

    /**
     * Set root node. (SELECT, UPDATE, DELETE)
     * @return 
     */
    private String setRootNode() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("<div class = \"").append(Constants.CSS_MSSQL_NODE).append("\">");

        String queryType = String.valueOf(metaData.get("StatementType"));
        MSSQLIcon mssqlIcon = operationIcon.get(queryType);

        if (mssqlIcon != null) {
            stringBuilder.append(prepareHTMLNodeIcon(mssqlIcon.getTitle(), "", mssqlIcon.getImage(), "", 0, -2).replace("\\", ""));
        } else {
            mssqlIcon = operationIcon.get("IconNotFound");
            stringBuilder.append(prepareHTMLNodeIcon(queryType, "", mssqlIcon.getImage(), "", 0, -2).replace("\\", ""));
        }
        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    /**
     * Recursive function to parse xml.
     * @param nodeListQueryPlan
     * @param parentID 
     */
    private void parseRecusrsive(NodeList nodeListQueryPlan, int parentID) {
        if (nodeListQueryPlan != null) {
            int nodeLength = nodeListQueryPlan.getLength();
            for (int i = 0; i < nodeLength; i++) {
                Node node = nodeListQueryPlan.item(i);
                if (node.hasChildNodes()) {
                    Element element = (Element) node;
                    if (REL_OP.equalsIgnoreCase(element.getTagName())) {
                        parentID = prepareChild(node, nodeListQueryPlan, parentID);
                    }
                    parseRecusrsive(nodeListQueryPlan.item(i).getChildNodes(), parentID);
                }
            }
        }
    }

    /**
     * Prepare child node.
     * @param parentNode
     * @param childNodes
     * @param parentID
     * @return
     */
    private int prepareChild(Node parentNode, NodeList childNodes, int parentID) {
        int previousParentID = parentID;
        Element element = (Element) parentNode;
        if (REL_OP.equalsIgnoreCase(element.getTagName())) {
            NamedNodeMap nodeAttributes = element.getAttributes();
            if (nodeAttributes != null) {
                if (nodeAttributes.getNamedItem(NODE_ID) != null
                        && nodeAttributes.getNamedItem(NODE_ID).getNodeValue() != null) {
                    parentID = Integer.parseInt(nodeAttributes.getNamedItem(NODE_ID).getNodeValue());
                }
            }
        }

        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element elementChild = (Element) childNodes.item(i);
                if (REL_OP.equals(elementChild.getTagName())) {
                    int childID = 0;
                    String operationType = "", logicalOperation = null, physicalOperation = null;
                    NamedNodeMap nodeAttributesChild = elementChild.getAttributes();
                    Map<String, String> nodeProperties = MSSQLUtil.prepareNodeProperties(nodeAttributesChild);

                    if (!nodeProperties.isEmpty()) {
                        if (nodeProperties.get(MSSQLExecutionPlanEnum.NodeId.toString()) != null) {
                            childID = Integer.parseInt(nodeProperties.get(MSSQLExecutionPlanEnum.NodeId.toString()));
                            /* oldParentID != 0 First element in the list */
                            if (previousParentID != 0 && previousParentID == childID) {
                                break;
                            }
                        }

                        if (nodeProperties.get(MSSQLExecutionPlanEnum.LogicalOp.toString()) != null) {
                            operationType = nodeProperties.get(MSSQLExecutionPlanEnum.LogicalOp.toString());
                            logicalOperation = nodeProperties.get(MSSQLExecutionPlanEnum.LogicalOp.toString());
                        }

                        if (nodeProperties.get(MSSQLExecutionPlanEnum.PhysicalOp.toString()) != null) {
                            physicalOperation = nodeProperties.get(MSSQLExecutionPlanEnum.PhysicalOp.toString());
                            operationType += ":" + physicalOperation;

                            Map<String, String> operationProperties = MSSQLUtil.prepareOperationProperties(physicalOperation, elementChild);
                            if (!operationProperties.isEmpty()) {
                                nodeProperties.putAll(operationProperties);
                            }
                        }

                        if (!operationType.trim().isEmpty()) {
                            stringBuilderHTMLReport.append("$(\"#").append(Constants.HTML_ID_PARENT).append(previousParentID).append("\")");
                            stringBuilderHTMLReport.append(".append(\"<div id = \\\"").append(Constants.HTML_ID_PARENT).append(childID).append("\\\"");
                            stringBuilderHTMLReport.append(" class = \\\"").append(Constants.CSS_MSSQL_NODE_CHILD).append("\\\">");
                            stringBuilderHTMLReport.append(getNodeIcon(operationType, logicalOperation, physicalOperation, nodeProperties, childID));
                            stringBuilderHTMLReport.append("</div><br/>\");\n");

                            String sourceNode = Constants.CSS_MSSQL_NODE_TABLE + childID;
                            String targetNode = previousParentID == -1 ? Constants.CSS_MSSQL_ROOT_NODE : Constants.CSS_MSSQL_NODE_TABLE + previousParentID;
                            stringBuilderHTMLReport.append(jsPlumb.getjsPlumbScript(sourceNode, targetNode, jsPlumb.LeftMiddle, jsPlumb.RightMiddle));
                        }
                    }
                }
            }
        }

        return parentID;
    }

    /**
     * Get node icon.
     * @param tagName
     * @param logicalOperation
     * @param physicalOperation
     * @param nodeProperties
     * @param parentID
     * @return 
     */
    private String getNodeIcon(String tagName, String logicalOperation, String physicalOperation, Map<String, String> nodeProperties, int parentID) {
        double estimatedCode = 0.0;
        String nodeName = "", newTagName = "", warningIcon = "";
        StringBuilder nodeAttribute = new StringBuilder("");
        if (nodeProperties != null && !nodeProperties.isEmpty()) {
            for (Map.Entry<String, String> nodeProperty : nodeProperties.entrySet()) {
                String strLabel = "";
                if ("Clustered Index Scan:Clustered Index Scan".equalsIgnoreCase(tagName)
                        || "Table Scan:Table Scan".equalsIgnoreCase(tagName)) {
                    if ("Table".equalsIgnoreCase(nodeProperty.getKey())) {
                        nodeName = nodeProperty.getValue() + "<br/>";
                        nodeName = nodeName.replace("[", "");
                        nodeName = nodeName.replace("]", "");
                    }
                } else if ("Clustered Index Seek:Clustered Index Seek".equalsIgnoreCase(tagName)
                        || "Index Seek:Index Seek".equalsIgnoreCase(tagName)) {
                    if ("Clustered Index Seek:Clustered Index Seek".equalsIgnoreCase(tagName)
                            && "Lookup".equalsIgnoreCase(nodeProperty.getKey())
                            && "true".equalsIgnoreCase(nodeProperty.getValue())) {
                        newTagName = "Key Lookup:Clustered";
                    }

                    if ("Table".equalsIgnoreCase(nodeProperty.getKey())) {
                        nodeName = nodeProperty.getValue() + "<br/>";
                        nodeName = nodeName.replace("[", "");
                        nodeName = nodeName.replace("]", "");
                    }
                } else if ("Delete:Table Delete".equalsIgnoreCase(tagName)
                        || "Update:Table Update".equalsIgnoreCase(tagName)) {
                    if ("Table".equalsIgnoreCase(nodeProperty.getKey())) {
                        nodeName = nodeProperty.getValue() + "<br/>";
                        nodeName = nodeName.replace("[", "");
                        nodeName = nodeName.replace("]", "");
                    }
                }

                if (MSSQLExecutionPlanEnum.EstimateOperatorCost.toString().equals(nodeProperty.getKey())) {
                    String statementSubTreeCost = String.valueOf(metaData.get("StatementSubTreeCost"));
                    estimatedCode = Math.round((Double.valueOf(nodeProperty.getValue()) / Double.valueOf(statementSubTreeCost)) * 100);
                }

                if ("NoJoinPredicate".equalsIgnoreCase(nodeProperty.getKey())) {
                    nodeName = "<font color=\\\"red\\\">Warning</font><br/>";
                    strLabel = "Warning:<br/>";
                    warningIcon = "Warning";
                }

                if ("Parallel".equalsIgnoreCase(nodeProperty.getKey()) && "true".equalsIgnoreCase(nodeProperty.getValue())) {
                    warningIcon = "Parallel";
                }

                nodeAttribute.append(nodeProperty.getKey()).append(" = ").append("\\\"<b>").append(strLabel).append(nodeProperty.getKey()).append("</b>: ").append(nodeProperty.getValue()).append("\\\" ");
            }
        }

        /* Check for newTagname(case: Clustered Index Seek -> Key Lookup) */
        tagName = !newTagName.isEmpty() ? newTagName : tagName;

        MSSQLIcon mSSQLIcon = null;
        
        MSSQLNodeValue mssqlNodeValue = xmlNodes.get(new MSSQLNodeKey(logicalOperation, physicalOperation));
        if(mssqlNodeValue != null){
            nodeName += mssqlNodeValue.getCaption();
            mSSQLIcon = mssqlNodeValue.getMssqlIcon();
        }else{
            nodeName += tagName;
            mSSQLIcon = operationIcon.get("IconNotFound");
        }
        
        if (mSSQLIcon != null) {
            return prepareHTMLNodeIcon(nodeName, nodeAttribute.toString(), mSSQLIcon.getImage(), warningIcon, estimatedCode, parentID);
        }
        return "";
    }

    /**
     * Get missing indices if any.
     * @param nodeList 
     */
    private void missingIndices(NodeList nodeList) {
        if (nodeList != null
                && nodeList.getLength() > 0) {
            List<String> createIndexQueres = new ArrayList<String>(0);
            for (int i = 0; i < nodeList.getLength(); i++) {
                String databaseName = "", schemaName = "", tableName = "", baseColumn = "", includeColumn = "", impact = "";
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    if (element != null && "MissingIndexGroup".equalsIgnoreCase(element.getNodeName())) {
                        if (element.hasAttribute("Impact")) {
                            impact = element.getAttribute("Impact");
                        }

                        if (element.hasChildNodes()) {
                            Element elementChild = null;
                            for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                                if (element.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                                    elementChild = (Element) element.getChildNodes().item(j);
                                    break;
                                }
                            }

                            if (elementChild != null && "MissingIndex".equalsIgnoreCase(elementChild.getNodeName())) {
                                NamedNodeMap attributes = elementChild.getAttributes();
                                if (attributes != null) {
                                    databaseName = attributes.getNamedItem("Database") != null ? attributes.getNamedItem("Database").getNodeValue() : databaseName;
                                    schemaName = attributes.getNamedItem("Schema") != null ? attributes.getNamedItem("Schema").getNodeValue() : schemaName;
                                    tableName = attributes.getNamedItem("Table") != null ? attributes.getNamedItem("Table").getNodeValue() : tableName;
                                }

                                NodeList nodeListChild = elementChild.getElementsByTagName("ColumnGroup");
                                for (int j = 0; j < nodeListChild.getLength(); j++) {
                                    Element elementColumnGroup = (Element) nodeListChild.item(j);
                                    if (elementColumnGroup.hasAttribute("Usage") && "EQUALITY".equals(elementColumnGroup.getAttribute("Usage"))) {
                                        NodeList nodeListColumn = elementColumnGroup.getElementsByTagName("Column");
                                        for (int k = 0; k < nodeListColumn.getLength(); k++) {
                                            if (k != 0) {
                                                baseColumn += ",";
                                            }
                                            baseColumn += ((Element) nodeListColumn.item(k)).getAttribute("Name");
                                        }
                                    } else if (elementColumnGroup.hasAttribute("Usage") && "INCLUDE".equals(elementColumnGroup.getAttribute("Usage"))) {
                                        NodeList nodeListColumn = elementColumnGroup.getElementsByTagName("Column");
                                        for (int k = 0; k < nodeListColumn.getLength(); k++) {
                                            if (k != 0) {
                                                includeColumn += ",";
                                            }
                                            includeColumn += ((Element) nodeListColumn.item(k)).getAttribute("Name");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (databaseName != null && !databaseName.isEmpty()
                        && schemaName != null && !schemaName.isEmpty()
                        && tableName != null && !tableName.isEmpty()
                        && !baseColumn.isEmpty()) {
                    StringBuilder createIndexQuery = new StringBuilder("");
                    if (impact != null && !impact.isEmpty()) {
                        createIndexQuery.append("(Impact : ").append(impact).append(")");
                    }
                    createIndexQuery.append("CREATE NONCLUSTERED INDEX IndxNc_").append(tableName.replace("[", "").replace("]", ""));
                    String[] baseColumns = baseColumn.split(",");
                    if (baseColumns.length > 1) {
                        createIndexQuery.append("_MultiCol").append((i + 1));
                    } else {
                        baseColumn = baseColumn.replace("[", "");
                        baseColumn = baseColumn.replace("]", "");
                        createIndexQuery.append("_");
                        createIndexQuery.append(baseColumn);
                    }
                    createIndexQuery.append(" ON ").append(databaseName).append(".").append(schemaName).append(".").append(tableName);
                    createIndexQuery.append(" (").append(baseColumn).append(")");

                    if (!includeColumn.trim().isEmpty()) {
                        createIndexQuery.append(" INCLUDE (").append(includeColumn).append(")");
                    }
                    createIndexQuery.append(";");
                    createIndexQueres.add(createIndexQuery.toString());
                }
            }
            metaData.put("MissingIndices", createIndexQueres);
        }
    }

    /**
     * Prepare HTML code for node.
     * @param nodeCaption
     * @param nodeAttribude
     * @param imageSource
     * @param warningIcon
     * @param estimatedOperationCost
     * @param parentID
     * @return 
     */
    private String prepareHTMLNodeIcon(String nodeCaption, String nodeAttribude, String imageSource,
            String warningIcon, double estimatedOperationCost, int parentID) {

        warningIcon = CommonUtil.value(warningIcon, "");
        String nodeID = parentID == -2 ? Constants.CSS_MSSQL_ROOT_NODE : Constants.CSS_MSSQL_NODE_TABLE + parentID;
        MSSQLIcon mssqlIcon = null;

        if (operationIcon.get(warningIcon) != null) {
            mssqlIcon = operationIcon.get(warningIcon);
            warningIcon = "<div class = \\\"" + Constants.CSS_MSSQL_NODE_WARNING_ICON + "\\\" style = \\\"background: url('" + HTMLUtil.CSS_ICON_IMAGE + "') no-repeat " + mssqlIcon.getImage() + "\\\"></div>";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table class = \\\"").append(Constants.CSS_MSSQL_NODE_TABLE).append("\\\" id = \\\"").append(nodeID).append("\\\">");
        stringBuilder.append("<tr>").append("<td class = \\\"").append(Constants.CSS_MSSQL_NODE_FIXED_WIDTH).append("\\\">");
        stringBuilder.append("<div class  = \\\"").append(Constants.CSS_NODE_IMAGE).append(" ").append(Constants.CSS_MSSQL_NODE_ICON).append("\\\" ");
        stringBuilder.append("style = \\\"background: url('").append(HTMLUtil.CSS_ICON_IMAGE).append("') no-repeat ").append(imageSource).append("\\\" ");
        stringBuilder.append("title = \\\"").append(nodeCaption).append("\\\" ").append(nodeAttribude).append(" >");
        stringBuilder.append(warningIcon);
        stringBuilder.append("</div>");
        stringBuilder.append("</td>").append("</tr>");
        stringBuilder.append("<tr>").append("<td class=\\\"").append(Constants.CSS_MSSQL_NODE_NAME).append("\\\">").append(nodeCaption).append("<br/>").append(estimatedOperationCost).append(" %</td></tr>");
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    @Override
    public Map<String, Object> metaData() {
        return metaData;
    }

    @Override
    public String stylesheet() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("  ").append("<style>\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_GRAPHICAL_NODE).append("{height:500px;max-height:500px;overflow:scroll;white-space:nowrap;position:relative}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE).append("{display:inline-block;vertical-align:top;text-align:center}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_WARNING_ICON).append("{position:absolute;bottom:0px;right:0px;width:16px;height:16px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_ICON).append("{position:relative;margin:0px auto;width:32px;height:32px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_FIXED_WIDTH).append("{width:95px}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_NAME).append("{width:95px;white-space:pre-wrap;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_CHILD).append("{padding-bottom:20px;display:inline-block;padding-left:95px;}\n");
        stringBuilder.append("      ").append(".").append(Constants.CSS_MSSQL_NODE_TABLE).append("{width:95px !important;height: 95px !important;display:inline-block;vertical-align:top;text-align:center;font-size:12px;margin:0px auto}");
        stringBuilder.append("  ").append("</style>\n");
        return stringBuilder.toString();
    }

    @Override
    public String name() {
        return "Microsoft SQL Server";
    }
}