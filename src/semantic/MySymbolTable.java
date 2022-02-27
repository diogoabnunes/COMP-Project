import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import pt.up.fe.comp.jmm.JmmNode;


public class MySymbolTable implements SymbolTable {

    Map<JmmNode, MySymbol> table = new LinkedHashMap();
    
    public MySymbolTable() {}

    public void add(JmmNode node, MySymbol symbol){
        
        table.put(node, symbol);
    }

    public Map<JmmNode, MySymbol> getTable(){
        return this.table;
    }

    @Override
    public List<String> getImports() {
        List<String> imports = new ArrayList<>();
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getType().getName().equals("ImportDeclaration")) {
                imports.add(symbol.getName());
            }
        }
        return imports;
    }
    
    @Override
    public String getClassName(){
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getType().getName().equals("ClassDeclaration")) {
                return symbol.getName();
            }
        }
        return "";
    }
    

    @Override
    public String getSuper(){
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getType().getName().equals("ClassDeclaration") && symbol.getAttributes().containsKey("Extends")) {
                return symbol.getAttributes().get("Extends").get(0).getSuperName();
            }
        }
        return "";
    }

    @Override
    public List<Symbol> getFields(){
        List<Symbol> fields = new ArrayList<>();
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();
            if (symbol.getSuperName().equals("VarDeclaration") &&
                symbol.getScope().equals("GLOBAL")) {
                fields.add(symbol);
            }
        }
        return fields;
    }
    
    @Override
    public List<String> getMethods(){
        List<String> methods = new ArrayList<>();
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getSuperName().equals("MethodDeclaration")) {
                methods.add(symbol.getName());
            }
        }
        return methods;
    }
    
    @Override
    public Type getReturnType(String methodName){
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getSuperName().equals("MethodDeclaration") &&
                symbol.getName().equals(methodName)) {
                return symbol.getType();
            }
        }
        return null;
    }

    @Override
    public List<Symbol> getParameters(String methodName){
        List<Symbol> par = new ArrayList<>();
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            if (symbol.getSuperName().equals("MethodDeclaration") &&
                symbol.getName().equals(methodName) && 
                symbol.getAttributes().containsKey("Parameter")) {
                    for (int i = 0; i < symbol.getAttributes().get("Parameter").size(); i++){
                        par.add(symbol.getAttributes().get("Parameter").get(i));
                    }
            }
        }
        return par;
    }
    
    @Override
    public List<Symbol> getLocalVariables(String methodName){
        List<Symbol> local = new ArrayList<>();
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();
            System.out.println(symbol.getSuperName() + " " + symbol.getScope());
            if (symbol.getSuperName().equals("VarDeclaration") &&
                symbol.getScope().equals(methodName)) {
                    local.add(symbol);
            }
        }
        return local;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void printTable(){
        for (Map.Entry<JmmNode, MySymbol> entry : table.entrySet()) {
            JmmNode node = entry.getKey();
            MySymbol symbol = entry.getValue();

            System.out.println(symbol);
        }
    }

}