import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.Method;

import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Instruction;
import org.specs.comp.ollir.*;

import java.lang.reflect.Array;
import java.util.*;


public class JasminGenerator {
    private ClassUnit classUnit;
    private Method method;
    private HashMap<String, Instruction> labels = new HashMap<>();
    private HashMap<String, Integer> labelsAux = new HashMap<>();
    private HashMap<String, Integer> reservedFields = new HashMap<>();
    private int reservedFieldN = 0;

    public JasminGenerator(ClassUnit classUnit) {
        this.classUnit = classUnit;
    }
    
    private String jasminAccessModifier(AccessModifiers accessModifiers) {
        if (accessModifiers.name().equals("PRIVATE")) return "private";
        else return "public";
    }

    public String classToJasmin() {
        String jasminString = ".class " + jasminAccessModifier(classUnit.getClassAccessModifier()) + " " + classUnit.getClassName() + "\n";
        jasminString += ".super ";
        if (classUnit.getSuperClass() == null || classUnit.getSuperClass().equals(classUnit.getClassName())) {
            jasminString += "java/lang/Object\n";
        }
        else {
            jasminString += classUnit.getSuperClass() + "\n";
        }
        return jasminString + fieldsToJasmin() + methodToJasmin();
    }

    private String jasminIsStatic(boolean isStatic) {
        if (isStatic) return "static ";
        else return "";
    }

    public String methodToJasmin() {
        String jasminString = "";
        for (Method method: classUnit.getMethods()) {
            this.method = method;
            this.labels = method.getLabels();
            jasminString += "\n.method ";
            boolean hasReturnInstr = false;
            if (method.isConstructMethod()) {
                for (Instruction i: method.getInstructions()) {
                    if (i instanceof ReturnInstruction) {
                        hasReturnInstr = true;
                        break;
                    }
                }
                if (!hasReturnInstr) {
                    method.addInstr(new ReturnInstruction(new Element(new Type(ElementType.VOID))));
                }
                jasminString += jasminAccessModifier(method.getMethodAccessModifier()) + " " +
                    jasminIsStatic(method.isStaticMethod()) + "<init>(";
                for (Element element: method.getParams()) {
                    jasminString += convertElementType(element.getType());
                } 
                jasminString += ")" + convertElementType(method.getReturnType()) + "\n";
            
            }
            else {
                for (Instruction i: method.getInstructions()) {
                    if (i instanceof ReturnInstruction) {
                        hasReturnInstr = true;
                        break;
                    }
                }
                if (!hasReturnInstr) {
                    method.addInstr(new ReturnInstruction(new Element(new Type(ElementType.VOID))));
                }
                jasminString += jasminAccessModifier(method.getMethodAccessModifier()) + " " +
                    jasminIsStatic(method.isStaticMethod()) + method.getMethodName()+"(";
                for (Element element: method.getParams()) {
                    jasminString += convertElementType(element.getType());
                } 
                jasminString += ")" + convertElementType(method.getReturnType()) + "\n";
            }

            String toAddNext = instructionToJasmin(method);
            toAddNext += ".end method\n";

            if (!method.isConstructMethod()) {
                jasminString += "\t.limit stack " + getStack(toAddNext) + "\n";
                jasminString += "\t.limit locals " + String.valueOf(method.getVarTable().size() + 1) + "\n";
            }

            jasminString += toAddNext;
        }
        jasminString = incrementOptimization(jasminString);

        return jasminString;
    }

    public String incrementOptimization(String methodCode) {
        String[] code = methodCode.split("\n");
        List<String> newCode = new ArrayList<>();

        for (int i = 0; i < code.length; i++) {
            if (code[i].contains("iload") &&
                code[i+1].contains("iconst") &&
                code[i+2].contains("iadd") &&
                code[i+3].contains("istore") &&
                code[i].substring(7).equals(code[i+3].substring(8))) {
                String newInst = "\tiinc ";
                newInst += code[i].substring(7);
                newInst += " 1";
                newCode.add(newInst);

                i++;
                i++;
                i++;
            }
            else {
                newCode.add(code[i]);
            }
        }
        return String.join("\n", newCode);
    }

