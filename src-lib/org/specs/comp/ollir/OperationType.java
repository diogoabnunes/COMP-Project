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
 * The type of operation.
 * Not all the operations are currently supported.
 */
public enum OperationType {
    ADD,
    SUB,
    MUL,
    DIV,
    SHR,
    SHL,
    SHRR,
    XOR,
    AND,
    OR,
    LTH,
    GTH,
    EQ,
    NEQ,
    LTE,
    GTE,
    ADDI32,
    SUBI32,
    MULI32,
    DIVI32,
    SHRI32,
    SHLI32,
    SHRRI32,
    XORI32,
    ANDI32,
    ORI32,
    LTHI32,
    GTHI32,
    EQI32,
    NEQI32,
    LTEI32,
    GTEI32,
    ANDB,  // boolean
    ORB, // boolean
    NOTB, // boolean
    NOT
}
