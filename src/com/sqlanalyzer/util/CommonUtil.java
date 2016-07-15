/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.util;

import com.sqlanalyzer.Configurator;
import com.sqlanalyzer.DefaultConfigurator;
import com.sqlanalyzer.exception.SQLAnalyzerException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @author vicky.thakor
 */
public class CommonUtil {

    private static final Logger logger = Logger.getLogger(CommonUtil.class.getName());

    /**
     * Check value not null or return default value.
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static String value(String value, String defaultValue) {
        return value != null && !value.trim().isEmpty() ? value : defaultValue;
    }

    /**
     * putAll data to {@link Map} if not null.
     *
     * @param source
     * @param destination
     */
    public static void putAllNullCheck(Map source, Map destination) {
        if (source != null && destination != null) {
            destination.putAll(source);
        }
    }

    /**
     * Save data to file.
     *
     * @param path
     * @param filename
     * @param content
     * @return
     */
    public static String saveFile(String path, String filename, String content) {
        path = path == null ? System.getProperty("user.home") : path;
        filename = filename == null ? UUID.randomUUID().toString() : filename;

        File reportFolder = new File(path);
        /* Create directories if not exist */
        if (!reportFolder.exists()) {
            reportFolder.mkdirs();
        }

        File reportFile = new File(path + File.separatorChar + filename);
        /* Delete existing report file */
        if (reportFile.exists()) {
            reportFile.delete();
        }

        try {
            PrintWriter writer = new PrintWriter(path + File.separatorChar + filename, "UTF-8");
            writer.write(content);
            writer.close();
            String informationMessage = "SQLAnalyzer Report: \"" + reportFile.getAbsolutePath() + "\"";
            logger.info(informationMessage);
            return reportFile.getAbsolutePath();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Copy JavaScript and Image.
     *
     * @param configurator
     * @param folder
     */
    public static void copyStaticContent(Configurator configurator, String folder) {
        CommonUtil commonUtil = new CommonUtil();
        if (configurator.jQuerySource() == null || configurator.jQuerySource().trim().isEmpty()
                || DefaultConfigurator.jQuerySource.equalsIgnoreCase(configurator.jQuerySource())) {
            commonUtil.createDirectory(folder + File.separatorChar + "js");
            commonUtil.copyResourceFile("/com/sqlanalyzer/files/jquery-1.8.2.min.js", folder + File.separatorChar + DefaultConfigurator.jQuerySource);
        }

        if (configurator.jsPlumbSource() == null || configurator.jsPlumbSource().trim().isEmpty()
                || DefaultConfigurator.jsPlumbSource.equalsIgnoreCase(configurator.jsPlumbSource())) {
            commonUtil.createDirectory(folder + File.separatorChar + "js");
            commonUtil.copyResourceFile("/com/sqlanalyzer/files/jquery.jsPlumb-1.3.3-all.js", folder + File.separatorChar + DefaultConfigurator.jsPlumbSource);
        }

        if (configurator.iconImage() == null || configurator.iconImage().trim().isEmpty()
                || DefaultConfigurator.iconImage.equalsIgnoreCase(configurator.iconImage())) {
            commonUtil.copyImage("/com/sqlanalyzer/files/SQLAnalyzerIconImage.png", folder + File.separatorChar + DefaultConfigurator.iconImage);
        }
    }

    /**
     * Copy resource file to destination.
     *
     * @param source
     * @param destination
     */
    private void copyResourceFile(String source, String destination) {
        if (source != null && !source.trim().isEmpty()
                && destination != null && !destination.trim().isEmpty()) {
            File destinationFile = new File(destination);
            if (!destinationFile.exists()) {
                StringBuilder stringBuilder = new StringBuilder("");
                String line;
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                BufferedWriter bufferedWriter = null;

                try {
                    /* Read resource file */
                    inputStream = getClass().getResourceAsStream(source);
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append(System.getProperty("line.separator"));
                    }

                    /* Write to file */
                    destinationFile.createNewFile();
                    bufferedWriter = new BufferedWriter(new FileWriter(destinationFile));
                    bufferedWriter.write(stringBuilder.toString());
                } catch (Exception e) {
                    throw new SQLAnalyzerException("", e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            throw new SQLAnalyzerException("", e);
                        }
                    }

                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Exception e) {
                            throw new SQLAnalyzerException("", e);
                        }
                    }

                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.close();
                        } catch (Exception e) {
                            throw new SQLAnalyzerException("", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Create directories.
     *
     * @param folder
     */
    private void createDirectory(String folder) {
        try {
            File file = new File(folder);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            throw new SQLAnalyzerException("", e);
        }
    }

    private void copyImage(String source, String destination) {
        try {
            BufferedImage objBufferedImage = ImageIO.read(getClass().getResourceAsStream(source));
            ImageIO.write(objBufferedImage, "PNG", new File(destination));
        } catch (Exception e) {
            throw new SQLAnalyzerException("", e);
        }

    }
}
