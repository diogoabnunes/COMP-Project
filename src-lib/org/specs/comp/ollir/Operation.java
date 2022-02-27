/*
 * Compiler course
 *
 * Department of Informatics Engineering, Faculty of Engineering of the University of Porto
 * Porto, Portugal
 *
 * March 2021
 * @author João MP Cardoso
 */
package org.specs.comp.ollir;

/**
 * Class representing the operation in an OLLIR instruction.
 */
public class Operation {

    OperationType opType;

    Type typeInfo;

    public OperationType getOpType() {
        return this.opType;
    }

    public void setOpType(OperationType opType) {
        this.opType = opType;
    }

    public Operation(OperationType opType, Type tp1) {
        this.opType = opType;
        this.typeInfo = tp1;
    }

}
