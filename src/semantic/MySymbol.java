import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MySymbol extends Symbol {
    private boolean init = false;
    private String scope;
    private String name;
    private Map<String, ArrayList<MySymbol>> attributes = new HashMap<String, ArrayList<MySymbol>>();
    
    public MySymbol(Type type, String nodeName, String name, String scope){
        super(type, nodeName);
        this.scope = scope;
        this.name = name;
        
    }

    public void addAttribute(String key, MySymbol value)
    {
        if(attributes.containsKey(key))
        {
            this.attributes.get(key).add(value);
        }
        else
        {
            ArrayList<MySymbol> toAdd = new ArrayList<MySymbol>();
            toAdd.add(value);       
            this.attributes.put(key, toAdd);
        }
    }

    public Map<String, ArrayList<MySymbol>> getAttributes(){
        return attributes;
    }

    public String getName() {
        return this.name;
    }

    public boolean getInit() { return init; }

    public String getScope(){
        return this.scope;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getSuperName() {
        return super.getName();
    }

    @Override
    public String toString() {
        String temp = "";
        for (Map.Entry<String, ArrayList<MySymbol>> entry : attributes.entrySet()) {
            String attribute = entry.getKey();
            ArrayList<MySymbol> symbols = entry.getValue();

            temp += " " + attribute + ": ";
            for (int i = 0; i < symbols.size(); i++)
            {
                temp += symbols.get(i).getType().getName() + " " + symbols.get(i).getName() + "; ";
            }
        }
        return "Symbol [type=" + super.getType() + ", name=" + super.getName() + "]" + " Name: " + this.name + " Scope: " + this.scope + " " + temp + " Init: " + this.init;
    }

}

