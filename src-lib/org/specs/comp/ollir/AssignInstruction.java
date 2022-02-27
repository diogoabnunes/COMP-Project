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
 * Class representing assignments. dest = rhs
 */
public class AssignInstruction extends Instruction {

    Element dest;

    Type typeOfAssign;

    Instruction rhs;

    public Type getTypeOfAssign() {
        return typeOfAssign;
    }

    public Element getDest() {
        return dest;
    }

    public Instruction getRhs() {
        return rhs;
    }

    @Override
    public void show() {
        System.out.print("\t" + this.instType + " ");

        Operand o1 = (Operand) this.dest;
        System.out.print("Operand: " + o1.getName() + " " + o1.getType());

        System.out.print(" = ");

        this.rhs.show();
        // System.out.println();

    }

    public AssignInstruction(Element o1, Type tp1, Instruction i1) {
        super(InstructionType.ASSIGN);
        this.dest = o1;
        this.typeOfAssign = tp1;
        this.rhs = i1;
    }

    @Override
    public void setId(int id) {
        // Set its own id
        super.setId(id);

        // Set id of rhs
        rhs.setId(id);
    }
}
