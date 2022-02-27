/*
 * Compiler course
 *
 * Department of Informatics Engineering, Faculty of Engineering of the University of Porto Porto, Portugal
 *
 * March 2021
 * 
 * @author João MP Cardoso
 */
package org.specs.comp.ollir;

/**
 * Class representing the elements of OLLIR instructions that are not literals.
 */
public class Operand extends Element {

    boolean isParameter;

    int paramID;

    String name;

    public boolean isParameter() {
        return isParameter;
    }

    public int getParamId() {
        return this.paramID;
    }

    public void setParamId(int Id) {
        this.paramID = Id;
        this.isParameter = true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.typeOfElement.toString();
    }

    public Operand(Type typeOfElement) {
        super(typeOfElement);
        this.isParameter = false;
    }

    public Operand(String name, Type typeOfElement) {
        super(typeOfElement);
        this.name = name;
        this.isParameter = false;
    }
}
