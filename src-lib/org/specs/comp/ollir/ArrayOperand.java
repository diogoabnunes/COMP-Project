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

import java.util.ArrayList;

/**
 * The class that represent each operand of type array.
 */
public class ArrayOperand extends Operand {

    // list of the indexing, one per array dimension
    ArrayList<Element> listOfIndexes;

    public void addIndexOperand(Operand indexOperand) {
        this.listOfIndexes.add(indexOperand);
    }

    public void addIndexOperand(ArrayList<Element> indexOperands) {
        this.listOfIndexes = indexOperands;
    }

    public ArrayList<Element> getIndexOperands() {
        return this.listOfIndexes;
    }

    public ArrayOperand(String name, Type tp1) {
        super(name, tp1);
        this.listOfIndexes = new ArrayList<Element>();
    }

    public ArrayOperand(String name, Type tp1, ArrayList<Element> listOfIndexes) {
        super(name, tp1);
        this.listOfIndexes = listOfIndexes;
    }
}
