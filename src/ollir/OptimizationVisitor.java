import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import pt.up.fe.comp.jmm.analysis.table.Symbol;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;


class OptimizationVisitor extends AJmmVisitor<String, String> {

    private MySymbolTable symbolTable;
    private Map<String, String> types = new HashMap<>();
    private String className = "";
    private String actualMethodName = "";
    private String actualMethodType = "";
    private List<String> parameters = new ArrayList<String>();
    private Stack stack = new Stack();
    private List<String> imports = new ArrayList<String>();
    private String assignmentOllir = "";
    private Integer auxNumber = 1;
    private Integer ifNumber = 1;
    private Integer whileNumber = 1;
    List<String> booleanArray = Arrays.asList("True", "False", "Less", "And", "Or", "Not");
    List<String> intArray = Arrays.asList("IntegerLiteral", "DotLength", "ArrayAccess");
    List<String> varArray = Arrays.asList("Var", "MethodCall", "RestIdentifier");
    List<String> newInstanceArray = Arrays.asList("NewInstance");
    List<String> typeReturner = Arrays.asList("Var", "True", "False", "Less", "And", "Or", "Not", "IntegerLiteral", "DotLength", "ArrayAccess", "MethodCall", "RestIdentifier", "NewInstance");
    List<String> nodeTypes = Arrays.asList("int", "boolean", "int[]");

    public OptimizationVisitor(MySymbolTable symbolTable) {
        fillTypesMap();
        this.symbolTable = symbolTable;

        addVisit("ClassDeclaration", this::handleClassDeclaration);
        addVisit("ImportDeclaration", this::handleImportDeclaration);
        addVisit("MethodDeclaration", this::handleMethodDeclaration);
        addVisit("MainDeclaration", this::handleMainDeclaration);
        addVisit("Assignment", this::handleAssignment);
        addVisit("MethodCall", this::handleMethodCall);
        addVisit("ReturnExpression", this::handleReturnExpression);
        addVisit("ifStatement", this::handleIfStatement);
        addVisit("whileStatement", this::handleWhileStatement);

        setDefaultVisit(this::defaultVisit);
    }

    public String handleClassDeclaration(JmmNode node, String ollirCode) {
        this.className = firstChildName(node);
        types.put(firstChildName(node), firstChildName(node));
        String ret = "";
        String type = new String();
        if (node.getChildren().get(1).getKind().equals("Extends"))
            ret += this.className + " extends " + node.getChildren().get(1).get("extendedClass") + " {\n";
        else
            ret += this.className + " {\n";
        for (int i = 0; i < node.getNumChildren(); i++)
        {
            if (node.getChildren().get(i).getKind().equals("VarDeclaration"))
            {
                type = getTypeToOllir(node.getChildren().get(i).getChildren().get(0).get("name"));
                ret += ".field private " + node.getChildren().get(i).get("name") + "." + type + ";\n";
            }
        }
        ret += "\t.construct " + this.className + "().V {\n";
        ret += "\t\tinvokespecial(this, \"<init>\").V;\n";
        ret += "\t}\n\n";
        return ret + defaultVisit(node, ollirCode) + "}";
    }

    public String handleImportDeclaration(JmmNode node, String ollirCode) {
        this.imports.add(node.get("name"));
        return "";
    }

    public String handleMethodDeclaration(JmmNode node, String ollirCode) {
        this.actualMethodName = node.get("name");
        this.actualMethodType = firstChildName(node);
        this.parameters = new ArrayList<String>();

        if (this.actualMethodType.equals("void")) {
            JmmNode nodeR = new JmmNodeImpl("ReturnExpression");
            node.add(nodeR);
        }

        String init = "\t.method public " + this.actualMethodName;
        String methodParameters = "(" + methodParameters(node) + ")";
        String methodType = "." + getMethodType(node) + " {\n";

        return init + methodParameters + methodType + defaultVisit(node, ollirCode) + "\t}\n\n";
    }

    public String handleMainDeclaration(JmmNode node, String ollirCode) {
        this.actualMethodName = "MainDeclaration";

        String init = "\t.method public static main";
        String args = "(" + firstChildName(node) + ".array.String).V {\n";
        this.parameters = new ArrayList<String>();
        JmmNode nodeR = new JmmNodeImpl("ReturnExpression");
        node.add(nodeR);

        return init + args + defaultVisit(node, ollirCode) + "\t}\n\n";
    }

    public String handleMethodCall(JmmNode node, String ollirCode) {
        String toReturn = new String();
        if (!node.getAncestor("ReturnExpression").isPresent() && !node.getAncestor("Assignment").isPresent() && !node.getAncestor("ifStatement").isPresent() && !node.getAncestor("whileStatement").isPresent() && !node.getAncestor("MethodCall").isPresent())
        {
            toReturn = soloMethodCallNode(node, ollirCode);
        }

        return toReturn;
    }

