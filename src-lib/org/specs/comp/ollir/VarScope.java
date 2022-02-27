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
 * The scope of the variables considering three scopes in OLLIR:
 * field (class variable), method local variables, method parameters
 */
public enum VarScope {
    LOCAL,
    PARAMETER,
    FIELD
}
