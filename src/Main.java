import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.MainAnalysis;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.ollir.*;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.jasmin.JasminUtils;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;

public class Main implements JmmParser {

	public String readFile(String filePath) {
		try {
			Path path = Path.of(filePath);
			return Files.readString(path);	
		} catch (IOException e) {
			return null;
		}
	}

	public JmmParserResult parse(String jmmCode) {
		Parser parser = new Parser(new StringReader(jmmCode));

		try {
    		SimpleNode root = parser.Program(); // returns reference to root node
            	
    		root.dump(""); // prints the tree on the screen
			//System.out.println(root.toJson());

    		return new JmmParserResult(root, parser.getReports());
		} catch(ParseException e) {
			Report r = new Report(ReportType.ERROR, Stage.SYNTATIC, 0, e.getMessage());
			parser.getReports().add(r);

			return new JmmParserResult(null, parser.getReports());
		}
	}

    public static void main(String[] args) {
		if (args.length != 1) { System.out.println("Invalid Arguments"); return; }

		Main compiler = new Main();
		String jmmCode = compiler.readFile(args[0]);
		JmmParserResult parserResult = compiler.parse(jmmCode);

		String fileNameWithoutExtension = compiler.getFileNameWithoutExtension(args[0]);

		// Output Original
		compiler.writeToFile(compiler.parseToFile(args[0], ".jmm"), fileNameWithoutExtension, jmmCode);

		// Output to JSON
		compiler.writeToFile(compiler.parseToFile(args[0], ".json"), fileNameWithoutExtension, parserResult.toJson());

		if(!parserResult.getReports().isEmpty()){
			System.out.println(parserResult.getReports());
			return;
		}

		AnalysisStage as = new AnalysisStage();
		JmmSemanticsResult semanticsResult = as.semanticAnalysis(parserResult);

		// Output to symbols.txt
		compiler.writeToFile(compiler.parseToFile(args[0], ".symbols.txt"), fileNameWithoutExtension, semanticsResult.getSymbolTable().print());

		if(!semanticsResult.getReports().isEmpty()){
			System.out.println(semanticsResult.getReports());
			return;
		}

		OptimizationStage os = new OptimizationStage();
		OllirResult ollirResult = os.toOllir(semanticsResult);
		System.out.println("\nOLLIR Code generated with success.\n");

		if(!ollirResult.getReports().isEmpty()){
			System.out.println(ollirResult.getReports());
			return;
		}

		// Output to ollir
		compiler.writeToFile(compiler.parseToFile(args[0], ".ollir"), fileNameWithoutExtension, ollirResult.getOllirCode());

		BackendStage bs = new BackendStage();
		JasminResult jasminResult = bs.toJasmin(ollirResult);
		compiler.compile(jasminResult, fileNameWithoutExtension);

		// Output to j
		compiler.writeToFile(compiler.parseToFile(args[0], ".j"), fileNameWithoutExtension, jasminResult.getJasminCode());

		JasminUtils.assemble(new File("compiled/" + fileNameWithoutExtension + "/" + compiler.parseToFile(args[0], ".j")), new File("./compiled/" + fileNameWithoutExtension));
	}


	public void compile(JasminResult jasminResult, String fileWithoutExtension){
		List<String> classpaths = new ArrayList<>();
		List<String> args = new ArrayList<>();
		classpaths.add(TestUtils.getLibsClasspath());
		classpaths.add("compiled/" + fileWithoutExtension);
		jasminResult.run(args, classpaths);
	}


	public void writeToFile(String fileName, String fileWithoutExtension, String content){
		try {
			File newDir = new File("compiled/" + fileWithoutExtension);
			if (!newDir.exists()){
				newDir.mkdirs();
			}
			FileWriter fileWriter = new FileWriter("compiled/" + fileWithoutExtension + "/" + fileName);		
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String parseToFile(String fileName, String extension){
		return getFileNameWithoutExtension(fileName) + extension;
	}

	public String getFileNameWithoutExtension(String fileName){
		String fileNameWithoutPath = fileName.substring(fileName.lastIndexOf("/") + 1);
		if (fileNameWithoutPath.indexOf(".") != -1) 
		{
			return fileNameWithoutPath.substring(0 , fileNameWithoutPath.indexOf("."));
		}
		System.out.println("Failed to get file name without extension");
		return null;
	}
}