    public String soloMethodCallNode(JmmNode node, String ollirCode) {
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "\t\t";
        switch (node.getChildren().get(0).getKind()) {
            case "This":
                toReturn = toReturn + "invokevirtual(this, " + "\"" + node.get("name") + "\"";
                break;
            case "RestIdentifier":
                if (varInImports(node.getChildren().get(0))) 
                {
                    toReturn = toReturn + "invokestatic(" + node.getChildren().get(0).get("name") + ", \"" + node.get("name") + "\"";
                }
                else
                {
                    toReturn = toReturn + "invokevirtual(" + node.getChildren().get(0).get("name") + "." + getClassName(node) + ", \"" + node.get("name") + "\"";
                }
                break;
            case "NewInstance":
                toReturn = toReturn + "invokevirtual(" + newInstanceNode(node.getChildren().get(0)) + ", \"" + node.get("name") + "\"";
                break;
            case "Exp":
                toReturn = toReturn + expNode(node.getChildren().get(0), true);
                break;
            default:
                System.out.println("Unexpected behaviour: soloMethodCallNode1");
                break;
        }

        for (int i = 0; i < node.getChildren().get(1).getChildren().size(); i++)
        {
            switch (node.getChildren().get(1).getChildren().get(i).getKind()) {
                case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                    toReturn = toReturn + ", " + terminalNode(node.getChildren().get(1).getChildren().get(i));
                    break;
                case "NewInstance":
                    toReturn = toReturn + ", " + newInstanceNode(node.getChildren().get(1).getChildren().get(i));
                    break;
                case "Operation":
                    toReturn = toReturn + ", " + operationNode(node.getChildren().get(1).getChildren().get(i), true);  ////////////////////
                    break;
                case "MethodCall":
                    toReturn = toReturn + ", " + methodCallNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "Exp":
                    toReturn = toReturn + ", " + expNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "DotLength":
                    toReturn = toReturn + ", " + dotLengthNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "ArrayAccess":
                    toReturn = toReturn + ", " + arrayNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                default:
                    System.out.println("Unexpected behaviour: soloMethodCallNode2");
                    break;
            }
        }
        toReturn = toReturn + ")." + type + ";\n";
        toReturn = this.assignmentOllir + toReturn;
        this.assignmentOllir = "";
    
        return toReturn + defaultVisit(node, ollirCode);
    }

