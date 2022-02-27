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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents each method in the input OLLIR class.
 */
public class Method {

    private final ClassUnit ollirClass;

    String methodName;

    ArrayList<Element> paramList;

    ArrayList<Instruction> listOfInstr;

    // labels of the branch and goto instructions
    HashMap<String, Instruction> methodLabels;

    // variables used by the method
    HashMap<String, Descriptor> varTable;

    Type returnType;

    Node beginNode;
    Node endNode;
    boolean cfgInitialized;

    Map<Instruction, List<String>> labelsMap;

    /**
     * 
     * @param instruction
     * @return a String list with the labels of the given instruction, or an empty list if the instruction has no labels
     */
    public List<String> getLabels(Instruction instruction) {
        if (labelsMap == null) {
            labelsMap = buildLabelsMap();
        }

        var labels = labelsMap.get(instruction);

        return labels != null ? labels : Collections.emptyList();
    }

    private Map<Instruction, List<String>> buildLabelsMap() {
        var labelsMap = new HashMap<Instruction, List<String>>();

        for (var entry : methodLabels.entrySet()) {
            var inst = entry.getValue();

            var labels = labelsMap.get(inst);
            if (labels == null) {
                labels = new ArrayList<>();
                labelsMap.put(inst, labels);
            }

            labels.add(entry.getKey());
        }

        return labelsMap;
    }

    public Node getBeginNode() {
        if (!this.cfgInitialized) {
            throw new RuntimeException("CFG has not been initialized yet, please call .buildCFG()");
        }

        return beginNode;
    }

    public Node getEndNode() {
        if (!this.cfgInitialized) {
            throw new RuntimeException("CFG has not been initialized yet, please call .buildCFG()");
        }

        return endNode;
    }

    public HashMap<String, Descriptor> getVarTable() {
        return varTable;
    }

    public HashMap<String, Instruction> getLabels() {
        return methodLabels;
    }

