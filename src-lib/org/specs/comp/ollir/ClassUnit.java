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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing all the information loaded from the OLLIR input.
 */
public class ClassUnit {

    String classPackage;

    ArrayList<String> imports;
    Set<String> importClasses;

    String className;

    String superClass;

    AccessModifiers classAccessModifier = AccessModifiers.DEFAULT;

    boolean staticClass = false;

    boolean finalClass = false;

    ArrayList<Field> fieldList;

    ArrayList<Method> methodList;

    public int getNumFields() {
        return fieldList.size();
    }

    public int getNumMethods() {
        return methodList.size();
    }

    /**
     * 
     * @return the name of the parent class if this class extends another, or null otherwise
     */
    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void checkMethodLabels() throws OllirErrorException {
        for (Method method : methodList) {
            method.checkLabels();
        }
    }

    public void buildCFGs() {
        for (Method method : methodList) {
            method.buildCFG();
        }
    }

    public void buildVarTables() {
        for (Method method : methodList) {
            method.buildVarTable();
        }
    }

    public void outputCFGs() throws OllirErrorException {
        for (Method method : methodList) {
            method.outputCFG();
        }
    }

    public String getClassName() {
        return this.className;
    }

    public String getImport(int index) {
        return this.imports.get(index);
    }

    public ArrayList<String> getImports() {
        return this.imports;
    }

    public void addImport(String str) {
        this.imports.add(str);

        var simpleClassName = Ollir.getSimpleClassName(str);

        var isNew = this.importClasses.add(simpleClassName);

        if (!isNew) {
            throw new RuntimeException(
                    "Code has two imports with the name simple class name: " + simpleClassName);
        }
    }

    /**
     * 
     * @param simpleClassName
     * @return true is the given class name is an import (e.g. "io" will return true if there is an import foo.io;),
     *         false otherwise
     */
    public boolean isImportedClass(String simpleClassName) {
        return importClasses.contains(simpleClassName);
    }

    public Set<String> getImportedClasseNames() {
        return importClasses;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public Field getField(int index) {
        return this.fieldList.get(index);
    }

    public ArrayList<Field> getFields() {
        return this.fieldList;
    }

    public void addField(Field newField) {
        this.fieldList.add(newField);
    }

    public Method getMethod(int index) {
        return this.methodList.get(index);
    }

    public ArrayList<Method> getMethods() {
        return this.methodList;
    }

    public void addMethod(Method newMethod) {
        this.methodList.add(newMethod);
    }

    public void setClassAccessModifier(AccessModifiers classAccessModifier) {
        if (this.classAccessModifier != AccessModifiers.DEFAULT)
            System.out.println("Warning: class access modifier previously set to: " + this.classAccessModifier);
        else
            this.classAccessModifier = classAccessModifier;
    }

    public AccessModifiers getClassAccessModifier() {
        return this.classAccessModifier;
    }

    public boolean isStaticClass() {
        return this.staticClass;
    }

    public void setStaticClass() {
        this.staticClass = true;
    }

    public boolean isFinalClass() {
        return this.staticClass;
    }

    public void setFinalClass() {
        this.finalClass = true;
    }

    public void setPackage(String classPackage) {
        this.classPackage = classPackage;
    }

    public String getPackage() {
        return this.classPackage;
    }

    public void show() {
        System.out.println("** Name of the package: " + this.classPackage);
        System.out.println("** Name of the class: " + this.className);
        System.out.println("\tAccess modifier: " + this.classAccessModifier);
        System.out.println("\tStatic class: " + this.staticClass);
        System.out.println("\tFinal class: " + this.finalClass);

        for (Field field : fieldList) {
            field.show();
        }

        for (Method method : methodList) {
            method.show();
        }
    }

    public ClassUnit() {
        this.fieldList = new ArrayList<Field>();
        this.methodList = new ArrayList<Method>();
        this.imports = new ArrayList<String>();
        this.importClasses = new HashSet<>();
        this.superClass = null;
    }
}
