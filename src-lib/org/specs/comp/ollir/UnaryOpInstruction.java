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
 * Class representing the OLLIR instructions with unary operations.
 */
public class UnaryOpInstruction extends Instruction {

    Element rightOperand;

    Operation operation;

    public Element getRightOperand() {
        return rightOperand;
    }

    public Operation getUnaryOperation() {
        return operation;
    }

    public void setRightOperand(Element rightOperand) {
        this.rightOperand = rightOperand;
    }

    public void setUnaryOperation(Operation unaryOperation) {
        this.operation = unaryOperation;
    }

    public void show() {
        System.out.print("\t"+this.instType+" ");

        Element e1 = this.rightOperand;

        System.out.print(" "+this.operation.getOpType()+" ");

        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }
        System.out.println();

    }

    public UnaryOpInstruction(Operation oper, Element o1) {
        super(InstructionType.UNARYOPER);
        this.rightOperand = o1;
        this.operation = oper;
    }

    public UnaryOpInstruction(InstructionType tp) {
        super(tp);
    }
}
