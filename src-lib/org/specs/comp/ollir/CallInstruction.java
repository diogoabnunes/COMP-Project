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

import java.util.ArrayList;

/**
 * Class representing the CALL instructions. This class can be improved by removing the special cases with a fixed
 * number or arguments and assuming only a listOfOperands.
 */
public class CallInstruction extends Instruction {

    int numOperands;

    Element firstArg;

    Element secondArg;

    ArrayList<Element> listOfOperands;

    CallType invocationType;

    Type returnType;

    public int getNumOperands() {
        return numOperands;
    }

    public CallType getInvocationType() {
        return invocationType;
    }

    @Override
    public void show() {
        System.out.print("\t" + this.instType + " ");

        Element e1 = this.firstArg;
        if (e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            System.out.print("Literal: " + ((LiteralElement) e1).getLiteral());
        } else {
            Operand o1 = (Operand) e1;
            System.out.print("Operand: " + o1.getName() + " " + o1.getType());
        }

        if (this.numOperands > 1) {
            // System.out.println(i1.getInstType()+" "+i1.getNumOperands());
            if (this.invocationType != CallType.NEW) { // only new type instructions do not have a field with second arg
                e1 = this.secondArg;
                if (e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
                    System.out.print(", Literal: " + ((LiteralElement) e1).getLiteral());
                } else {
                    Operand o1 = (Operand) e1;
                    System.out.print(", Operand: " + o1.getName() + " " + o1.getType());
                }
            }
            ArrayList<Element> otherArgs = this.listOfOperands;
            for (Element arg : otherArgs) {
                if (arg.isLiteral()) { // if the e1 is not a literal, then it is a variable
                    System.out.print(", Literal: " + ((LiteralElement) arg).getLiteral());
                } else {
                    Operand o1 = (Operand) arg;
                    System.out.print(", Operand: " + o1.getName() + " " + o1.getType());
                }
            }
        }

        System.out.println();
    }

    public Element getFirstArg() {
        return firstArg;
    }

    public Element getSecondArg() {
        return secondArg;
    }

    public ArrayList<Element> getListOfOperands() {
        return listOfOperands;
    }

    public Type getReturnType() {
        return returnType;
    }

    public CallInstruction(CallType invocationType) {
        super(InstructionType.CALL);
        this.listOfOperands = new ArrayList<Element>();
        this.invocationType = invocationType;
        this.numOperands = 0;
    }

    public CallInstruction(CallType invocationType, Element o1, ArrayList<Element> listOfOperands, Type returnType) {
        super(InstructionType.CALL);
        this.listOfOperands = listOfOperands;
        this.invocationType = invocationType;
        this.firstArg = o1;
        this.returnType = returnType;
        this.numOperands = 1 + listOfOperands.size();
    }

    public CallInstruction(CallType invocationType, Element o1, Element o2, ArrayList<Element> listOfOperands,
            Type returnType) {
        super(InstructionType.CALL);
        this.listOfOperands = listOfOperands;
        this.invocationType = invocationType;
        this.firstArg = o1;
        this.secondArg = o2;
        this.returnType = returnType;
        this.numOperands = 2 + listOfOperands.size();
    }

    public CallInstruction(CallType invocationType, Element o1, Type returnType) {
        super(InstructionType.CALL);
        this.listOfOperands = null;
        this.invocationType = invocationType;
        this.firstArg = o1;
        this.secondArg = null;
        this.returnType = returnType;
        this.numOperands = 1;
    }
}
