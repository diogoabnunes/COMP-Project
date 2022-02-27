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
 * The Type is a Class.
 */
public class ClassType extends Type {

    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void show() {
        System.out.println("Type: " + this.typeOfElement + " Name: " + this.name);
    }

    public ClassType(ElementType tp, String name) {
        super(tp);
        this.name = name;

        // assert tp != ElementType.CLASS && tp != ElementType.OBJECTREF && tp != ElementType.THIS;
    }

}
