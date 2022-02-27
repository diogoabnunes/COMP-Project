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
 * Class representing the type of variables, assignments, returns, literals.
 */
public class Type {

    ElementType typeOfElement;

    public void show() {
        System.out.println("Type: "+this.typeOfElement);
    }

    public String toString() {
        return typeOfElement.toString();
    }

    public void setTypeOfElement(ElementType typeOfElement) {
        this.typeOfElement = typeOfElement;
    }

    public ElementType getTypeOfElement() {
        return this.typeOfElement;
    }

    public Type(ElementType typeOfElement) {
        this.typeOfElement = typeOfElement;
    }
}