    public String handleReturnExpression(JmmNode node, String ollirCode) {
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String line = "\t\tret." + type + " ";
        String varName;
        int varParam = -1;
        boolean needAux = true;
        String putFieldAdd = "";

        if (node.getChildren().isEmpty()) {
            return "\t\tret.V;\n";
        }
        
        switch (node.getChildren().get(0).getKind()) {
            case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                needAux = false;
                line = line + terminalNode(node.getChildren().get(0)) + putFieldAdd + ";";
                break;
            case "NewInstance":
                needAux = false;
                line = line + newInstanceNode(node.getChildren().get(0)) + putFieldAdd + ";";
                break;
            case "Operation": case "And": case "Less":
                line = line + operationNode(node.getChildren().get(0), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(0), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(0), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "Not":
                line = line + notNode(node.getChildren().get(0), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "DotLength":
                line = line + dotLengthNode(node.getChildren().get(0), needAux) + putFieldAdd;
                break;
            case "ArrayAccess":
                line = line + arrayNode(node.getChildren().get(0), needAux) + putFieldAdd;
                break;
            case "NewArray":
                line = line + newArrayNode(node.getChildren().get(0), needAux) + putFieldAdd;
                break;
            default:
                System.out.println("Unexpected behaviour: handleReturn");
                break;
        }
        if (needAux)
        {
            line = line + ";";
        }
        this.assignmentOllir += line + defaultVisit(node, ollirCode) + "\n";
        String toReturn = this.assignmentOllir;
        this.assignmentOllir = "";
        return toReturn;
    }

    /*-------------------------------------------------------------------------------------------------*/

   

    public String handleWhileStatement(JmmNode node, String ollirCode){
        
        String toReturn = new String();
        if (!node.getAncestor("whileStatement").isPresent() && !node.getAncestor("ifStatement").isPresent())
        {
            toReturn = whileNode(node, ollirCode);
        }

        return toReturn;
    }

    public String whileNode(JmmNode node, String ollirCode) {
        String scope = getScope(node.getChildren().get(0));
        String type = getTypeToOllir(getTypeReturnedByNode(node.getChildren().get(0), scope));
        Integer currentWhileNumber = this.whileNumber;
        String line = "\t\tLoop" + currentWhileNumber + ":\n";
        this.whileNumber++;
        String binaryExpression = new String();

        switch (node.getChildren().get(0).getKind()){
            case "And": case "Less":
                binaryExpression = removeLastTwoChar(operationNode(node.getChildren().get(0), false));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + ") goto Body" + currentWhileNumber + ";\n";
                break;
            case "Not":
                binaryExpression = removeLastTwoChar(notNode(node.getChildren().get(0), false));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + ") goto Body" + currentWhileNumber + ";\n";
                break;
            case "MethodCall":
                binaryExpression = removeLastTwoChar(methodCallNode(node.getChildren().get(0), true));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto Body" + currentWhileNumber + ";\n";
                break;
            case "True":
                binaryExpression = "1.bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto Body" + currentWhileNumber + ";\n";
                break;
            case "False":
                binaryExpression = "0.bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto Body" + currentWhileNumber + ";\n";
                break;
            case "RestIdentifier":
                binaryExpression = node.getChildren().get(0).get("name") + ".bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto Body" + currentWhileNumber + ";\n";
                break;
            default:
                System.out.println("Unexpected behaviour: whileNode1");
                break;
        }

        line = line + "goto EndLoop" + currentWhileNumber + ";\n";
        line = line + "Body" + currentWhileNumber + ":\n";

        for (int i = 1; i < node.getNumChildren(); i++)
        {
            switch (node.getChildren().get(i).getKind()) {
                case "Assignment":
                    line = line + assignmentNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "MethodCall":
                    line = line + soloMethodCallNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "ifStatement":
                    line = line + ifNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "whileStatement":
                    line = line + whileNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                default:
                    System.out.println("Unexpected behaviour: whileNode2");
                    break;
            }
        }

        line = line + "goto Loop" + currentWhileNumber + ";\n";
        line = line + "EndLoop" + currentWhileNumber + ":\n";

        this.assignmentOllir = "";
        return line + defaultVisit(node, ollirCode) + "\n";
    }

    public String handleIfStatement(JmmNode node, String ollirCode){
        String toReturn = new String();
        if (!node.getAncestor("whileStatement").isPresent() && !node.getAncestor("ifStatement").isPresent())
        {
            toReturn = ifNode(node, ollirCode);
        }
        return toReturn;
    }
    
    public String ifNode(JmmNode node, String ollirCode) {
        String scope = getScope(node.getChildren().get(0));
        String type = getTypeToOllir(getTypeReturnedByNode(node.getChildren().get(0), scope));
        String line = "\t\t";
        Integer currentIfNumber = this.ifNumber;
        this.ifNumber++;

        String binaryExpression = new String();

        switch (node.getChildren().get(0).getKind()){
            case "And": case "Less":
                binaryExpression = removeLastTwoChar(operationNode(node.getChildren().get(0), false));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + ") goto ifBlock" + currentIfNumber + ";\n";
                break;
            case "Not":
                binaryExpression = removeLastTwoChar(notNode(node.getChildren().get(0), false));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + ") goto ifBlock" + currentIfNumber + ";\n";
                break;
            case "MethodCall":
                binaryExpression = removeLastTwoChar(methodCallNode(node.getChildren().get(0), true));
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto ifBlock" + currentIfNumber + ";\n";
                break;
            case "True":
                binaryExpression = "1.bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto ifBlock" + currentIfNumber + ";\n";
                break;
            case "False":
                binaryExpression = "0.bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto ifBlock" + currentIfNumber + ";\n";
                break;
            case "RestIdentifier":
                binaryExpression = node.getChildren().get(0).get("name") + ".bool";
                line = line + this.assignmentOllir;
                this.assignmentOllir = "";
                line = line + "if (" + binaryExpression + " ==.bool 1.bool) goto ifBlock" + currentIfNumber + ";\n";
                break;
            default:
                System.out.println("Unexpected behaviour: ifNode1");
                break;
        }

        line = line + elseNode(node.getChildren().get(node.getNumChildren() - 1), ollirCode);
        line = line + "goto endif" + currentIfNumber + ";\n";
        line = line + "ifBlock" + currentIfNumber + ":\n";

        for (int i = 1; i < node.getNumChildren()-1; i++)
        {
            switch (node.getChildren().get(i).getKind()) {
                case "Assignment":
                    line = line + assignmentNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "MethodCall":
                    line = line + soloMethodCallNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "ifStatement":
                    line = line + ifNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "whileStatement":
                    line = line + whileNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                default:
                    System.out.println("Unexpected behaviour: ifNode2");
                    break;
            }
        }
        line = line + "endif" + currentIfNumber + ":\n";

        this.assignmentOllir = "";
        return line + defaultVisit(node, ollirCode) + "\n";
    }

    private String elseNode(JmmNode node, String ollirCode) {
        
        String toReturn = new String();
        for (int i = 0; i < node.getNumChildren(); i++)
        {
            switch (node.getChildren().get(i).getKind()) {
                case "Assignment":
                    toReturn = toReturn + assignmentNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "MethodCall":
                    toReturn = toReturn + handleMethodCall(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                case "ifStatement":
                    toReturn = toReturn + ifNode(node.getChildren().get(i), ollirCode) + "\n";
                    break;
                default:
                    System.out.println("Unexpected behaviour: elseNode");
                    break;
            }
        }
        return toReturn;
    }

    public String removeLastTwoChar(String temp){
        if (temp.endsWith(";\n"))
            return temp.substring(0, temp.length()-2);
        return temp;
    }

    public String getType(JmmNode node, String scope){
        String tempType = "";
        String nodeName;
        if (node.getKind().equals("ArrayAccess"))
        {
            nodeName = node.getChildren().get(0).get("name");
        }
        else
        {
            nodeName = node.get("name");
        }

        if (booleanArray.contains(node.getKind()))
        {
            return "boolean";
        }
        else if (intArray.contains(node.getKind()))
        {
            return "int";
        }
        else if (newInstanceArray.contains(node.getKind()))
        {
            return node.get("name");
        }

        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            MySymbol symbol = entry.getValue();

            if (symbol.getName().equals(nodeName) && (symbol.getScope().equals("GLOBAL") || symbol.getScope().equals(scope))) {
                if (symbol.getScope().equals("GLOBAL"))
                {
                    tempType = symbol.getType().getName();
                }
                else if(symbol.getScope().equals(scope))
                {
                    return symbol.getType().getName();
                }
                
            }
        }
        return tempType;
    }

    /**
     * 
     * @param node Node to evaluate scope of 
     * @return Returns the scope of the node given
     */
    public String getScope(JmmNode node){
        String scope = "";
        if (node.getAncestor("MethodDeclaration").isPresent()) {
            scope = node.getAncestor("MethodDeclaration").get().get("name");
        } else if (node.getAncestor("MainDeclaration").isPresent()) {
            scope = "MainDeclaration";
        } else if (node.getAncestor("ClassDeclaration").isPresent()) {
            scope = "GLOBAL";
        }
        return scope;
    }

    public Boolean inScope(JmmNode node, String scope) {
        if (node.getKind().equals("RestIdentifier") || node.getKind().equals("Var"))
        {
            for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
                MySymbol symbol = entry.getValue();

                if (symbol.getName().equals(node.get("name")) && symbol.getScope().equals("GLOBAL") && symbol.getSuperName().equals("VarDeclaration")) {
                    return true;
                }
            }
        } 
        return false;
    }

    public String getTypeReturnedByNode(JmmNode node, String scope)
    {
        
        String currentType = "";
        if (typeReturner.contains(node.getKind()))
        {
            if (booleanArray.contains(node.getKind()))
            {
                return "boolean";
            }
            else if (intArray.contains(node.getKind()))
            {
                return "int";
            }
            else if (varArray.contains(node.getKind()))
            {
                return getType(node, scope);
            }
            else if (newInstanceArray.contains(node.getKind()))
            {
                return node.get("name");
            }
        }
        for (int i = 0; i < node.getChildren().size(); i++)
        {
            JmmNode current = node.getChildren().get(i);
            String typeReturned = getTypeReturnedByNode(current, scope);
            if (currentType.equals(""))
            {
                currentType = typeReturned;
            }
            else if (!typeReturned.equals(currentType))
            {
                return "NULL";
            }
        }
        return currentType;
    }

    public String handleAssignment(JmmNode node, String ollirCode) { //TODO: tratar types
        if (!node.getAncestor("ifStatement").isPresent() && !node.getAncestor("whileStatement").isPresent()){
            return assignmentNode(node, ollirCode);
        }
        return "";
    }

    public String assignmentNode(JmmNode node, String ollirCode) { //TODO: tratar types
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node.getChildren().get(0), scope));
        String line = "\t\t";
        String varName;
        int varParam = -1;
        boolean needAux = false;
        String putFieldAdd = "";
        switch (node.getChildren().get(0).getKind()) {
            case "Var":
                varName = node.getChildren().get(0).get("name");
                varParam = checkVarIsArg(varName);
                if (varTypeST(varName, this.actualMethodName).equals("int[]")) {
                    type = "array.i32";
                }
                if (inScope(node.getChildren().get(0), "GLOBAL"))
                {
                    line = line + "putfield(this, " + node.getChildren().get(0).get("name") + "." + type + ", ";
                    needAux = true;
                    putFieldAdd = ").V";
                }
                else {
                    if (varParam != 0) {
                        line = line + "$" + varParam + "." + node.getChildren().get(0).get("name") + "." + type + " :=." + type + " "; 
                    }
                    else {
                        line = line + node.getChildren().get(0).get("name") + "." + type + " :=." + type + " "; 
                    }
                }
                break;
            case "ArrayAccess":
                varName = node.getChildren().get(0).getChildren().get(0).get("name");
                varParam = checkVarIsArg(varName);

                if (inScope(node.getChildren().get(0).getChildren().get(0), "GLOBAL"))
                {
                    line = line + getFieldArrayAccessNode(node.getChildren().get(0).getChildren().get(0)) + "[";
                }
                else {
                    if (varParam != 0) {
                        line = line + "$" + varParam + "." + varName + "["; 
                    }
                    else {
                        line = line + varName + "[";
                    }
                }

                switch (node.getChildren().get(0).getChildren().get(1).getKind()) {
                    case "RestIdentifier":
                        line = line + terminalNode(node.getChildren().get(0).getChildren().get(1)) + "].i32 :=.i32 ";
                        break;
                    case "NewInstance":
                        line = line + newInstanceNode(node.getChildren().get(0).getChildren().get(1)) + "].i32 :=.i32 ";
                        break;
                    case "IntegerLiteral": // TODO: solve this
                        line = line + terminalArrayAccessNode(node.getChildren().get(0).getChildren().get(1)) + "].i32 :=.i32 ";
                        break;
                    case "Operation":
                        line = line + operationNode(node.getChildren().get(0).getChildren().get(1), true) + "].i32 :=.i32 ";
                        break;
                    case "MethodCall":
                        line = line + methodCallNode(node.getChildren().get(0).getChildren().get(1), true) + "].i32 :=.i32 ";
                        break;
                    case "Exp":
                        line = line + expNode(node.getChildren().get(0).getChildren().get(1), true) + "].i32 :=.i32 ";
                        break;
                    case "DotLength":
                        line = line + dotLengthNode(node.getChildren().get(0).getChildren().get(1), true) + "].i32 :=.i32 ";
                        break;
                    case "ArrayAccess":
                        line = line + arrayNode(node.getChildren().get(0).getChildren().get(1), true) + "].i32 :=.i32 ";
                        break;
                    default:
                        System.out.println("Unexpected behaviour: handleAssignment2");
                        break;
                }

                break;
            default:
                System.out.println("Unexpected behaviour: handleAssignment1");
                break;
        }
        
