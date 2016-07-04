/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

/**
 * @author vicky.thakor
 * @date 24th May, 2016
 */
public class jsPlumb {

    public static final String TopCenter = "TopCenter";
    public static final String BottomCenter = "BottomCenter";
    public static final String LeftMiddle = "LeftMiddle";
    public static final String RightMiddle = "RightMiddle";
    public static final String Center = "Center";
    public static final String TopRight = "TopRight";
    public static final String BottomRight = "BottomRight";
    public static final String TopLeft = "TopLeft";
    public static final String BottomLeft = "BottomLeft";
    
    /**
     * Use jsPlumb to connect two nodes with an Arrow.
     *
     * @author vicky.thakor
     * @param SourceNode
     * @param TargetNode
     * @param SourceArrowPosition
     * @param TargetArrowPosition
     * @return
     */
    public static String getjsPlumbScript(String SourceNode, String TargetNode, String SourceArrowPosition, String TargetArrowPosition) {
        StringBuilder jsPlumb = new StringBuilder("");
        jsPlumb.append("jsPlumb.bind(\"ready\", function() {");
        jsPlumb.append("jsPlumb.connect({");
        jsPlumb.append("source: \"").append(SourceNode).append("\",");
        jsPlumb.append("target: \"").append(TargetNode).append("\",");
        jsPlumb.append("anchors: [\"").append(SourceArrowPosition).append("\",\"").append(TargetArrowPosition).append("\"],");
        jsPlumb.append("endpoint: [\"Dot\", {radius: 1}],");
        jsPlumb.append("endpointStyle: {fillStyle: \"#5b9ada\"},");
        jsPlumb.append("setDragAllowedWhenFull: true,");
        jsPlumb.append("paintStyle: {strokeStyle: \"#5b9ada\",lineWidth: 3},");
        jsPlumb.append("connector: [\"Straight\"],");
        jsPlumb.append("connectorStyle: {lineWidth: 3,strokeStyle: \"#5b9ada\"},");
        jsPlumb.append("overlays: [[\"Arrow\", {width: 10,length: 10,foldback: 1,location: 1,id: \"arrow\"}]]");
        jsPlumb.append("});");
        jsPlumb.append("});");
        return jsPlumb.toString();
    }
}