    private String ArrayTypeToJasmin(ArrayType type){
        switch (type.getTypeOfElements()){
            case STRING:
                return "Ljava/lang/String;";
            case INT32:
                return "I";
            case BOOLEAN:
                return "B";
            case CLASS:
                return classUnit.getClassName();
            default:    
                return "";
        }
    }

    public String convertElementType(Type type){
        switch (type.getTypeOfElement()){
            case INT32:
                return "I";
            case BOOLEAN:
                return "Z";
            case ARRAYREF:
                return "[" + ArrayTypeToJasmin((ArrayType) type);
            case OBJECTREF:
                return "OBJ";
            case CLASS:
                return "C";
            case THIS:
                return "T";
            case STRING:
                return "Ljava/lang/String;";
            case VOID:
                return "V";
            default:
                return "V"; 
        }
    }

    public String instructToJasmin(Instruction instruction, HashMap<String, Descriptor> varTable, HashMap<String, Instruction> methodLabels){
        String jasminString = "";
        for (Map.Entry<String, Instruction> entry: methodLabels.entrySet()) {
            if (entry.getValue().equals(instruction)) {
                jasminString += entry.getKey() + ":\n";
            }
        }

        if (instruction instanceof AssignInstruction) {
            AssignInstruction instruction2 = (AssignInstruction)instruction;

            Element e = ((AssignInstruction)instruction2).getDest();
            Descriptor var = varTable.get(((Operand)e).getName());
            if (var.getVarType().toString().equals("ARRAYREF") && e.getType().toString().equals("INT32")) {
                jasminString += "\taload " + var.getVirtualReg() + "\n";

                ArrayOperand arrayOp = (ArrayOperand) e;
                ArrayList<Element> indexOperands = arrayOp.getIndexOperands();
                for (Element elem: indexOperands) {
                    jasminString += loadElement(elem, varTable);
                }
                jasminString += instructToJasmin(instruction2.getRhs(), varTable, methodLabels);

                jasminString += "\tiastore\n";
            }
            else {
                jasminString += instructToJasmin(instruction2.getRhs(), varTable, methodLabels);

                Operand operand = (Operand) instruction2.getDest();
                jasminString += this.storeElement(operand, varTable);
            }

            return jasminString;
        }
        
        else if (instruction instanceof SingleOpInstruction) {
            SingleOpInstruction instruction2 = (SingleOpInstruction)instruction;
            Element e = ((SingleOpInstruction) instruction2).getSingleOperand();
            if (!e.isLiteral()) {
                Descriptor var = varTable.get(((Operand)e).getName());
                if(var.getVarType().toString().equals("ARRAYREF")){
                    jasminString += "\taload " + var.getVirtualReg() + "\n";

                    ArrayOperand arrayOp = (ArrayOperand) e;
                    ArrayList<Element> indexOperands = arrayOp.getIndexOperands();

                    for (Element elem: indexOperands) {
                        jasminString += loadElement(elem, varTable);
                    }

                    jasminString += "\tiaload\n";
                }
                else{
                    jasminString += loadElement(e, varTable);
                }
            }
            else {
                jasminString += loadElement(e, varTable);
            }
            return jasminString;
        }

        else if (instruction instanceof BinaryOpInstruction) {
            BinaryOpInstruction instruction2 = (BinaryOpInstruction)instruction;
            String left = loadElement(instruction2.getLeftOperand(), varTable);
            String right = loadElement(instruction2.getRightOperand(), varTable);
            OperationType operationType = instruction2.getUnaryOperation().getOpType();
            switch(operationType) {
                case ADD: return jasminString + left + right + "\tiadd\n";
                case SUB: return jasminString + left + right + "\tisub\n";
                case MUL: return jasminString + left + right + "\timul\n";
                case DIV: return jasminString + left + right + "\tidiv\n";
                case LTH: return jasminString + left + right + "\tisub\n";
                case GTH: return jasminString + left + right + "\tiadd\n";
                case EQ:  return jasminString + left + right + "\tieq\n";
                case NEQ: return jasminString + left + right + "\tine\n";
                case LTE: return jasminString + left + right + "\tisub\n";
                case GTE: return jasminString + left + right +"\tiadd\n";
                case ANDB: return jasminString + left + right + "\tiand\n";
                case NOTB: return jasminString + left;
                default:
                    return "default - operations: " + operationType + "\n";
            }
        }

        else if(instruction instanceof ReturnInstruction){
            ReturnInstruction instruction2 = (ReturnInstruction)instruction;

            if (!instruction2.hasReturnValue()) {
                jasminString += "\treturn\n";
                return jasminString;

            }
            switch(instruction2.getOperand().getType().getTypeOfElement()){
                case VOID:
                    jasminString += "\treturn\n";
                    return jasminString;

                case INT32: case BOOLEAN:
                    jasminString += loadElement(instruction2.getOperand(), varTable);
                    jasminString += "\tireturn\n";
                    return jasminString;

                case ARRAYREF: case OBJECTREF:
                    jasminString += loadElement(instruction2.getOperand(), varTable);
                    jasminString += "\tareturn\n";
                    return jasminString;
                default:
                    break;
            }
        }

        else if (instruction instanceof CallInstruction) {
            CallInstruction instruction2 = (CallInstruction) instruction;
            switch (instruction2.getInvocationType()) {
                case invokevirtual:
                case invokespecial:
                case invokestatic:
                    return jasminString + invokeToJasmin(instruction2, varTable);
                case NEW:
                    Operand op = (Operand)instruction2.getFirstArg();

                    if (op.getType().toString().equals("OBJECTREF")) {
                        jasminString += "\tnew " + op.getName() + "\n\tdup\n";
                    }
                    else { // ARRAYREF
                        ArrayList<Element> ar = instruction2.getListOfOperands();
                        if (ar.get(0).isLiteral()) {
                            jasminString += loadElement(ar.get(0), varTable);
                        }
                        else {
                            Descriptor var = varTable.get(((Operand)ar.get(0)).getName());
                            jasminString += "iload " + var.getVirtualReg() + "\n";
                        }

                        jasminString += "\tnewarray int\n";
                    }
                    return jasminString;
                case arraylength:
                    jasminString += loadElement(instruction2.getFirstArg(), varTable);
                    jasminString += "\tarraylength\n";
                    return jasminString;
                default:
                    return "not dealed call instruction";
            }
        }

        else if (instruction instanceof GotoInstruction) {
            GotoInstruction instruction2 = (GotoInstruction) instruction;
            String goToLabel = instruction2.getLabel();
            jasminString += "\tgoto " + goToLabel + "\n";
            return jasminString;
        }

        else if (instruction instanceof CondBranchInstruction){
            CondBranchInstruction instruction2 = (CondBranchInstruction) instruction;
            String left = loadElement(instruction2.getLeftOperand(), varTable);
            String right = loadElement(instruction2.getRightOperand(), varTable);
            String goToLabel = instruction2.getLabel();
            String ret = jasminString + left + right;

            Element e = instruction2.getRightOperand();

            switch(instruction2.getCondOperation().getOpType()){
                case LTH: ret += "\tif_icmplt "; break;
                case GTH: ret += "\tif_icmpgt "; break;
                case EQ:  ret += "\tif_icmpeq "; break;
                case NEQ: ret += "\tif_icmpne "; break;
                case LTE: ret += "\tif_icmple "; break;
                case GTE: ret += "\tif_icmpge "; break;
                case ANDB: ret += "\tiand\n\tifne "; break;
                case NOTB: ret = jasminString + left + "\tifeq "; break;
                default:  ret += "\tif_icmp_not_dealed:" + instruction2.getCondOperation().getOpType() + " "; break;
            }

            return ret + goToLabel + "\n";
        }

        else if (instruction instanceof PutFieldInstruction){
            PutFieldInstruction instruction2 = (PutFieldInstruction) instruction;
            Element first = instruction2.getFirstOperand();
            Element second = instruction2.getSecondOperand();
            Element third = instruction2.getThirdOperand();

            jasminString += loadElement(first, varTable);
            jasminString += loadElement(third, varTable);
            jasminString += "\tputfield " + classUnit.getClassName() + "/";
            jasminString += ((Operand)second).getName();
            jasminString += " " + convertElementType(second.getType()) + "\n";
            return jasminString;
        }

        else if(instruction instanceof GetFieldInstruction){
            GetFieldInstruction instruction2 = (GetFieldInstruction) instruction;
            Element first = instruction2.getFirstOperand();
            Element second = instruction2.getSecondOperand();

            jasminString += loadElement(first, varTable);
            jasminString += "\tgetfield " + classUnit.getClassName() + "/";
            jasminString += ((Operand)second).getName();
            jasminString += " " + convertElementType(second.getType()) + "\n";
            jasminString += storeElement((Operand)first, varTable);
            return jasminString;

        }

        else {
            return jasminString;
        }

        return jasminString;
    }

