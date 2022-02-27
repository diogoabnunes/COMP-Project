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
 * The kind of function call for CALL instructions.
 */
public enum CallType {
        invokevirtual,
        invokeinterface,
        invokespecial,
        invokestatic,
        NEW,
        arraylength,
        ldc
}