        switch (node.getChildren().get(1).getKind()) {
            case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                needAux = false;
                line = line + terminalNode(node.getChildren().get(1)) + putFieldAdd + ";";
                break;
            case "NewInstance":
                needAux = false;
                line = line + newInstanceNode(node.getChildren().get(1)) + putFieldAdd + ";";
                break;
            case "Operation": case "And": case "Less":
                line = line + operationNode(node.getChildren().get(1), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(1), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(1), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "Not":
                line = line + notNode(node.getChildren().get(1), needAux) + putFieldAdd/* + ";"*/;
                break;
            case "DotLength":
                line = line + dotLengthNode(node.getChildren().get(1), needAux) + putFieldAdd;
                break;
            case "ArrayAccess":
                line = line + arrayNode(node.getChildren().get(1), needAux) + putFieldAdd;
                break;
            case "NewArray":
                line = line + newArrayNode(node.getChildren().get(1), needAux) + putFieldAdd;
                break;
            default:
                System.out.println("Unexpected behaviour: handleAssignment2");
                break;
        }
        if (needAux)
        {
            line = line + ";";
        }
        this.assignmentOllir += line + defaultVisit(node, ollirCode) + "\n";
        String toReturn = this.assignmentOllir;
        this.assignmentOllir = "";
        return toReturn;
    }
    
    