    public void setReturnType(Type tp1) {
        this.returnType = tp1;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public ClassUnit getOllirClass() {
        return ollirClass;
    }

    AccessModifiers methodAccessModifier = AccessModifiers.DEFAULT;

    public Method(ClassUnit ollirClass) {
        this.ollirClass = ollirClass;
        this.paramList = new ArrayList<Element>();
        this.listOfInstr = new ArrayList<Instruction>();
        this.methodLabels = new HashMap<String, Instruction>();

        this.beginNode = new Node(NodeType.BEGIN);
        this.endNode = new Node(NodeType.END);
        this.cfgInitialized = false;

        this.varTable = new HashMap<String, Descriptor>();
    }

    boolean staticMethod = false;

    boolean finalMethod = false;

    boolean constructMethod = false;

    public void setMethodName(String name) {
        this.methodName = name;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public ArrayList<Instruction> getInstructions() {
        return this.listOfInstr;
    }

    public Instruction getInstr(int index) {
        return this.listOfInstr.get(index);
    }

    public void addInstr(Instruction instr) {
        this.listOfInstr.add(instr);
    }

    public ArrayList<Element> getParams() {
        return this.paramList;
    }

    public Element getParam(int index) {
        return this.paramList.get(index);
    }

    public void addParam(Element param) {
        this.paramList.add(param);
    }

    public void setMethodAccessModifier(AccessModifiers methodAccessModifier) {
        if (this.methodAccessModifier != AccessModifiers.DEFAULT)
            System.out.println("Warning: method access modifier previously set to: " + this.methodAccessModifier);
        else
            this.methodAccessModifier = methodAccessModifier;
    }

    public AccessModifiers getMethodAccessModifier() {
        return this.methodAccessModifier;
    }

    public boolean isStaticMethod() {
        return this.staticMethod;
    }

    public void setStaticMethod() {
        this.staticMethod = true;
    }

    public boolean isFinalMethod() {
        return this.finalMethod;
    }

    public void setFinalMethod() {
        this.finalMethod = true;
    }

    public boolean isConstructMethod() {
        return this.constructMethod;
    }

    public void setConstructMethod() {
        this.constructMethod = true;
    }

    public void addLabel(String label, Instruction i1) {
        // System.out.println("Label "+label);
        if (!this.methodLabels.containsKey(label))
            this.methodLabels.put(label, i1);
        else
            System.out.println("Label " + label + " already used!");
    }

    /**
     * Check if the labels used in the branch and goto instructions are associated with an instruction of the method. If
     * not, exit from the ollir processor.
     *
     * @throws OllirErrorException
     *             exception if the label used is a goto and a conditional branch does not exist.
     */
    public void checkLabels() throws OllirErrorException {
        String label;
        for (Instruction inst : listOfInstr) {
            if (inst.getInstType() == InstructionType.BRANCH) {
                label = ((CondBranchInstruction) inst).getLabel();
                if (!this.methodLabels.containsKey(label))
                    throw new OllirErrorException("Label " + label + " is not associated with an instruction!");
            } else

            if (inst.getInstType() == InstructionType.GOTO) {
                label = ((GotoInstruction) inst).getLabel();
                if (!this.methodLabels.containsKey(label))
                    throw new OllirErrorException("Label " + label + " is not associated with an instruction!");
            }
        }
    }

    /**
     * If the variable is not already in the varTable, create the Descriptor associated with the variable and update the
     * varTable.
     * 
     * @param e1
     *            Element of an Instruction
     * @param vs
     *            the scope of the variable
     * @param varID
     *            the ID assigned to the variable
     * @return returns an update of the varID
     */
    int updateTable(Element e1, VarScope vs, int varID) {
        if (!e1.isLiteral()) { // if the e1 is not a literal, then it is a variable
            var operand = (Operand) e1;

            var varName = operand.getName();

            // If it is an import, ignore
            if (getOllirClass().isImportedClass(varName)) {
                return varID;
            }

            if (!this.varTable.containsKey(varName)) { // if not already in the varTable
                // var operand = (Operand) e1;

                // If 'this', special case, is always 0
                if ("this".equals(varName)) {
                    Descriptor d1 = new Descriptor(vs, 0, e1.getType());
                    this.varTable.put(varName, d1);
                } else {
                    // Descriptor d1 = new Descriptor(vs, ++varID, e1.getType());
                    Descriptor d1 = new Descriptor(vs, varID, e1.getType());
                    varID++;
                    this.varTable.put(varName, d1);
                }
            }
        }
        return varID;
    }

    /**
     * This method adds to the symbol table of variables the vars used in each instruction and their associated
     * descriptors. This can be improved by moving to each instruction class the method to create and add the
     * descriptor.
     *
     * @param inst
     *            an object representing an OLLIR instruction
     * @param varID
     *            the id of virtual registers/variables
     */
    public int addToVartable(Instruction inst, int varID) {
        Element e1;
        switch (inst.getInstType()) {
        case CALL:
            CallInstruction i1 = (CallInstruction) inst;

            e1 = i1.getFirstArg();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            if (i1.getNumOperands() > 1) {
                // System.out.println(i1.getInstType()+" "+i1.getNumOperands());
                if (i1.invocationType != CallType.NEW) { // only new type instructions do not have a field with second
                                                         // arg
                    e1 = i1.getSecondArg();
                    varID = updateTable(e1, VarScope.LOCAL, varID);
                }
                ArrayList<Element> otherArgs = i1.getListOfOperands();
                for (Element arg : otherArgs) {
                    varID = updateTable(arg, VarScope.LOCAL, varID);
                }
            }
            break;
        case ASSIGN:
            AssignInstruction i2 = (AssignInstruction) inst;

            e1 = i2.getDest();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            Instruction i3 = i2.getRhs();
            varID = this.addToVartable(i3, varID);

            break;
        case BRANCH:
            CondBranchInstruction i4 = (CondBranchInstruction) inst;

            e1 = i4.getLeftOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            e1 = i4.getRightOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            break;
        case PUTFIELD:
            PutFieldInstruction i5 = (PutFieldInstruction) inst;

            e1 = i5.getFirstOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            e1 = i5.getSecondOperand();
            // varID = updateTable(e1, VarScope.FIELD, varID);
            updateTable(e1, VarScope.FIELD, -1);

            e1 = i5.getThirdOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            break;
        case GETFIELD:
            GetFieldInstruction i6 = (GetFieldInstruction) inst;

            e1 = i6.getFirstOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            e1 = i6.getSecondOperand();
            // varID = updateTable(e1, VarScope.FIELD, varID);
            updateTable(e1, VarScope.FIELD, -1);

            break;
        case UNARYOPER:
            UnaryOpInstruction i7 = (UnaryOpInstruction) inst;

            e1 = i7.getRightOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            break;
        case BINARYOPER:
            BinaryOpInstruction i8 = (BinaryOpInstruction) inst;

            e1 = i8.getLeftOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            e1 = i8.getRightOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            break;
        case NOPER:
            SingleOpInstruction i9 = (SingleOpInstruction) inst;

            e1 = i9.getSingleOperand();
            varID = updateTable(e1, VarScope.LOCAL, varID);

            break;
        default:
            break;
        }

        return varID;
    }

    /**
     * Builds a symbol table for the varaiables of the method: paramenters and local variables. It assigns a varID that
     * can be considered the ID of virtual registers or JVM local variables if targeting the JVM. The parameters use
     * varID from 0 (if the method is static) or 1 (if the mathod is not static) to the number of parameters of the
     * method (or -1 when teh varID starts with 0). The local variables use subsequent values for varID.
     *
     * The table uses a key which is the name of the variable and associates it with a Descriptor (see class
     * Descriptor).
     */
    public void buildVarTable() {
        // int varID = -1;
        int varID = isStaticMethod() ? 0 : 1;
        // System.out.println("INIT varID: " + varID);
        for (Element param : this.paramList) {
            // Parameters override the initial value of varID
            varID = ((Operand) param).getParamId();
            // System.out.println("PARAM varID: " + varID);
            Descriptor d1 = new Descriptor(VarScope.PARAMETER, varID++, param.getType());
            // Descriptor d1 = new Descriptor(VarScope.PARAMETER, ++varID, param.getType());
            // System.out.println("PARAM " + ((Operand) param).getName() + " at reg " + d1.getVirtualReg());
            this.varTable.put(((Operand) param).getName(), d1);
        }

        for (Instruction inst : this.listOfInstr) {
            // System.out.println("INST varID BEFORE: " + varID);
            varID = this.addToVartable(inst, varID);
            // System.out.println("INST varID AFTER: " + varID);
        }

    }

    /**
     * Builds the control-flow graph (CFG) of the method. It uses the ArrayList listOfInstr of instructions and assumes
     * that the instructions are in that ArrayList by the order they order in the input OLLIR.
     */
    public void buildCFG() {
        this.cfgInitialized = true;

        // methods without instructions just connect begin to the end
        if (this.listOfInstr.isEmpty()) {
            this.beginNode.addSucc(this.endNode);
            this.endNode.addPred(this.beginNode);
            return;
        }

        // connect first BEGIN with first instruction
        // this.beginNode.showNode();
        Node first = this.listOfInstr.get(0);
        this.beginNode.addSucc(first);
        first.addPred(this.beginNode);

        // add IDs to nodes and connect Return and last instructions to END
        int id = 1;
        for (Instruction inst : this.listOfInstr) {
            // inst.show();
            inst.setId(id++);
            if (inst.getInstType() == InstructionType.RETURN) {
                // System.out.println("RETURN +"+inst.getId());
                // inst.show();
                inst.addSucc(this.endNode);
                this.endNode.addPred(inst);
                // System.out.println("CHECK "+inst.getId()+" -> "+inst.getSucc1().getId());
            }
        }
        Instruction last = this.listOfInstr.get(this.listOfInstr.size() - 1);
        if (last.instType != InstructionType.GOTO) {
            last.addSucc(this.endNode);
            this.endNode.addPred(last);
        }

        // connect Goto and Conditional Branch instructions to instruction with label
        for (Instruction inst : this.listOfInstr) {
            if (inst.getInstType() == InstructionType.GOTO) {
                Instruction dest = this.methodLabels.get(((GotoInstruction) inst).getLabel());
                inst.addSucc(dest);
                dest.addPred(inst);
                // System.out.println("CHECK: "+inst.getId()+" -> "+dest.getId());
            } else if (inst.getInstType() == InstructionType.BRANCH) {
                Instruction dest = this.methodLabels.get(((CondBranchInstruction) inst).getLabel());
                inst.addSucc(dest);
                dest.addPred(inst);
                // System.out.println("CHECK: "+inst.getId()+" -> "+dest.getId());
            }
        }

        // connect adjacent instructions
        for (int i = 0; i < this.listOfInstr.size() - 1; i++) {
            Instruction src = this.listOfInstr.get(i);
            if (src.getInstType() != InstructionType.GOTO) {
                Instruction dest = this.listOfInstr.get(i + 1);
                src.addSucc(dest);
                dest.addPred(src);
            }
        }

    }

    /**
     * This method generates a textual dotty file .dot representing the CFG of the OLLIR code of the method. The dotty
     * file generated uses the name of the method. The https://graphviz.org/ Webpage interface:
     * https://dreampuf.github.io/GraphvizOnline/
     * 
     * @throws OllirErrorException
     *             an Exception in case of error
     */
    public void outputCFG() throws OllirErrorException {
        // String fileName = new String("."+File.separator);
        String fileName = "".concat(this.methodName).concat(".dot");
        // System.out.println("Filename: "+fileName);
        try {
            // PrintWriter file = new PrintWriter(new FileOutputStream(new File(fileName)));
            PrintWriter file = new PrintWriter(new FileOutputStream(fileName));
            file.println("digraph cfg {");
            file.println("\tbegin [shape=Msquare];");
            file.println("\tend [shape=Msquare];");

            Node succ = this.beginNode.getSucc1();
            file.print("\tbegin -> " + succ.getId() + ";");

            for (Instruction src : listOfInstr) {
                // src.show();
                Node dest1 = src.getSucc1();
                Node dest2 = src.getSucc2();
                if (src.getInstType() == InstructionType.BRANCH) {
                    if (dest1.getNodeType() != NodeType.END)
                        file.print("\t" + src.getId() + " -> " + dest1.getId() + "[label=T];");
                    else
                        file.print("\t" + src.getId() + " -> end [label=T];");
                    if (dest1.getNodeType() != NodeType.END)
                        file.print("\t" + src.getId() + " -> " + dest2.getId() + "[label=F];");
                    else
                        file.print("\t" + src.getId() + " -> end [label=F];");
                } else {
                    if (dest1.getNodeType() != NodeType.END)
                        file.print("\t" + src.getId() + " -> " + dest1.getId() + ";");
                    else
                        file.print("\t" + src.getId() + " -> end;");
                }
            }
            file.println("}");

            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new OllirErrorException(e.toString() + fileName);
        }
    }

    /**
     * Print the information regarding a method.
     */
    public void show() {
        System.out.println("--------------------------");
        System.out.println("*** Name of the method: " + this.methodName);
        System.out.println("\tConstruct method: " + this.constructMethod);
        System.out.println("\tAccess modifier: " + this.methodAccessModifier);
        System.out.println("\tStatic method: " + this.staticMethod);
        System.out.println("\tFinal method: " + this.finalMethod);

        System.out.println("\t* Parameters: ");
        for (Element param : paramList) {
            param.show();
        }

        System.out.print("\t* Return: ");
        returnType.show();

        System.out.println("\t* No. Instructions: " + this.listOfInstr.size());
        System.out.println("\n\t* Instructions:");
        for (Instruction inst : listOfInstr) {
            inst.show();
        }

        System.out.println("\n\t* Table of variables:");
        for (Map.Entry<String, Descriptor> entry : varTable.entrySet()) {
            String key = entry.getKey();
            Descriptor d1 = entry.getValue();
            System.out.println(
                    "\t\tVar name: " + key + " scope: " + d1.getScope() + " virtual register: " + d1.getVirtualReg());
        }
        System.out.println("--------------------------");
    }
}
