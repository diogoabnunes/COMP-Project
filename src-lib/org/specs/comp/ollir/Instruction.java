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
 * Class representing an OLLIR instruction.
 * This extends the Node class, which is used for the CFG.
 */
public abstract class Instruction extends Node {

    //protected Operation instOp;

    protected InstructionType instType;

    boolean isAssign;

    public abstract void show();

/*
    public void show() {
        System.out.println("\t"+this.instType);
    }
*/

    public InstructionType getInstType() {
        return this.instType;
    }

    public Instruction(InstructionType tp) {
        this.instType = tp;
    }

}