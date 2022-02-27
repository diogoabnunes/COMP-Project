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
 * Class representing the goto instructions.
 * Those instructions include a label to which the flow must jump.
 */
public class GotoInstruction extends Instruction {

    String label;

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void show() {
        System.out.println("\t"+this.instType+" "+this.label);
    }

    public GotoInstruction(String label) {
        super(InstructionType.GOTO);
        this.label = label;
    }
}
