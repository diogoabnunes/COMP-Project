import java.util.List;
import java.util.Map;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp.jmm.analysis.table.Symbol;

class SymbolTableGenerator extends PreorderJmmVisitor<List<Report>, Boolean> {

    private MySymbolTable symbolTable = new MySymbolTable();

    public MySymbolTable getSymbolTable(){
        return this.symbolTable;
    }

    public SymbolTableGenerator() {

        addVisit("ImportDeclaration", this::handleImportDeclaration);
        addVisit("ClassDeclaration", this::handleClassDeclaration);
        addVisit("VarDeclaration", this::handleVarDeclaration);
        addVisit("MethodDeclaration", this::handleMethodDeclaration);
        addVisit("MainDeclaration", this::handleMainDeclaration);
        addVisit("Parameter", this::handleParameter);
        addVisit("Extends", this::handleExtends);
        addVisit("Assignment", this::handleAssignment);

        setDefaultVisit(this::defaultVisit);

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

    public Boolean handleAssignment(JmmNode node, List<Report> reports){
        String scope = getScope(node);
        String name = new String();
        if (node.getChildren().get(0).getKind().equals("ArrayAccess"))
        {
            name = node.getChildren().get(0).getChildren().get(0).get("name");
        }
        else{
            name = node.getChildren().get(0).get("name");
        }
        for (Map.Entry<JmmNode, MySymbol> entry : symbolTable.getTable().entrySet()) {
            JmmNode tempNode = entry.getKey();
            MySymbol symbol = entry.getValue();
        }
        
        return defaultVisit(node, reports);
    }

    public Boolean handleImportDeclaration(JmmNode node, List<Report> reports){
        
        MySymbol symbol = new MySymbol(new Type(node.getKind(), false), node.getKind(), node.get("name"), "GLOBAL");
        symbolTable.add(node, symbol);        
        
        return defaultVisit(node, reports);
    }

    public Boolean handleClassDeclaration(JmmNode node, List<Report> reports){
        MySymbol symbol = new MySymbol(new Type(node.getKind(), false), node.getKind(), node.getChildren().get(0).get("name"), "GLOBAL");
        symbolTable.add(node, symbol);        
        
        return defaultVisit(node, reports);
    }

    public Boolean handleVarDeclaration(JmmNode node, List<Report> reports){
        
        Boolean isArray = false;
        if (node.getChildren().get(0).get("name").equals("int[]")) 
            isArray = true;
        
        if (node.getParent().getKind().equals("ClassDeclaration"))
        {
            MySymbol symbol = new MySymbol(new Type(node.getChildren().get(0).get("name"), isArray), node.getKind(), node.get("name"), "GLOBAL");
            symbolTable.add(node, symbol);
        }
        else if (node.getParent().getKind().equals("MainDeclaration"))
        {
            MySymbol symbol = new MySymbol(new Type(node.getChildren().get(0).get("name"), isArray), node.getKind(), node.get("name"), node.getParent().getKind());
            symbolTable.add(node, symbol);   
        }
        else
        {
            MySymbol symbol = new MySymbol(new Type(node.getChildren().get(0).get("name"), isArray), node.getKind(), node.get("name"), node.getParent().get("name"));
            symbolTable.add(node, symbol);        
        }        
        return defaultVisit(node, reports);
    }

    public Boolean handleMethodDeclaration(JmmNode node, List<Report> reports){
        Boolean isArray = false;
        if (node.getChildren().get(0).get("name").equals("int[]")) 
            isArray = true;
        
        MySymbol symbol = new MySymbol(new Type(node.getChildren().get(0).get("name"), isArray), node.getKind(), node.get("name"), "GLOBAL");
        symbolTable.add(node, symbol);      
        
        return defaultVisit(node, reports);
    }

    public Boolean handleMainDeclaration(JmmNode node, List<Report> reports){
        MySymbol symbol = new MySymbol(new Type(node.getKind(), false), node.getKind(), node.getKind(), "GLOBAL");
        symbolTable.add(node, symbol);        
        
        return defaultVisit(node, reports);
    }
    
    public Boolean handleParameter(JmmNode node, List<Report> reports){
        MySymbol symbol = new MySymbol(new Type(node.getChildren().get(0).get("name"), node.getChildren().get(0).get("name").equals("int[]")), node.get("name"), node.get("name"), node.getParent().get("name"));
        symbol.setInit(true);
        this.symbolTable.getTable().get(node.getParent()).addAttribute("Parameter", symbol);  
        symbolTable.add(node, symbol); 
        
        return defaultVisit(node, reports);
    }

    public Boolean handleExtends(JmmNode node, List<Report> reports){
        MySymbol symbol = new MySymbol(new Type(node.getKind(), false), node.get("extendedClass"), node.getKind(), node.getParent().getKind());
        
        this.symbolTable.getTable().get(node.getParent()).addAttribute("Extends", symbol);

        return defaultVisit(node, reports);

    }

    private Boolean defaultVisit(JmmNode node, List<Report> reports) {
        return true;
    }


}