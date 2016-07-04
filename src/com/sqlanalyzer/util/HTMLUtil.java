/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

import com.sqlanalyzer.exception.SQLAnalyzerException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author vicky.thakor
 * @date 6th May, 2016
 */
public class HTMLUtil {

    public static String CSS_ICON_IMAGE = "SQLAnalyzerIconImage.png";
    public static String DATABASE_ENGINE = "DatabaseEngine";
    public static String GRAPHICAL_DATA = "GraphicalData";
    public static String QUERY = "Query";
    public static String METADATA = "MetaData";

    /**
     * Prepare script tag from given source.
     * @param source
     * @param defaultValue
     * @return 
     */
    public static String prepareScriptTag(String source, String defaultValue) {
        if (source != null && !source.trim().isEmpty()) {
            return "<script src = \"" + source + "\"></script>\n";
        } else if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            return "<script src = \"" + defaultValue + "\"></script>\n";
        } else {
            return "";
        }
    }

    /**
     * Prepare style tag from given source.
     * @param source
     * @param defaultValue
     * @return 
     */
    public static String prepareStyleTag(String source, String defaultValue) {
        if (source != null && !source.trim().isEmpty()) {
            return "<link rel=\"stylesheet\" href=\"" + source + "\" type=\"text/css\">\n";
        } else if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            return "<link rel=\"stylesheet\" href=\"" + defaultValue + "\" type=\"text/css\">\n";
        } else {
            return "";
        }
    }

    /**
     * Javascript for node details
     *
     * @author vicky.thakor
     * @since 1.0
     * @return
     */
    public static String javaScripts() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("  <script>\n");
        stringBuilder.append("       ").append("$(document).ready(function(){\n");
        stringBuilder.append("       ").append("$(\".").append(Constants.CSS_NODE_IMAGE).append("\").live(\"click\",function(){\n");
        stringBuilder.append("		$(this).each(function() {\n");
        stringBuilder.append("			var htmlContent = \"<tr><td><a style=\\\"color:blue;text-decoration: underline;cursor: pointer;\\\" id=\\\"").append(Constants.HTML_ID_NODE_PROPERTIES_CLOSE_BUTTON).append("\\\">close</a></td></tr>\";\n");
        stringBuilder.append("			var count = 0;\n");
        stringBuilder.append("			$.each(this.attributes, function() {\n");
        stringBuilder.append("				if(this.specified) {\n");
        stringBuilder.append("					if(this.name != \"style\" && this.name != \"class\" && this.name != \"src\" && this.name != \"title\" && this.name != \"nodeid\" && this.name != \"id\"){\n");
        stringBuilder.append("						if(count == 0){\n");
        stringBuilder.append("							htmlContent += \"<tr>\";\n");
        stringBuilder.append("						}else if(count == 1){\n");
        stringBuilder.append("							htmlContent += \"</tr>\";\n");
        stringBuilder.append("							count = 0;\n");
        stringBuilder.append("						}\n");
        stringBuilder.append("						htmlContent += \"<td>\" + this.value; + \"</td>\";\n");
        stringBuilder.append("						count = count + 1;\n");
        stringBuilder.append("					}\n");
        stringBuilder.append("				}\n");
        stringBuilder.append("			});\n");
        stringBuilder.append("			$(\"#").append(Constants.HTML_ID_NODE_PROPERTIES).append("\").css(\"display\",\"block\");\n");
        stringBuilder.append("			$(\"#").append(Constants.HTML_ID_NODE_PROPERTIES).append("\").html(\"<table style=\\\"width:100%;font-size: 13px;\\\">\"+htmlContent+\"</table>\");\n");
        stringBuilder.append("		});\n");
        stringBuilder.append("	});\n");
        stringBuilder.append("	$(\"#").append(Constants.HTML_ID_NODE_PROPERTIES_CLOSE_BUTTON).append("\").live(\"click\",function(){\n");
        stringBuilder.append("		$(\"#").append(Constants.HTML_ID_NODE_PROPERTIES).append("\").css(\"display\",\"none\");\n");
        stringBuilder.append("	});\n");
        stringBuilder.append("      ").append("$(\".").append(Constants.CSS_ICON_GRAPHICAL_DATA).append("\").click(function(){\n");
        stringBuilder.append("          ").append("$(\".").append(Constants.CSS_QUERY_STATISTICS_DATA).append("\").css(\"display\", \"none\");\n");
        stringBuilder.append("          ").append("$(\".").append(Constants.CSS_GRAPHICAL_DATA).append("\").css(\"display\", \"block\");\n");
        stringBuilder.append("      ").append("});\n");
        stringBuilder.append("      ").append("$(\".").append(Constants.CSS_ICON_QUERY_STATISTICS).append("\").click(function(){\n");
        stringBuilder.append("          ").append("$(\".").append(Constants.CSS_GRAPHICAL_DATA).append("\").css(\"display\", \"none\");\n");
        stringBuilder.append("          ").append("$(\".").append(Constants.CSS_QUERY_STATISTICS_DATA).append("\").css(\"display\", \"block\");\n");
        stringBuilder.append("      ").append("});\n");
        stringBuilder.append("   });\n");
        stringBuilder.append("   </script>\n");
        return stringBuilder.toString();
    }

    /**
     * Custom style.
     * @return 
     */
    public static String customReportStyleSheet() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("  <style>\n");
        stringBuilder.append("      ").append(Constants.CSS_BODY).append("{margin:0;padding:0;width:100%;height:100%;overflow-x:hidden;background:rgb(224, 222, 222) !important;font-family:\"Helvetica Neue\", Helvetica, \"Segoe UI\", Arial, freesans, sans-serif}\n");
        stringBuilder.append("      .").append(Constants.CSS_BODY_CONTENT).append("{margin:5px 5px 10px 66px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_SIDE_BAR).append("{position:fixed;top:0px;left:0px;width:59px;height: 100%;text-align:center}\n");
        stringBuilder.append("      .").append(Constants.CSS_SMOOTH_BLUE_BACKGROUND).append("{background-color:#50b7dc !important;border-bottom:1px solid #2693ba;-webkit-box-shadow:rgba(0,0,0,0.3) 0px 2px 2px -1px;-moz-box-shadow:rgba(0,0,0,0.3) 0px 2px 2px -1px;box-shadow:rgba(0,0,0,0.3) 0px 2px 2px -1px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_H3).append(" h3{margin-top:3px;margin-bottom:5px}\n");
        stringBuilder.append("      .").append(Constants.CSS_REPORT_DATE).append("{position:fixed;top:2px;right:2px;font-size:12px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_WHITE_FADED_BOX).append("{-webkit-box-shadow:rgba(0, 0, 0, 0.2) 0px 2px 4px;box-shadow:rgba(0, 0, 0, 0.2) 0px 2px 4px;-webkit-transition:opacity 0.218s;background-color:white;border:1px solid rgba(0, 0, 0, 0.2);cursor:default;outline:none;padding: 10px;}\n");
        stringBuilder.append("      .").append(Constants.CSS_SIDE_BAR_ICON).append("{width:32px;height:32px;margin:0px auto;margin-top:10px;cursor:pointer}\n");
        stringBuilder.append("      .").append(Constants.CSS_ICON_GRAPHICAL_DATA).append("{background: url('").append(CSS_ICON_IMAGE).append("') no-repeat -8px -97px}\n");
        stringBuilder.append("      .").append(Constants.CSS_ICON_QUERY_STATISTICS).append("{background: url('").append(CSS_ICON_IMAGE).append("') no-repeat -56px -97px}\n");
        stringBuilder.append("      .").append(Constants.CSS_ICON_INFO).append("{background: url('").append(CSS_ICON_IMAGE).append("') no-repeat -104px -97px}\n");
        stringBuilder.append("      .").append(Constants.CSS_QUERY_STATISTICS_DATA).append("{display:none}\n");
        stringBuilder.append("      .").append(Constants.CSS_OVERFLOW).append("{overflow:scroll}");
        stringBuilder.append("      #").append(Constants.HTML_ID_NODE_PROPERTIES).append("{width:250px;max-height:400px;overflow:scroll;z-index:1;margin-top:1px;display:none;position:absolute;top:0px;right:20px;background-color:rgb(255, 255, 161)}\n");
        stringBuilder.append("      .").append(Constants.CSS_NODE_IMAGE).append("{margin:0px auto;cursor:pointer}\n");
        stringBuilder.append("      .").append(Constants.CSS_MARGIN_BOTTOM).append("{margin-bottom:5px}");
        stringBuilder.append("  </style>\n");
        return stringBuilder.toString();
    }

    /**
     * Prepare sidebar of report.
     * @return 
     */
    public static String prepareReportSidebar() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("      <div class = \"").append(Constants.CSS_SIDE_BAR).append(" ").append(Constants.CSS_SMOOTH_BLUE_BACKGROUND).append("\">\n");
        stringBuilder.append("          ").append("<div class = \"").append(Constants.CSS_SIDE_BAR_ICON).append(" ").append(Constants.CSS_ICON_GRAPHICAL_DATA).append("\"></div>\n");
        stringBuilder.append("          ").append("<div class = \"").append(Constants.CSS_SIDE_BAR_ICON).append(" ").append(Constants.CSS_ICON_QUERY_STATISTICS).append("\"></div>\n");
        stringBuilder.append("          ").append("<div class = \"").append(Constants.CSS_SIDE_BAR_ICON).append(" ").append(Constants.CSS_ICON_INFO).append("\"></div>\n");
        stringBuilder.append("      </div>\n");
        return stringBuilder.toString();
    }

    /**
     * Prepare html body of report.
     * @param properties
     * @return 
     */
    public static String prepareReportBody(Map<String, Object> properties) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("      ").append("<div class = \"").append(Constants.CSS_REPORT_DATE).append("\">").append(new Date().toString()).append("</div>\n");
        stringBuilder.append("      ").append("<div class = \"").append(Constants.CSS_BODY_CONTENT).append("\">\n");
        stringBuilder.append("          ").append("<div class = \"").append(Constants.CSS_GRAPHICAL_DATA).append(" ").append(Constants.CSS_H3).append("\">\n");
        stringBuilder.append("              ").append("<h3>Execution Plan - ").append(properties.get(DATABASE_ENGINE)).append("</h3>\n");
        stringBuilder.append("              ").append("It is the graphical representation of query executed on database. Each node in it plays significant role in query. Click on node for more information.<br/><br/>\n");
        stringBuilder.append("              ").append(properties.get(GRAPHICAL_DATA)).append("\n");
        stringBuilder.append("          ").append("</div>\n");
        stringBuilder.append("          ").append("<div class = \"").append(Constants.CSS_QUERY_STATISTICS_DATA).append(" ").append(Constants.CSS_H3).append("\">\n");
        stringBuilder.append("              ").append("<h3>Query Statistics</h3>\n");
        stringBuilder.append("              ").append("Executed Query<br/><br/>\n");
        stringBuilder.append("              ").append("<div class = \"").append(Constants.CSS_WHITE_FADED_BOX).append(" ").append(Constants.CSS_OVERFLOW).append("\">");
        stringBuilder.append("              ").append(properties.get(QUERY)).append("\n");
        stringBuilder.append("              ").append("</div>");
        stringBuilder.append("              ").append(console(properties.get(METADATA)));
        stringBuilder.append("          ").append("</div>\n");
        stringBuilder.append("      ").append("</div>\n");
        return stringBuilder.toString();
    }

    /**
     * Print meta data to console.
     * @param object
     * @return 
     */
    private static String console(Object object) {
        if (object != null) {
            try {
                Map<String, Object> metaData = (Map<String, Object>) object;
                if (!metaData.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder("");
                    stringBuilder.append("<br/>");
                    stringBuilder.append("<h3>Console</h3>\n");
                    stringBuilder.append("<div class = \"").append(Constants.CSS_WHITE_FADED_BOX).append(" ").append(Constants.CSS_OVERFLOW).append("\">");
                    for (Map.Entry<String, Object> entrySet : metaData.entrySet()) {
                        String key = entrySet.getKey();
                        Object value = entrySet.getValue();
                        
                        stringBuilder.append("<div class = \"").append(Constants.CSS_MARGIN_BOTTOM).append("\">");
                        if(value instanceof String){
                            stringBuilder.append(consoleLine("<b>" + key + "</b>"));
                            stringBuilder.append(consoleLine(value));
                        }else if(value instanceof Collection){
                            stringBuilder.append(consoleLine("<b>" + key + "</b>"));
                            Collection collection = (Collection) value;
                            for (Object col : collection) {
                                stringBuilder.append(consoleLine(col));
                            }
                        }
                        stringBuilder.append("</div>");
                    }
                    stringBuilder.append("</div>");
                    return stringBuilder.toString();
                }
            } catch (Exception e) {
                throw new SQLAnalyzerException(Constants.CONSOLE_ERROR, e);
            }
        }
        return "";
    }
    
    private static String consoleLine(Object content){
        if(content != null){
            String string = "<div class = \"" + Constants.CSS_MARGIN_BOTTOM + "\">";
            string += content;
            string += "</div>";
            return string;
        }
        return "";
    }
}
