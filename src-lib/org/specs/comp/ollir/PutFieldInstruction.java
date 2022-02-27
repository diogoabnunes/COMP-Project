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
 * Class representing the field instructions: putfield, putstatic.
 */
public class PutFieldInstruction extends GetFieldInstruction {

    Element thirdOperand;

    public Element getThirdOperand() {
        return this.thirdOperand;
    }

    public void setThirdOperand(Element thirdOperand) {
        this.thirdOperand = thirdOperand;
    }

    public void show() {

        System.out.print("\t"+this.instType+" putfield ");

        Element e1 = this.firstOperand;
        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }

        e1 = this.secondOperand;
        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }

        e1 = this.thirdOperand;
        if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: "+o1.getName()+" "+o1.getType());
        }
        System.out.println();
    }

    public PutFieldInstruction(Element firstOperand, Element secondOperand, Element thirdOperand, Type fieldType) {
        super(InstructionType.PUTFIELD);
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.thirdOperand = thirdOperand;
        this.fieldType = fieldType;
    }
}
