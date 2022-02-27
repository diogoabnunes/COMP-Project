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

import org.specs.comp.ollir.parser.OllirParser;
import org.specs.comp.ollir.parser.ParseException;

/**
 * Class with a main example with the current stages of the OLLIR tool.
 */
public class Ollir {

    /**
     * Main example with the stages to load an OLLIR file and to build the data structures to be used by backends.
     *
     * @param args
     *            Name of the input file with the OLLIR
     */
    public static void main(String[] args) {

        OllirParser parser;

        if (args.length == 0) {
            System.out.println("OLLIR 0.1:  Reading from standard input . . .");
            parser = new OllirParser(System.in);
        } else if (args.length == 1) {
            System.out.println("OLLIR 0.1:  Reading from file " + args[0] + " . . .");
            try {
                parser = new OllirParser(new java.io.FileInputStream(args[0]));
            } catch (java.io.FileNotFoundException e) {
                System.out.println("OLLIR 0.1:  File " + args[0] + " not found.");
                return;
            }
        } else {
            System.out.println("OLLIR 0.1:  Usage is one of:");
            System.out.println("         java org.specs.comp.ollir.Ollir < inputfile");
            System.out.println("OR");
            System.out.println("         java org.specs.comp.ollir.Ollir inputfile");
            return;
        }

        try {
            parser.ClassUnit(); // parse the input OLLIR and represent it by the class structure used
            System.out.println("OLLIR 0.1:  OLLIR code parsed successfully.");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("OLLIR 0.1:  Encountered errors during parse.");
        }

        // get Class instance representing the OLLIR file loaded
        ClassUnit myClass = parser.getMyClass();
        try {
            myClass.checkMethodLabels(); // check the use of labels in the OLLIR loaded
            myClass.buildCFGs(); // build the CFG of each method
            myClass.outputCFGs(); // output to .dot files the CFGs, one per method
            myClass.buildVarTables(); // build the table of variables for each method
            myClass.show(); // print to console main information about the input OLLIR
        } catch (OllirErrorException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getSimpleClassName(String className) {
        String separator = ".";

        int extIndex = className.lastIndexOf(separator);
        if (extIndex < 0) {
            return className;
        }

        return className.substring(extIndex + 1, className.length());
    }
}
