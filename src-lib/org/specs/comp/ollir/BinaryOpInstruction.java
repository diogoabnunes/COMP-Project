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
 * Class representing all the instructions with 2 operands.
 */
public class BinaryOpInstruction extends UnaryOpInstruction {

    Element leftOperand;

    public void setLeftOperand(Element leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Element getLeftOperand() {
        return leftOperand;
    }

    public void show() {

        System.out.print("\t"+this.instType+" ");

        Element e1 = this.leftOperand;

        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }

        System.out.print(" "+this.operation.getOpType()+" ");

        e1 = this.rightOperand;
        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }

        System.out.println();
    }

    public BinaryOpInstruction(Element o1, Operation oper, Element o2) {
        super(InstructionType.BINARYOPER);
        this.operation = oper;
        this.leftOperand = o1;
        this.rightOperand = o2;
    }
}
