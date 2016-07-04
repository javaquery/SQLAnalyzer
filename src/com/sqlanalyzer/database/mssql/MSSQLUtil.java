/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.database.mssql;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author vicky.thakor
 * @date 29th June, 2016
 */
public class MSSQLUtil {

    /**
     * Position of properties matter so inserted in order.
     *
     * @param nodeAttributes
     * @return
     */
    public static Map<String, String> prepareNodeProperties(NamedNodeMap nodeAttributes) {
        Map<String, String> nodeProperties = new LinkedHashMap<String, String>();
        if (nodeAttributes != null) {
            MSSQLExecutionPlanEnum[] executionPlanEnums = MSSQLExecutionPlanEnum.values();
            Arrays.sort(executionPlanEnums, MSSQLExecutionPlanEnum.comparator());

            for (MSSQLExecutionPlanEnum executionPlanEnum : executionPlanEnums) {
                if (nodeAttributes.getNamedItem(executionPlanEnum.toString()) != null
                        && nodeAttributes.getNamedItem(executionPlanEnum.toString()).getNodeValue() != null
                        && !nodeAttributes.getNamedItem(executionPlanEnum.toString()).getNodeValue().isEmpty()) {
                    nodeProperties.put(executionPlanEnum.toString(), nodeAttributes.getNamedItem(executionPlanEnum.toString()).getNodeValue());
                }
            }

            if (nodeProperties.get(MSSQLExecutionPlanEnum.EstimateIO.toString()) != null
                    && nodeProperties.get(MSSQLExecutionPlanEnum.EstimateCPU.toString()) != null) {
                double EstimateIO = Double.valueOf(nodeProperties.get(MSSQLExecutionPlanEnum.EstimateIO.toString()));
                double EstimateCPU = Double.valueOf(nodeProperties.get(MSSQLExecutionPlanEnum.EstimateCPU.toString()));
                nodeProperties.put(MSSQLExecutionPlanEnum.EstimateOperatorCost.toString(), String.valueOf(EstimateIO + EstimateCPU));
            }

            if (nodeAttributes.getNamedItem(MSSQLExecutionPlanEnum.AvgRowSize.toString()) != null
                    && nodeAttributes.getNamedItem(MSSQLExecutionPlanEnum.AvgRowSize.toString()).getNodeValue() != null
                    && !nodeAttributes.getNamedItem(MSSQLExecutionPlanEnum.AvgRowSize.toString()).getNodeValue().isEmpty()) {
                double convertSizeInKB = Long.valueOf(nodeAttributes.getNamedItem(MSSQLExecutionPlanEnum.AvgRowSize.toString()).getNodeValue());
                convertSizeInKB = Math.round(convertSizeInKB / 1024);
                nodeProperties.put(MSSQLExecutionPlanEnum.AvgRowSize.toString(), convertSizeInKB + "KB");
            }
        }
        return nodeProperties;
    }

