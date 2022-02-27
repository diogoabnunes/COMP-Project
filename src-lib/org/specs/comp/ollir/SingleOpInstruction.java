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
 * Class representing the instructions with a single element. I.e., without other elements (operands) and without
 * operation. These instructions are in rhs of assignments such as a = b;
 */
public class SingleOpInstruction extends Instruction {

    // Instruction with a single operand and without operation
    Element singleOperand;

    public Element getSingleOperand() {
        return singleOperand;
    }

    public void setSingleOperand(Element singleOperand) {
        this.singleOperand = singleOperand;
    }

    @Override
    public void show() {
        if (this.singleOperand.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.println("\t" + this.instType + " Literal: " + ((LiteralElement) singleOperand).getLiteral());
        } else {
            Operand o1 = (Operand) this.singleOperand;
            System.out.println("\t" + this.instType + " Operand: " + o1.getName() + " " + o1.getType());
        }
    }

    public SingleOpInstruction(Element o1) {
        super(InstructionType.NOPER);
        this.singleOperand = o1;
    }
}