    private String getFieldArrayAccessNode(JmmNode node) {
        String scope = getScope(node);
        String type = "array.i32";
        String toReturn = "aux" + this.auxNumber;
        this.auxNumber++;
        String line = "";
        line = "\t\t" + toReturn + "." + type + " :=." + type + " getfield(this, " + node.get("name") + "." + type + ")." + type + ";\n";

        this.assignmentOllir += line;
        return toReturn;
    }

    private String arrayNode(JmmNode node, boolean needAux) {

        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }

        switch (node.getChildren().get(0).getKind()) {
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(0), true) + "[";
                break;
            case "Var": case "RestIdentifier":
                line = line + node.getChildren().get(0).get("name") + "[";
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(0), true) + "[";
                break;
            default:
                System.out.println("Unexpected behaviour: arrayNode1");
                break;
        }

        switch (node.getChildren().get(1).getKind()) {
            case "IntegerLiteral": case "RestIdentifier":
                line = line + terminalArrayAccessNode(node.getChildren().get(1)) + "]." + type + ";\n";
                break;
            case "NewInstance":
                line = line + newInstanceNode(node.getChildren().get(1)) + "]." + type + ";\n";
                break;
            case "Operation":
                line = line + operationNode(node.getChildren().get(1), true) + "]." + type + ";\n";
                break;
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(1), true) + "]." + type + ";\n";
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(1), true) + "]." + type + ";\n";
                break;
            case "DotLength":
                line = line + dotLengthNode(node.getChildren().get(1), true) + "]." + type + ";\n";
                break;
            case "ArrayAccess":
                line = line + arrayNode(node.getChildren().get(1), true) + "]." + type + ";\n";
                break;
            default:
                System.out.println("Unexpected behaviour: arrayNode2");
                break;
        }

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String newArrayNode(JmmNode node, boolean needAux) {

        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + "array.i32";
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + "array.i32" + " :=." + "array.i32" + " ";
            this.auxNumber++;
        }

        switch (node.getChildren().get(0).getKind()) {
            case "IntegerLiteral": case "Var": case "RestIdentifier":
                line = line + "new(array, " + node.getChildren().get(0).get("name") + "." + type + ").array.i32;\n";
                break;
            case "DotLength":
                line = line + "new(array, " + dotLengthNode(node.getChildren().get(0), true) + ").array.i32;\n";
                break;
            case "MethodCall":
                line = line + "new(array, " + methodCallNode(node.getChildren().get(0), true) + ").array.i32;\n";
                break;
            case "Operation":
                line = line + "new(array, " + operationNode(node.getChildren().get(0), true) + ").array.i32;\n";
                break;
            case "Exp":
                line = line + "new(array, " + expNode(node.getChildren().get(0), true) + ").array.i32;\n";
                break;
            case "ArrayAccess":
                line = line + "new(array, " + arrayNode(node.getChildren().get(0), true) + ").array.i32;\n";
                break;
            default:
                System.out.println("Unexpected behaviour: newArrayNode1");
                break;
        }

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String dotLengthNode(JmmNode node, boolean needAux) {

        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }

        switch (node.getChildren().get(0).getKind()) {
            case "RestIdentifier": 
                line = line + "arraylength(" + terminalNode(node.getChildren().get(0)) + ").i32;\n";
                break;
            case "MethodCall":
                line = line + "arraylength(" + methodCallNode(node.getChildren().get(0), true) + ").i32;\n";
                break;
            case "Exp":
                line = line + "arraylength(" + expNode(node.getChildren().get(0), true) + ").i32;\n";
                break;
            default:
                System.out.println("Unexpected behaviour: dotLengthNode");
                break;
        }

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String notNode(JmmNode node, boolean needAux) {
        String operation;
        switch (node.getKind()) {
            case "And":
                operation = "&&";
                break;
            case "Less":
                operation = "<";
                break;
            default:
                operation = node.get("name");
                break;
        }

        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }
        String var = new String();
        switch (node.getChildren().get(0).getKind()) {
            case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                var = terminalNode(node.getChildren().get(0));
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            case "Operation": case "And":
                var = operationNode(node.getChildren().get(0), true);
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            case "MethodCall":
                var = methodCallNode(node.getChildren().get(0), true);
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            case "Exp":
                var = expNode(node.getChildren().get(0), true);
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            case "Not":
                var = notNode(node.getChildren().get(0), true);
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            case "DotLength":
                var = dotLengthNode(node.getChildren().get(0), true);
                line = line + var + " " + operation + "." + type + " " + var + ";\n";
                break;
            default:
                System.out.println("Unexpected behaviour: notNode1");
                break;
        }

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String expNode(JmmNode node, boolean needAux) {
        String toReturn = "";
        
        switch (node.getChildren().get(0).getKind()) {
            case "Exp":
                toReturn = expNode(node.getChildren().get(0), needAux);
                break;
            case "Operation": case "And": case "Less":
                toReturn = operationNode(node.getChildren().get(0), needAux); //////////////////////////////
                break;
            case "MethodCall":
                toReturn = methodCallNode(node.getChildren().get(0), needAux); /////////////////////////////
                break;
            case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                toReturn = terminalNode(node.getChildren().get(0));
                break;
            case "Not":
                toReturn = notNode(node.getChildren().get(0), needAux);
                break;
            case "DotLength":
                toReturn = dotLengthNode(node.getChildren().get(0), needAux);
                break;
            case "ArrayAccess":
                toReturn = arrayNode(node.getChildren().get(0), needAux);
                break;
            default:
                System.out.println("Unexpected behaviour: expNode1");
                break;
        }
        
        
        return toReturn;
    }

    private String methodCallNode(JmmNode node, boolean needAux) {
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }
        
        switch (node.getChildren().get(0).getKind()) {
            case "This":
                line = line + "invokevirtual(this, " + "\"" + node.get("name") + "\"";
                break;
            case "RestIdentifier":
                if (varInImports(node.getChildren().get(0))) 
                {
                    line = line + "invokestatic(" + node.getChildren().get(0).get("name") + ", \"" + node.get("name") + "\"";
                }
                else
                {
                    line = line + "invokevirtual(" + node.getChildren().get(0).get("name") + "." + getClassName(node) + ", \"" + node.get("name") + "\"";
                }
                break;
            case "NewInstance":
                if (varInImports(node.getChildren().get(0))) 
                {
                    line = line + "invokestatic(" + newInstanceNode(node.getChildren().get(0)) + ", \"" + node.get("name") + "\"";
                }
                else
                {
                    line = line + "invokevirtual(" + newInstanceNode(node.getChildren().get(0)) + ", \"" + node.get("name") + "\"";
                }
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(0), true);
                break;
            default:
                System.out.println("Unexpected behaviour: methodCallNode1");
                break;
        }

        for (int i = 0; i < node.getChildren().get(1).getChildren().size(); i++)
        {
            switch (node.getChildren().get(1).getChildren().get(i).getKind()) {
                case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                    line = line + ", " + terminalNode(node.getChildren().get(1).getChildren().get(i));
                    break;
                case "Operation":
                    line = line + ", " + operationNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "MethodCall":
                    line = line + ", " + methodCallNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "Exp":
                    line = line + ", " + expNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "DotLength":
                    line = line + ", " + dotLengthNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                case "ArrayAccess":
                    line = line + ", " + arrayNode(node.getChildren().get(1).getChildren().get(i), true);
                    break;
                default:
                    System.out.println("Unexpected behaviour: methodCallNode2");
                    break;
            }
        }

        line = line + ")." + type + ";\n";
        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String operationNode(JmmNode node, boolean needAux) { //TODO: types
        String operation;
        if (node.getKind().equals("And")) {
            operation = "&&";
        }
        else if (node.getKind().equals("Less")) {
            operation = "<";
        }
        else {
            operation = node.get("name");
        }
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }

        switch (node.getChildren().get(0).getKind()) {
            case "IntegerLiteral": case "RestIdentifier": 
                line = line + terminalNode(node.getChildren().get(0)) + " " + operation + "." + "i32" + " ";
                break;
            case "Operation": case "Less": case "And":
                line = line + operationNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            case "True": case "False":
                line = line + terminalNode(node.getChildren().get(0)) + " " + operation + ".bool ";
                break;
            case "Not":
                line = line + notNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            case "DotLength":
                line = line + dotLengthNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            case "ArrayAccess":
                line = line + arrayNode(node.getChildren().get(0), true) + " " + operation + "." + type + " ";
                break;
            default:
                System.out.println("Unexpected behaviour: operationNode1: " + node.getChildren().get(0).getKind());
                break;
        }

        switch (node.getChildren().get(1).getKind()) {
            case "IntegerLiteral": case "RestIdentifier": case "True": case "False":
                line = line + terminalNode(node.getChildren().get(1)) + ";\n";
                break;
            case "Operation": case "Less":
                line = line + operationNode(node.getChildren().get(1), true) + ";\n";
                break;
            case "MethodCall":
                line = line + methodCallNode(node.getChildren().get(1), true) + ";\n";
                break;
            case "Exp":
                line = line + expNode(node.getChildren().get(1), true) + ";\n";
                break;
            case "Not":
                line = line + notNode(node.getChildren().get(1), true) + ";\n";
                break;
            case "DotLength":
                line = line + dotLengthNode(node.getChildren().get(1), true) + ";\n";
                break;
            case "ArrayAccess":
                line = line + arrayNode(node.getChildren().get(1), true) + ";\n";
                break;
            default:
                System.out.println("Unexpected behaviour: operationNode2");
                break;
        }

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    private String newInstanceNode(JmmNode node, boolean needAux) { //TODO: types
        String operation = node.get("name");
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String toReturn = "aux" + this.auxNumber + "." + type;
        String line = "";
        if (needAux){
            line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
            this.auxNumber++;
        }

        line = line + "ABC" + " " + operation + "." + type + " ";

        if (needAux){
            this.assignmentOllir += line;
            return toReturn;
        }
        else{
            return line;
        }
    }

    public String terminalNode(JmmNode node) //TODO: tratar types
    {
        String scope = getScope(node);
        String toReturn = new String();
        int varParam;
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        switch (node.getKind()) {
            case "IntegerLiteral":
                toReturn = node.get("name") + "." + type;
                varParam = checkVarIsArg(node.get("name"));
                if (varParam != 0) {
                    toReturn = "$" + varParam + "." + toReturn;
                }
                break;
            case "RestIdentifier":
                if (inScope(node, "GLOBAL"))
                {
                    toReturn = "aux" + this.auxNumber + "." + type;
                    this.auxNumber++;
                    this.assignmentOllir += toReturn + " :=." + type + " getfield(this, " + node.get("name") + "." + type + ")." +  type + ";\n";
                }
                else{
                    toReturn = node.get("name") + "." + type;
                    varParam = checkVarIsArg(node.get("name"));
                    if (varParam != 0) {
                        toReturn = "$" + varParam + "." + toReturn;
                    }
                }
                break;
            case "True":
                toReturn = "1.bool";
                break;
            case "False":
                toReturn = "0.bool";
                break;
            case "NewInstance":
                toReturn = "new(" + node.get("name") + ")." + node.get("name");
                break;
            default:
                System.out.println("Unexpected behaviour: terminalNode");
                break;
        }
        return toReturn;
    }

    public String newInstanceNode(JmmNode node)
    {
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
        String toReturn = "aux" + this.auxNumber + "." + type;
        this.auxNumber++;
        line = line + "new(" + node.get("name") + ")." + node.get("name");
        this.assignmentOllir += line + ";\n";
        this.assignmentOllir += "invokespecial(" + toReturn + ", \"<init>\").V;\n";
        return toReturn;
    }

    public String terminalArrayAccessNode(JmmNode node) //TODO: tratar types
    {
        String scope = getScope(node);
        String type = getTypeToOllir(getTypeReturnedByNode(node, scope));
        String line = "\t\taux" + this.auxNumber + "." + type + " :=." + type + " ";
        String toReturn = "aux" + this.auxNumber + "." + type;
        this.auxNumber++;
        switch (node.getKind()) {
            case "IntegerLiteral": case "RestIdentifier":
                line = line + node.get("name") + "." + getTypeToOllir(getTypeReturnedByNode(node, scope));
                int varParam = checkVarIsArg(node.get("name"));
                if (varParam != 0) {
                    toReturn = "$" + varParam + "." + toReturn;
                }
                break;
            case "True":
                line = line + "1.bool";
                break;
            case "False":
                line = line + "0.bool";
                break;
            case "NewInstance":
                line = line + "new(" + node.get("name") + ")." + node.get("name");
                break;
            default:
                System.out.println("Unexpected behaviour: terminalArrayAccessNode");
                break;
        }
        this.assignmentOllir += line + ";\n";
        return toReturn;
    }

    public Boolean varInImports(JmmNode node){
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("ImportDeclaration") && symbol.getName().equals(node.get("name"))){
                return true;
            }
        }
        return false;
    }

    public String getClassName(JmmNode node)
    {
        return node.getAncestor("ClassDeclaration").get().getChildren().get(0).get("name");
    }

    /*-------------------------------------------------------------------------------------------------*/


    public String handleInsideFunction(JmmNode node) {
        List<String> a = new ArrayList<String>();
        String methodCall = node.get("name");
        String restIdent = node.getChildren().get(0).get("name");
        a.add(restIdent);
        a.add("\"" + methodCall + "\"");
        for (JmmNode child : node.getChildren().get(1).getChildren()) {
            a.add(child.get("name") + "." + types.get(varTypeST(child.get("name"), this.actualMethodName)));
        }
        return String.join(", ", a);
    }

    public String handleInsideFunctionVirtual(JmmNode node) {
        List<String> a = new ArrayList<String>();
        String methodCall = node.get("name");
        String restIdent = node.getChildren().get(0).get("name") + "." + types.get(varTypeST("s", this.actualMethodName));
        a.add(restIdent);
        a.add("\"" + methodCall + "\"");
        for (JmmNode child : node.getChildren().get(1).getChildren()) {
            a.add(child.get("name") + "." + types.get(varTypeST(child.get("name"), this.actualMethodName)));
        }
        return String.join(", ", a);
    }

    private String defaultVisit(JmmNode node, String ollirCode) {
        String ret = "";
        if (!(node.getChildren().isEmpty())) {
            for (JmmNode child : node.getChildren()) {
                ret += visit(child, ollirCode);
            }
        }
        return ret;
    }

    /**
     * 
     * @param node Node to be evaluated
     * @return Name of the first child's name
     */
    public String firstChildName(JmmNode node) {
        return node.getChildren().get(0).get("name");
    }

    /**
     * 
     * @param node Node of the method
     * @return Type of method
     */
    public String getMethodType(JmmNode node) {
        String type = types.get(firstChildName(node));
        if (type.equals("null")) {
            type = firstChildName(node);
        }
        return type;
    }

    /**
     * @param node Node of the method
     * @return String containing the method parameters
     */
    public String methodParameters(JmmNode node) {
        int i = 1;
        List<JmmNode> children = node.getChildren();

        while (i < children.size()) {
            JmmNode child = children.get(i);

            if (child.getKind().equals("Parameter")) {
                String paramType = types.get(firstChildName(child));
                parameters.add(child.get("name") + "." + paramType);
            }
            else break;
            i++;
        }
        return String.join(", ", parameters);
    }

    /**
     * 
     * @param var Variable to check
     * @return Return index of parameter on array or 0
    */
    public int checkVarIsArg(String var) {
        for (int i = 0; i < this.parameters.size(); i++) {
            if (this.parameters.get(i).startsWith(var)) {
                return this.parameters.get(i).indexOf(var) + 1;
            }
        }
        return 0;
    }

    /**
     * 
     * @param var Variable to check
     * @param methodName Method where variable is at
     * @return Type of variable or "" if not found
    */
    public String varTypeST(String var, String methodName) {
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            MySymbol symbol = entry.getValue();

            if (symbol.getName().equals(var) &&
                    (symbol.getScope().equals(methodName) || symbol.getScope().equals("GLOBAL"))) {
                return symbol.getType().getName();
            }
        }
        return "";
    }

    /**
     * Fills types variables with corresponding OLLIR types
    */
    public void fillTypesMap() {
        types.put("int", "i32");
        types.put("boolean", "bool");
        types.put("String", "String");
        types.put("int[]", "array.i32");
        types.put("String[]", "array.String");
        types.put("void", "V");
        
    }
    

    public String getTypeToOllir(String type)
    {
        switch (type) {
            case "int":
                return "i32";
            case "boolean":
                return "bool";
            case "int[]":
                return "array.i32";
            case "String[]":
                return "array.String";
            case "void": case "":
                return "V";
            default:
                return type;
        }

    }
}