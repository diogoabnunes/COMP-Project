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
 * Class representing the fields of an OLLIR class.
 */
public class Field {

    AccessModifiers fieldAccessModifier = AccessModifiers.DEFAULT;

    boolean staticField = false;

    boolean finalField = false;

    String fieldName;

    boolean isInitialized = false;

    int initialValue;

    Type fieldType;

    public void setFieldType(Type type1) {
        this.fieldType = type1;
    }

    public Type getFieldType() {
        return this.fieldType;
    }

    public void setInitialValue(int val) {
        this.initialValue = val;
        this.isInitialized = true;
    }

    public int getInitialValue() {
        return this.initialValue;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public void setFieldName(String name) {
        this.fieldName = name;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldAccessModifier (AccessModifiers fieldAccessModifier) {
        if(this.fieldAccessModifier != AccessModifiers.DEFAULT)
            System.out.println("Warning: field access modifier previously set to: "+this.fieldAccessModifier);
        else
            this.fieldAccessModifier = fieldAccessModifier;
    }

    public AccessModifiers getFieldAccessModifier () {
        return this.fieldAccessModifier;
    }

    public boolean isStaticField() {
        return this.staticField;
    }

    public void setStaticField() {
        this.staticField = true;
    }

    public boolean isFinalField() {
        return this.finalField;
    }

    public void setFinalField() {
        this.finalField = true;
    }

    public void show() {
        System.out.println("*** Name of the field: "+this.fieldName);
        System.out.println("\tAccess modifier: "+this.fieldAccessModifier);
        System.out.println("\tStatic field: "+this.staticField);
        System.out.println("\tFinal field: "+this.finalField);
        if(this.isInitialized)
            System.out.println("\tInitial value: "+this.initialValue);
        this.fieldType.show();
    }
}
