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
 * Class represents the OLLIR elements that are literals.
 */
public class LiteralElement extends Element {

    String literal;

    public String getLiteral() {
        return this.literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public LiteralElement(String literal, Type tp1) {
        super(tp1);
        this.literal = literal;
        this.isLiteral = true;
    }

    public LiteralElement(Type tp1) {
        super(tp1);
        this.isLiteral = true;
    }
}
