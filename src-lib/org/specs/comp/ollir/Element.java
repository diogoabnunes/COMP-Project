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
 * Class representing the elements used in the OLLIR instructions.
 */
public class Element {

    Type typeOfElement;

    boolean isLiteral;

    public boolean isLiteral() {
        return isLiteral;
    }

    public void setLiteral(boolean literal) {
        isLiteral = literal;
    }

    public void show() {
        System.out.println("\t\t"+this.typeOfElement);
    }

    public Type getType() {
        return this.typeOfElement;
    }

    public void setType(Type typeOfElement) {
        this.typeOfElement = typeOfElement;
    }

    public Element(Type typeOfElement) {
        this.typeOfElement = typeOfElement;
    }

}
