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
 * The type of instruction.
 */
public enum InstructionType {
    ASSIGN,
    CALL,
    GOTO,
    BRANCH,
    RETURN,
    PUTFIELD,
    GETFIELD,
    UNARYOPER,
    BINARYOPER,
    NOPER
}
