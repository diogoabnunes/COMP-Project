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
 * Class representing the OLLIR return instructions.
 */
public class ReturnInstruction extends Instruction {

    Element operand;

    // boolean returnsValue;

    ElementType elementType;

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public Element getOperand() {
        return operand;
    }

    public void setOperand(Element op1) {
        this.operand = op1;
    }

    public boolean hasReturnValue() {
        return operand != null;
        // return returnsValue;
    }

    // public void setReturnValue(boolean hasReturnValue) {
    // this.returnsValue = hasReturnValue;
    // }

    @Override
    public void show() {

        System.out.print("\t" + this.instType + " ");

        if (!this.hasReturnValue())
            System.out.println("\t" + this.instType + " void");
        else {
            Element e1 = this.operand;
            if (e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
                System.out.println("Literal: " + ((LiteralElement) e1).getLiteral());
            } else {
                Operand o1 = (Operand) e1;
                System.out.println("Operand: " + o1.getName() + " " + o1.getType());
            }
        }
    }

    /**
     * with return value
     * 
     * @param op1
     *            element to return. It can be an operand or a literal
     */
    public ReturnInstruction(Element op1) {
        super(InstructionType.RETURN);
        this.operand = op1;
        // setReturnValue(true);
    }

    /**
     * without return value
     */
    public ReturnInstruction() {
        super(InstructionType.RETURN);
        this.operand = null;
    }
}
