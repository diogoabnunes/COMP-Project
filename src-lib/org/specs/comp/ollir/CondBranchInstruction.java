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
 * Class representing the OLLIR conditional instructions.
 * They have a comparison and a label to which the flow must jump
 * if the comparison is evaluated as true.
 */
public class CondBranchInstruction extends Instruction {

    Element rightOperand;
    Element leftOperand;

    Operation condOperation;

    String label;

    public Element getLeftOperand() {
        return leftOperand;
    }

    public Element getRightOperand() {
        return rightOperand;
    }

    public Operation getCondOperation() {
        return condOperation;
    }

    public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
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

            System.out.print(" "+this.condOperation.getOpType()+" ");

            e1 = this.rightOperand;
            if(e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
                System.out.print("Literal: "+((LiteralElement) e1).getLiteral());
            } else {
                Operand o1 = (Operand) e1;
                System.out.print("Operand: "+o1.getName()+" "+o1.getType());
            }

            System.out.println(" Label: "+this.label);
        }

        public CondBranchInstruction(Element o1, Element o2, Operation operation, String label) {
            super(InstructionType.BRANCH);
            this.leftOperand = o1;
            this.rightOperand = o2;
            this.condOperation = operation;
            this.label = label;
        }

    public CondBranchInstruction(Element o1, Element o2, Operation operation) {
            super(InstructionType.BRANCH);
            this.leftOperand = o1;
            this.rightOperand = o2;
            this.condOperation = operation;
        }

}