    public String instructionToJasmin(Method method) {
        HashMap<String, Descriptor> varTable = method.getVarTable();
        HashMap<String, Instruction> labels = method.getLabels();
        String jasminString = "";
        for(Instruction instruction : method.getInstructions()){
            jasminString += "" + instructToJasmin(instruction, varTable, labels);
        }
        return jasminString;
    }

    public String storeElement(Operand operand, HashMap<String, Descriptor> varTable){

        switch(operand.getType().getTypeOfElement()){
            case INT32:
            case BOOLEAN:
                return String.format("\tistore %s\n", varTable.get(operand.getName()).getVirtualReg());
            case ARRAYREF: case OBJECTREF:
                return String.format("\tastore %s\n", varTable.get(operand.getName()).getVirtualReg());
            case STRING:
                return String.format("\tastore %s\n", varTable.get(operand.getName()).getVirtualReg());
            default:
                return "";
        }
    }

    public String loadElement(Element element, HashMap<String, Descriptor> varTable){
        String jasminString = "";
        if (element instanceof LiteralElement) {
            String num = ((LiteralElement) element).getLiteral();
            int num_parsed = Integer.parseInt(num);
            if (num_parsed < 6){
                return "\ticonst_" + num + "\n";
            }
            else if (num_parsed < 128){
                return "\tbipush " + num + "\n";
            }
            else if (num_parsed < 32768){
                return "\tsipush " + num + "\n";
            }
            else {
                return "\tldc " + num + "\n";
            }
        }
        else if (element instanceof ArrayOperand) {
            ArrayOperand operand = (ArrayOperand) element;
            jasminString += String.format("\taload %s\n", varTable.get(operand.getName()).getVirtualReg());
            jasminString += loadElement(operand.getIndexOperands().get(0), varTable);
            return jasminString;
        }

        else if(element instanceof Operand){
            Operand operand = (Operand) element;
            switch (operand.getType().getTypeOfElement()) {
                case THIS:
                    return "\taload_0\n";
                case INT32:
                case BOOLEAN:
                    return String.format("\tiload %s\n", varTable.get(operand.getName()).getVirtualReg());
                case ARRAYREF:
                    return String.format("\taload %s\n", varTable.get(operand.getName()).getVirtualReg());
                case OBJECTREF:
                    return String.format("\taload %s\n", varTable.get(operand.getName()).getVirtualReg());
                //case CLASS:
                    //return String.format("\taload %s\n", varTable.get(operand.getName()).getVirtualReg());
                default:
                    break;
            }
        }
        return "";
    }

