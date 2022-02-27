import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import java.util.List;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp.jmm.JmmNode;
import java.util.Map;

import java.util.ArrayList;
import java.util.Arrays;


class SemanticAnalysisVisitor extends PreorderJmmVisitor<ArrayList<Report>, Boolean> {

    private MySymbolTable symbolTable = new MySymbolTable();

    List<String> booleanArray = Arrays.asList("True", "False", "Less", "And", "Or", "Not");
    List<String> intArray = Arrays.asList("IntegerLiteral", "DotLength", "ArrayAccess");
    List<String> varArray = Arrays.asList("MethodCall", "RestIdentifier");
    List<String> newInstanceArray = Arrays.asList("NewInstance");
    List<String> typeReturner = Arrays.asList("True", "False", "Less", "And", "Or", "Not", "IntegerLiteral", "DotLength", "ArrayAccess", "MethodCall", "RestIdentifier", "NewInstance");
    List<String> types = Arrays.asList("int", "boolean", "int[]");
 
    public SemanticAnalysisVisitor(MySymbolTable symbolTable) {

        this.symbolTable = symbolTable;

        addVisit("Assignment", this::handleAssignment);
        addVisit("Less", this::handleLess);
        addVisit("And", this::handleAnd);
        addVisit("Not", this::handleNot);
        addVisit("ifStatement", this::handleIfStatementWhileStatement);
        addVisit("whileStatement", this::handleIfStatementWhileStatement);
        addVisit("ArrayAccess", this::handleArrayAccess);
        addVisit("ReturnExpression", this::handleReturnExpression);
        addVisit("MethodCall", this::handleMethodCall);
        addVisit("Operation", this::handleOperation);
        addVisit("NewArray", this::handleNewArray);
        addVisit("DotLength", this::handleDotLength);
        addVisit("VarDeclaration", this::handleVarDeclaration);

        setDefaultVisit(this::defaultVisit);

    }

    public Boolean varIsThisOrClass(JmmNode node, String scope)
    {
        if (node.getKind().equals("This"))
        {
            return true;
        }
        else
        {
            for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
                JmmNode tempNode = entry.getKey();
                MySymbol symbol = entry.getValue();

                if (tempNode.getKind().equals("ClassDeclaration") && symbol.getName().equals(getType(node, scope))){
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean extendsExists()
    {
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            ArrayList<MySymbol> tempArray = new ArrayList<MySymbol>();

            tempArray = symbol.getAttributes().get("Extends");

            if (tempNode.getKind().equals("ClassDeclaration") && tempArray != null){
                return true;
            }
        }
        return false;
    }

    public Boolean varIsObject(JmmNode node, String scope)
    {
        if (getType(node, scope).equals("int") || getType(node, scope).equals("int[]") || getType(node, scope).equals("boolean"))
        {
            return false;
        }
        else if (typeInImports(node.get("name")))
        {
            return true;
        }
        else if (typeInImports(getType(node, scope)))
        {
            return true;
        }
        return false;
    }

    public Boolean typeInImports(String type){
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("ImportDeclaration") && symbol.getName().equals(type)){
                return true;
            }
        }
        return false;
    }

