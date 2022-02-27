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
 * Class representing the exceptions thrown when an error is identified when loading the OLLIR input.
 */
public class OllirErrorException extends Exception {

    public OllirErrorException(String errorMessage) {
        super(errorMessage);
    }
}
