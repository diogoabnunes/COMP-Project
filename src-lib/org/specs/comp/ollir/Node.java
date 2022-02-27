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
import java.util.List;

/**
 * Class the represents a node in the CFG.
 */
public class Node {

    // th id is just for identifying each node with a number
    int id;

    NodeType type;

    // number of sucessor instruction is from 1 to 2
    // (the conditional branch instructions are the only ones with two succ instruction: one
    // if the condition is true and the other when the condition is false)
    List<Node> succNodes = new ArrayList<>();

    // typically the number of predecessor instructions is 1, but the instructions
    // reached by goto and conditional branches may have more than 1 pred instructions
    List<Node> predNodes = new ArrayList<>();

    public void addSucc(Node n1) {
        this.succNodes.add(n1);
    }

    public void addPred(Node n1) {
        this.predNodes.add(n1);
    }

    public Node getSucc1() {
        // System.out.println("Node "+id+" "+this.succNodes.size());
        if (this.succNodes.size() >= 1)
            return this.succNodes.get(0);
        else
            return null;
    }

    public Node getSucc2() {
        if (this.succNodes.size() >= 2)
            return this.succNodes.get(1);
        else
            return null;
    }

    public ArrayList<Node> getPred() {
        // Casting, to maintain compatibility
        return (ArrayList<Node>) this.predNodes;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NodeType getNodeType() {
        return this.type;
    }

    public void showNode() {
        System.out.println("Node id: " + this.id + " type: " + this.type);
    }

    public Node(NodeType type) {
        this.type = type;
        if (type != NodeType.END)
            this.succNodes = new ArrayList<Node>();
        if (type != NodeType.BEGIN)
            this.predNodes = new ArrayList<Node>();
    }

    public Node() {
        this.type = NodeType.INSTRUCTION;
        this.succNodes = new ArrayList<Node>();
        this.predNodes = new ArrayList<Node>();
    }
}
