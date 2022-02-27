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
 * Class representing the field instructions: getfield, getstatic.
 */
public class GetFieldInstruction extends Instruction {

    Element firstOperand;
    Element secondOperand;

    Type fieldType;

    public void setFirstOperand(Element firstOperand) {
        this.firstOperand = firstOperand;
    }

    public Element getSecondOperand() {
        return secondOperand;
    }

    public Element getFirstOperand() {
        return firstOperand;
    }

    public void setSecondOperand(Element secondOperand) {
        this.secondOperand = secondOperand;
    }

    public void show() {
        System.out.print("\t"+this.instType+" getfield ");

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
        System.out.println();

    }

    public GetFieldInstruction(Element firstOperand, Element secondOperand, Type fieldType) {
        super(InstructionType.GETFIELD);
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.fieldType = fieldType;
    }

    public GetFieldInstruction(InstructionType tp) {
        super(tp);
    }
}