    public Boolean methodTypeUnknown(JmmNode node, String scope)
    {
        if (varIsThisOrClass(node.getChildren().get(0), scope))
        {
            if (!extendsExists())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (varIsObject(node.getChildren().get(0), scope))
        {
            return true;
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

    
    // Function that goes through every child and returns true if scope is correct, false otherwise
    public Boolean scopeIsCorrect(String currScope, JmmNode node) {
        if (node.getKind().equals("RestIdentifier"))
        {
            if (!inScope(node, currScope))
            {
                return false;
            }
        }
        for (int i = 0; i < node.getChildren().size(); i++)
        {
            if (node.getChildren().get(i).getKind().equals("RestIdentifier"))
            {
                if (!inScope(node.getChildren().get(i), currScope))
                {
                    return false;
                }
            }
            else
            {
                if (!scopeIsCorrect(currScope, node.getChildren().get(i)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    // Checks if the scope in an assignment is the current or global and var is initialized
    public Boolean inScope(JmmNode node, String scope) {
        if (node.getKind().equals("RestIdentifier") || node.getKind().equals("Var"))
        {
            for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
                MySymbol symbol = entry.getValue();

                if (symbol.getName().equals(node.get("name"))
                        && (symbol.getScope().equals("GLOBAL") || symbol.getScope().equals(scope))) {
                    if (node.getKind().equals("RestIdentifier") && !symbol.getInit() && !node.getParent().getKind().equals("ArrayAccess") && symbol.getSuperName().equals("VarDeclaration"))
                    {
                        return false;
                    }
                    if (node.getKind().equals("Var"))
                    {
                        symbol.setInit(true);
                    }
                    return true;
                }
            }
            return false;
        }
        else
        {
            return true;
        }   
    }

    public String getMethodDeclarationType(JmmNode node)
    {
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("MethodDeclaration") && symbol.getName().equals(node.get("name"))){
                return symbol.getType().getName();
            }
        }

        return null;
    }

    
    public Integer getMethodNumParameters(JmmNode node)
    {
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("MethodDeclaration") && symbol.getName().equals(node.get("name"))){
                return symbol.getAttributes().get("Parameter").size();
            }
        }

        return -1;
    }

    // Returns the type of a variable/method
    public String getType(JmmNode node, String scope){
        String tempType = new String();
        String nodeName = new String();
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

    public Boolean checkTypes(JmmNode node, String type, String scope)
    {
        if (this.intArray.contains(node.getKind()) || this.booleanArray.contains(node.getKind()) || this.varArray.contains(node.getKind()) || this.newInstanceArray.contains(node.getKind()))
        {
            if (this.intArray.contains(node.getKind()))
            {
                if (!type.equals("int"))
                {
                    return false;
                }

            }
            else if (this.booleanArray.contains(node.getKind()))
            {
                if(!type.equals("boolean")){                    
                    return false;
                }
            }
            else if(this.newInstanceArray.contains(node.getKind())){
                if(!type.equals(node.get("name")))
                {
                    return false;
                }
            }
            else if (this.varArray.contains(node.getKind()))
            {
                if (!type.equals(getType(node, scope)) && node.getKind().equals("MethodCall") && !methodTypeUnknown(node, scope))
                {
                    return false;
                }
                else if(!type.equals(getType(node, scope)) && node.getKind().equals("RestIdentifier"))
                {
                    return false;
                }
            }
            return true;
        }
        for (int i = 0; i < node.getChildren().size(); i++)
        {
            if (this.intArray.contains(node.getChildren().get(i).getKind()))
            {
                if(!type.equals("int")){
                    return false;             
                }
            }
            else if (this.booleanArray.contains(node.getChildren().get(i).getKind()))
            {
                if (!type.equals("boolean"))
                {
                    return false;
                }
            }
            else if (this.newInstanceArray.contains(node.getChildren().get(i).getKind()))
            {
                if (!type.equals(node.get("name")))
                {
                    return false;
                }
            }
            else if (this.varArray.contains(node.getChildren().get(i).getKind()))
            {
                if (!type.equals(getType(node.getChildren().get(i), scope)) && node.getChildren().get(i).getKind().equals("MethodCall") && !methodTypeUnknown(node.getChildren().get(i), scope))
                {
                    return false;
                }
                else if(!type.equals(getType(node.getChildren().get(i), scope)) && node.getChildren().get(i).getKind().equals("RestIdentifier"))
                {
                    return false;
                }
            }
            else
            {
                if (!checkTypes(node.getChildren().get(i), type, scope))
                    return false;
            }
        }
        return true;
    } 

    public Integer methodNumPar(String methodName){

        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("MethodDeclaration") && symbol.getName().equals(methodName)){
                ArrayList<MySymbol> tempArray = new ArrayList<MySymbol>();
                tempArray = symbol.getAttributes().get("Parameter");
                
                if(tempArray != null)
                    return tempArray.size();
                else
                    return 0;
            }
        }

        return -1;

    }

    public Boolean methodExists(String methodName){
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (tempNode.getKind().equals("MethodDeclaration") && symbol.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }

    public Boolean methodCheckParTypes(String methodName, List<JmmNode> nodes, String scope){
        Integer n = 0;
        
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();
            

            if (tempNode.getKind().equals("MethodDeclaration") && symbol.getName().equals(methodName)){
                ArrayList<MySymbol> tempArray = new ArrayList<MySymbol>();
                tempArray = symbol.getAttributes().get("Parameter");

                if(tempArray != null){
                    for (int i = 0; i < symbol.getAttributes().get("Parameter").size(); i++)
                    {
                        if (!symbol.getAttributes().get("Parameter").get(i).getType().getName().equals(getTypeReturnedByNode(nodes.get(i), scope)))
                        {
                            return false;
                        }
                    }
                    return true;
                }
                else
                    return (nodes.size() == 0);
            }
        }


        return false;
    }

    public Boolean handleVarDeclaration(JmmNode node, ArrayList<Report> reports) {

        if (!typeInImports(node.getChildren().get(0).get("name")) && !types.contains(node.getChildren().get(0).get("name")) && !node.getChildren().get(0).get("name").equals(node.getAncestor("ClassDeclaration").get().getChildren().get(0).get("name")))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Unknown type '" + node.getChildren().get(0).get("name") + "'."));
        
        return defaultVisit(node, reports);
    }

    public Boolean handleMethodCall(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        Integer numChildren = node.getChildren().get(1).getNumChildren();

        if (!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("int") && !getTypeReturnedByNode(node.getChildren().get(0), scope).equals("boolean") && !getTypeReturnedByNode(node.getChildren().get(0), scope).equals("int[]")){
            if (methodTypeUnknown(node, scope))
            {
                if (methodNumPar(node.get("name")) != numChildren && methodNumPar(node.get("name")) != -1)
                {
                    if (!methodTypeUnknown(node, scope))
                    {
                        reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                        "The number of parameters in function " + node.get("name") + " is wrong."));
                    }
                }
            }
            else if (methodExists(node.get("name")) && (methodNumPar(node.get("name")) != numChildren) )
            {
                if (!methodTypeUnknown(node, scope))
                {
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                        "The number of parameters in function " + node.get("name") + " is wrong."));
                }
            }
            else if (!methodExists(node.get("name")))
            {
                reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                        "The method " + node.get("name") + " does not exist."));
            }
            else
            {
                if (!methodCheckParTypes(node.get("name"), node.getChildren().get(1).getChildren(), scope))
                {
                    reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                        "The method " + node.get("name") + " does not have the correct parameter types."));
                }
            }
        }
        else
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                        "The method " + node.get("name") + " can't be applied to the current type."));
        }
        
        return defaultVisit(node, reports);
    }

    public Boolean handleDotLength(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);

        if(!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("int[]"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "The method length is not called on an array."));
        }

        return defaultVisit(node, reports);
    }

    public Boolean handleNewArray(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);

        if (!getTypeReturnedByNode(node, scope).equals("int"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Array does not have an 'int' on the access."));
        }
        
        return defaultVisit(node, reports);
    }

    public Boolean handleReturnExpression(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getMethodDeclarationType(node.getParent()).equals(getTypeReturnedByNode(node, scope)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Wrong return type."));
        }

        return defaultVisit(node, reports);
    }

    public Boolean handleArrayAccess(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }
        if (!scopeIsCorrect(scope, node.getChildren().get(1)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getType(node.getChildren().get(0), scope).equals("int[]"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Trying to access a variable that is not an array."));
        }

        if (!getTypeReturnedByNode(node.getChildren().get(1), scope).equals("int"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Array does not have an 'int' on the access."));
        }

        return defaultVisit(node, reports);
    }
    public Boolean handleIfStatementWhileStatement(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("boolean"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Condition does not return a 'boolean'."));
        }
        
        return defaultVisit(node, reports);
    }

    public Boolean handleNot(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("boolean"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Operator '!' not used on a 'boolean'."));
        }
    
        return defaultVisit(node, reports);
    }

    public Boolean handleAnd(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope.AccessController."));
        }
        if (!scopeIsCorrect(scope, node.getChildren().get(1)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("boolean") || !getTypeReturnedByNode(node.getChildren().get(1), scope).equals("boolean"))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Operator '&&' does not have both operands being 'boolean'."));
        }

        return defaultVisit(node, reports);
    }

    public Boolean handleLess(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (!scopeIsCorrect(scope, node.getChildren().get(0)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }
        if (!scopeIsCorrect(scope, node.getChildren().get(1)))
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Variable does not exist in current scope."));
        }

        if (!getTypeReturnedByNode(node.getChildren().get(0), scope).equals("int") || !getTypeReturnedByNode(node.getChildren().get(1), scope).equals("int"))
        {
            
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Operator '<' does not have both operands being 'int'."));
        }

        return defaultVisit(node, reports);
        

    }

    public String getScope(JmmNode node){
        String scope = new String();
        if (node.getAncestor("MethodDeclaration").isPresent()) {
            scope = node.getAncestor("MethodDeclaration").get().get("name");
        } else if (node.getAncestor("MainDeclaration").isPresent()) {
            scope = "MainDeclaration";
        } else if (node.getAncestor("ClassDeclaration").isPresent()) {
            scope = "GLOBAL";
        }
        return scope;
    }

    public Boolean handleOperation(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);
        if (getTypeReturnedByNode(node.getChildren().get(0), scope).equals("boolean") || getTypeReturnedByNode(node.getChildren().get(1), scope).equals("boolean"))        
        {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Invalid operation."));
        }
       
       return defaultVisit(node, reports);
    }

    public Boolean handleAssignment(JmmNode node, ArrayList<Report> reports) {
        String scope = getScope(node);

        if (!this.inScope(node.getChildren().get(0), scope)) {
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    node.getChildren().get(0).get("name") + " is not in current scope."));
        }

        if (!scopeIsCorrect(scope, node.getChildren().get(1)))
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Assignment with wrong scopes."));

        String type = getType(node.getChildren().get(0), scope);

        if (!checkTypes(node.getChildren().get(1), type, scope) && !(type.equals("int[]") && node.getChildren().get(1).getKind().equals("NewArray"))){
            reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC, Integer.valueOf(node.get("line")), Integer.valueOf(node.get("column")),
                    "Operation with different types."));
        }
        return defaultVisit(node, reports);
    }

    private Boolean defaultVisit(JmmNode node, ArrayList<Report> reports) {
        return true;
    }

}