    public String invokeToJasmin(CallInstruction instruction, HashMap<String, Descriptor> varTable){
        String jasminString = "";

        Element first = instruction.getFirstArg();

        jasminString += loadElement(first, varTable);
        for (Element e: instruction.getListOfOperands()) {
            jasminString += loadElement(e, varTable);
        }

        if (first.toString().equals("this")) {
            Element second = instruction.getSecondArg();

            if (second.isLiteral()) {
                jasminString += ((LiteralElement) second).getLiteral().replace("\"", "");
            }
            else {
                Operand op = (Operand) second;
                jasminString += op.getName();
            }
        }
        else {
            jasminString += "\t" + instruction.getInvocationType() + " ";

            Element second = instruction.getSecondArg();
            if(second.isLiteral()) {
                if (this.method.isConstructMethod()) {
                    jasminString+="java/lang/Object" + "." + ((LiteralElement) second).getLiteral().replace("\"", "");
                }
                else {
                    if (first.getType().getTypeOfElement().equals(ElementType.CLASS)) {
                        jasminString+=((Operand) first).getName() + "." + ((LiteralElement) second).getLiteral().replace("\"", "");
                    }
                    else { // OBJECTREF
                        jasminString+= classUnit.getClassName() + "." + ((LiteralElement) second).getLiteral().replace("\"", "");
                    }
                }
            }
            else{
                Operand op = (Operand) second;
                jasminString += op.getName();
            }
        }


        jasminString+="(";
        for (Element e: instruction.getListOfOperands()) {
            jasminString += convertElementType(e.getType());
        }
        jasminString+=")";
        jasminString += convertElementType(instruction.getReturnType());
        
        return jasminString + "\n";
    }

