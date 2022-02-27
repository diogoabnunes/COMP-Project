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
 * The type of each operand in an OLLIR instruction.
 */
public enum OperandType {
    INT32,
    BOOLEAN,
    ARRAYREF,
    OBJECTREF,
    THIS, // verify
    STRING,
    VOID
}
