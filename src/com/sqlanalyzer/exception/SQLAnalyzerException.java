/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.exception;

/**
 * @author vicky.thakor
 */
public class SQLAnalyzerException extends RuntimeException{

    public SQLAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}
