import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ast.examples.ExamplePostorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePreorderVisitor;
import pt.up.fe.comp.jmm.ast.examples.ExamplePrintVariables;
import pt.up.fe.comp.jmm.ast.examples.ExampleVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class AnalysisStage implements JmmAnalysis {

	private MySymbolTable table = new MySymbolTable();

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        if (parserResult.getReports().isEmpty() && parserResult.getRootNode() != null) {
			JmmNode node = parserResult.getRootNode().sanitize();
			
			var stGenerator = new SymbolTableGenerator();
			Boolean temp = stGenerator.visit(node);
			MySymbolTable st = stGenerator.getSymbolTable();

			this.table = stGenerator.getSymbolTable(); 

			ArrayList<Report> reports = new ArrayList<Report>();
			var semanticAnalysisVisitor = new SemanticAnalysisVisitor(st);
			semanticAnalysisVisitor.visit(node, reports);

			st.printTable();
			System.out.println(st.print());
			
			return new JmmSemanticsResult(node, st, reports);
		}
		return null;

    }

	public MySymbolTable getSymbolTable(){
		return this.table;
	}
}