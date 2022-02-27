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
 *
 */
public class ArrayType extends Type {

    // number of dimensions of the array
    int numDimensions;

    int[] sizeDimensions;

    ElementType typeOfElements;

    public void setTypeOfElements(ElementType typeOfElements) {
        this.typeOfElements = typeOfElements;
    }

    public ElementType getTypeOfElements() {
        return this.typeOfElements;
    }

    public void setNumDimensions(int numDimensions) {
        this.numDimensions = numDimensions;
        this.sizeDimensions = new int[numDimensions];
    }

    public int getNumDimensions() {
        return this.numDimensions;
    }

    public void show() {
        // translate array dimensions to "[..."
        StringBuilder array = new StringBuilder();
        for(int i = 0; i< numDimensions; i++) {
           array.append("["+this.sizeDimensions[i]);
        }
        System.out.println("Type: "+this.typeOfElement+" "+array+this.typeOfElements);
    }


    /**
     * Multidimensional array.
     * @param numDim    number of dimensions of the array
     * @param sizes     the size of each dimension
     * @param typeOfElements    the type of each array element
     */
    public ArrayType(int numDim, int[] sizes, ElementType typeOfElements) {
        super(ElementType.ARRAYREF);
        this.numDimensions = numDim;
        this.sizeDimensions = new int[numDim];
        // for now a manual array copy:
        for(int i=0; i<numDim; i++) {
            this.sizeDimensions[i] = sizes[i];
        }
        this.typeOfElements = typeOfElements;
    }

    /**
     * Unidimensional array.
     * @param size  the size (no. of elements) of the 1D array
     * @param typeOfElements    the type of the elements of the array
     */
    public ArrayType(int size, ElementType typeOfElements) {
        super(ElementType.ARRAYREF);
        this.numDimensions = 1;
        this.sizeDimensions = new int[1];
        this.sizeDimensions[0] = size;
        this.typeOfElements = typeOfElements;
    }

    public ArrayType() {
        super(ElementType.ARRAYREF);
    }
}
