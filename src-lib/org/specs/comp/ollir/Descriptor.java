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
 * Class used to store the information regarding each variable of a method.
 * It is used in the symbol table of the variables for each method.
 */
public class Descriptor {

    // the associated virtual register: register allocation can change this to a real registers
    int virtualReg;

    VarScope scope;

    Type VarType;

    public Type getVarType() {
        return VarType;
    }

    public void setVarType(Type varType) {
        VarType = varType;
    }

    public void setVirtualReg(int virtualReg) {
        this.virtualReg = virtualReg;
    }

    public int getVirtualReg() {
        return this.virtualReg;
    }

    public Descriptor(int num) {
        this.virtualReg = num;
    }

    public VarScope getScope() {
        return this.scope;
    }

    public void setScope(VarScope scope) {
        this.scope = scope;
    }

    public Descriptor(VarScope type, int num) {
        this.scope = type;
        this.virtualReg = num;
    }

    public Descriptor(VarScope type, int num, Type tp) {
        this.scope = type;
        this.virtualReg = num;
        this.VarType = tp;
    }
}