    private String fieldsToJasmin(){
        String jasminString = "";
        for(Field field : classUnit.getFields())
        {
            jasminString += ".field " + jasminIsStatic(field.isStaticField()) + field.getFieldAccessModifier().toString().toLowerCase() + " ";
            if (field.getFieldName().equals("field")) {
                jasminString += "'field'";
            }
            else {
                jasminString += field.getFieldName();
            }
            jasminString += " " + convertElementType(field.getFieldType())+"\n";
        }
        return jasminString;
    }

    public int getStack(String methodCode) {
        String[] methodCodeSplit = methodCode.split("\n");

        int limit = 0, maxLimit = 0;

        for (String s: methodCodeSplit) {
            if (s.contains("const") || s.contains("bipush") || s.contains("sipush")
                    || s.contains("ldc") || s.contains("getfield") || s.contains("dup")) {
                limit += 1;
                maxLimit = Math.max(maxLimit, limit);
            }
            else if (s.contains("putfield") || s.contains("add") || s.contains("div") ||
                    s.contains("sub") || s.contains("mul")) {
                limit -=1;
                maxLimit = Math.max(maxLimit, limit);
            }
            else if (s.contains("load")) {
                if (s.contains("iaload")) limit -= 1;
                else limit += 1;
                maxLimit = Math.max(maxLimit, limit);
            }
            else if(s.contains("store")) {
                if (s.contains("iastore")) limit -= 3;
                else limit -= 1;
                maxLimit = Math.max(maxLimit, limit);
            }
            else if (s.contains("invoke")) {
                char returnType = s.charAt(s.indexOf(")")+1);
                if (returnType!='V') limit += 1;

                String arguments = s.substring(s.indexOf("(")+1,s.indexOf(")"));

                int nStrings=countSubstring(arguments,"Ljava/lang/String;");
                String restOfArgs=arguments.substring(nStrings*"Ljava/lang/String;".length());
                limit-=nStrings+restOfArgs.length();

                maxLimit = Math.max(maxLimit, limit);
            }
            else if(s.contains("if_") || s.contains("iflt") || s.contains("ifgt") || s.contains("ifeq") ||
            s.contains("ifne") || s.contains("ifle") || s.contains("ifge")){
                limit -=2;
                maxLimit = Math.max(maxLimit, limit);
            }
        }
        return maxLimit;
    }

    public int countSubstring(String str, String findStr){
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {

            lastIndex = str.indexOf(findStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}



