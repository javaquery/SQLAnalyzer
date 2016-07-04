/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.database.mssql;

/**
 * @author vicky.thakor
 * @date 1st July, 2016
 */
public class MSSQLNodeKey{
   private String logicalOperation;
   private String physicalOperation;

    public MSSQLNodeKey(String logicalOperation, String physicalOperation) {
        this.logicalOperation = logicalOperation;
        this.physicalOperation = physicalOperation;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.logicalOperation != null ? this.logicalOperation.hashCode() : 0);
        hash = 53 * hash + (this.physicalOperation != null ? this.physicalOperation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MSSQLNodeKey other = (MSSQLNodeKey) obj;
        if ((this.logicalOperation == null) ? (other.logicalOperation != null) : !this.logicalOperation.equals(other.logicalOperation)) {
            return false;
        }
        if ((this.physicalOperation == null) ? (other.physicalOperation != null) : !this.physicalOperation.equals(other.physicalOperation)) {
            return false;
        }
        return true;
    }
}