    /**
     * Prepare operation properties.
     * @param operationType
     * @param elementRelOp
     * @return 
     */
    public static Map<String, String> prepareOperationProperties(String operationType, Element elementRelOp) {
        Map<String, String> operationProperties = new LinkedHashMap<String, String>();

        if (elementRelOp.hasChildNodes()) {
            for (int i = 0; i < elementRelOp.getChildNodes().getLength(); i++) {
                if (elementRelOp.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element elementWarnings = (Element) elementRelOp.getChildNodes().item(i);
                    if ("Warnings".equalsIgnoreCase(elementWarnings.getNodeName())) {
                        getAttributes(elementWarnings.getAttributes(), operationProperties);
                    }
                }
            }
        }

        if ("Sort".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Sort") != null) {
                Element elementSort = (Element) elementRelOp.getElementsByTagName("Sort").item(0);
                if (elementSort.hasChildNodes()) {
                    for (int i = 0; i < elementSort.getChildNodes().getLength(); i++) {
                        if (elementSort.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementSort.getChildNodes().item(i);
                            if ("OrderBy".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                for (int j = 0; j < elementChildNode.getChildNodes().getLength(); j++) {
                                    if (elementChildNode.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        Element elementOrderByColumn = (Element) elementChildNode.getChildNodes().item(j);
                                        if (elementOrderByColumn.getAttributes() != null
                                                && elementOrderByColumn.getAttribute("Ascending") != null) {
                                            operationProperties.put("Ascending", elementOrderByColumn.getAttribute("Ascending"));
                                        }

                                        if (elementOrderByColumn.hasChildNodes()) {
                                            for (int k = 0; k < elementOrderByColumn.getChildNodes().getLength(); k++) {
                                                if (elementOrderByColumn.getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {
                                                    Element elementColumnReference = (Element) elementOrderByColumn.getChildNodes().item(j);
                                                    if ("ColumnReference".equalsIgnoreCase(elementColumnReference.getNodeName())) {
                                                        NamedNodeMap namedNodeMap = elementColumnReference.getAttributes();
                                                        getAttributes(namedNodeMap, operationProperties);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if ("Table Scan".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("TableScan") != null) {
                Element elementTableScan = (Element) elementRelOp.getElementsByTagName("TableScan").item(0);

                /* Attributes of IndexScan node */
                NamedNodeMap namedNodeMapTableScan = elementTableScan.getAttributes();
                getAttributes(namedNodeMapTableScan, operationProperties);

                if (elementTableScan.hasChildNodes()) {
                    for (int i = 0; i < elementTableScan.getChildNodes().getLength(); i++) {
                        if (elementTableScan.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementTableScan.getChildNodes().item(i);
                            if ("Object".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                NamedNodeMap namedNodeMap = elementChildNode.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Nested Loops".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("NestedLoops") != null) {
                Node NestedLoopNode = elementRelOp.getElementsByTagName("NestedLoops").item(0);
                if (NestedLoopNode.hasChildNodes()) {
                    for (int k = 0; k < NestedLoopNode.getChildNodes().getLength(); k++) {
                        if (NestedLoopNode.getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) NestedLoopNode.getChildNodes().item(k);
                            if ("OuterReferences".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                if (elementChildNode.hasChildNodes()) {
                                    for (int i = 0; i < elementChildNode.getChildNodes().getLength(); i++) {
                                        if (elementChildNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                                            Element ColumnReferenceElement = (Element) elementChildNode.getChildNodes().item(i);
                                            if ("ColumnReference".equalsIgnoreCase(ColumnReferenceElement.getNodeName())) {
                                                NamedNodeMap namedNodeMap = ColumnReferenceElement.getAttributes();
                                                getAttributes(namedNodeMap, operationProperties);
                                            }
                                        }
                                    }
                                }
                            } else if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                if (elementChildNode.hasChildNodes()) {
                                    Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                    NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                    getAttributes(namedNodeMap, operationProperties);
                                }
                            }
                        }
                    }
                }
            }
        } else if ("Merge Join".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Merge") != null) {
                Element MergeElement = (Element) elementRelOp.getElementsByTagName("Merge").item(0);

                /* Attributes of Merge node */
                NamedNodeMap namedNodeMapMerge = MergeElement.getAttributes();
                getAttributes(namedNodeMapMerge, operationProperties);

                if (MergeElement.hasChildNodes()) {
                    Element ResidualElement = (Element) MergeElement.getElementsByTagName("Residual").item(0);
                    if (ResidualElement.hasChildNodes()) {
                        Element elementScalarOperator = (Element) ResidualElement.getElementsByTagName("ScalarOperator").item(0);
                        NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                        getAttributes(namedNodeMap, operationProperties);
                    }
                }
            }
        } else if ("Clustered Index Scan".equalsIgnoreCase(operationType)
                || "RID Lookup".equalsIgnoreCase(operationType)
                || "Index Scan".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("IndexScan") != null) {
                Element elementIndexScan = (Element) elementRelOp.getElementsByTagName("IndexScan").item(0);

                /* Attributes of IndexScan node */
                NamedNodeMap namedNodeMapIndex = elementIndexScan.getAttributes();
                getAttributes(namedNodeMapIndex, operationProperties);

                if (elementIndexScan.hasChildNodes()) {
                    for (int i = 0; i < elementIndexScan.getChildNodes().getLength(); i++) {
                        if (elementIndexScan.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementIndexScan.getChildNodes().item(i);
                            if ("Object".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                NamedNodeMap namedNodeMap = elementChildNode.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Clustered Index Seek".equalsIgnoreCase(operationType) || "Index Seek".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("IndexScan") != null) {
                Element elementIndexScan = (Element) elementRelOp.getElementsByTagName("IndexScan").item(0);

                /* Attributes of IndexScan node */
                NamedNodeMap namedNodeMapIndex = elementIndexScan.getAttributes();
                getAttributes(namedNodeMapIndex, operationProperties);

                if (elementIndexScan.hasChildNodes()) {
                    for (int i = 0; i < elementIndexScan.getChildNodes().getLength(); i++) {
                        if (elementIndexScan.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementIndexScan.getChildNodes().item(i);
                            if ("Object".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                NamedNodeMap namedNodeMap = elementChildNode.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Hash Match".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Hash") != null) {
                Element elementHash = (Element) elementRelOp.getElementsByTagName("Hash").item(0);

                if (elementHash.hasChildNodes()) {
                    for (int i = 0; i < elementHash.getChildNodes().getLength(); i++) {
                        if (elementHash.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementHash.getChildNodes().item(i);
                            if ("ProbeResidual".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("HashKeysBuild".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementColumnReference = (Element) elementChildNode.getElementsByTagName("ColumnReference").item(0);
                                NamedNodeMap namedNodeMap = elementColumnReference.getAttributes();
                                operationProperties.put("HashKeysBuild,HashKeysProbe", "");
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("HashKeysProbe".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementColumnReference = (Element) elementChildNode.getElementsByTagName("ColumnReference").item(0);
                                NamedNodeMap namedNodeMap = elementColumnReference.getAttributes();
                                operationProperties.put("HashKeysBuild,HashKeysProbe", "");
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Table Delete".equalsIgnoreCase(operationType)
                || "Table Update".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Update") != null) {
                Element elementTableDelete = (Element) elementRelOp.getElementsByTagName("Update").item(0);

                if (elementTableDelete.hasChildNodes()) {
                    for (int i = 0; i < elementTableDelete.getChildNodes().getLength(); i++) {
                        if (elementTableDelete.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementTableDelete.getChildNodes().item(i);
                            if ("Object".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                NamedNodeMap namedNodeMap = elementChildNode.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            } else if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Filter".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Filter") != null) {
                Element elementFilter = (Element) elementRelOp.getElementsByTagName("Filter").item(0);

                if (elementFilter.hasChildNodes()) {
                    for (int i = 0; i < elementFilter.getChildNodes().getLength(); i++) {
                        if (elementFilter.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementFilter.getChildNodes().item(i);
                            if ("Predicate".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        } else if ("Top".equalsIgnoreCase(operationType)) {
            if (elementRelOp.getElementsByTagName("Top") != null) {
                Element elementTop = (Element) elementRelOp.getElementsByTagName("Top").item(0);

                if (elementTop.hasChildNodes()) {
                    for (int i = 0; i < elementTop.getChildNodes().getLength(); i++) {
                        if (elementTop.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elementChildNode = (Element) elementTop.getChildNodes().item(i);
                            if ("TopExpression".equalsIgnoreCase(elementChildNode.getNodeName())) {
                                Element elementScalarOperator = (Element) elementChildNode.getElementsByTagName("ScalarOperator").item(0);
                                NamedNodeMap namedNodeMap = elementScalarOperator.getAttributes();
                                getAttributes(namedNodeMap, operationProperties);
                            }
                        }
                    }
                }
            }
        }

        return operationProperties;
    }

    /**
     * Get attributes of XML.
     * @param namedNodeMap
     * @param operationProperties 
     */
    private static void getAttributes(NamedNodeMap namedNodeMap, Map<String, String> operationProperties) {
        if (namedNodeMap != null && namedNodeMap.getLength() > 0) {
            for (int i = 0; i < namedNodeMap.getLength(); i++) {
                Node node = namedNodeMap.item(i);
                String attributeName = node.getNodeName();
                attributeName = "ScalarString".equalsIgnoreCase(attributeName) ? "Predicate/ProbeResidual" : attributeName;
                if (operationProperties.get(attributeName) != null) {
                    operationProperties.put(attributeName, operationProperties.get(node.getNodeName()) + ", " + node.getNodeValue());
                } else {
                    operationProperties.put(attributeName, node.getNodeValue());
                }
            }
        }
    }

}
