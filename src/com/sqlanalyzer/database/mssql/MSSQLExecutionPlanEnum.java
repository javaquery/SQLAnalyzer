/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.database.mssql;

import java.util.Comparator;

/**
 * @author vicky.thakor
 * @date 29th June, 2016
 */
public enum MSSQLExecutionPlanEnum{

    AvgRowSize(8), EstimateCPU(4), EstimateIO(3), EstimateOperatorCost(5), EstimateRebinds(9),
    EstimateRewinds(10), EstimateRows(7), LogicalOp(2), NodeId(11), Parallel(12),
    PhysicalOp(1), EstimatedTotalSubtreeCost(6);

    private final Integer position;
    
    private MSSQLExecutionPlanEnum(Integer position) {
        this.position = position;
    }
    
    public static Comparator<MSSQLExecutionPlanEnum> comparator(){
        return new Comparator<MSSQLExecutionPlanEnum>() {

            @Override
            public int compare(MSSQLExecutionPlanEnum o1, MSSQLExecutionPlanEnum o2) {
                return o1.position.compareTo(o2.position);
            }
        };
    }
}
