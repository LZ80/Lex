/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import java.util.ArrayList;

/**
 *
 * @author Arkai Ariza
 */
public class Tree<T> {
    public Node<T> root;

    public Tree(Node<T> node) {
        root = node;
        root.data = node.data;
        root.children = node.children;
    }

    public static class Node<T> {
        
        public enum NodeType {
            programNode, blockNode, exprNode,
            tNode, fNode, rNode, statsNode, mStatNode, statNode,
            ifNode, whileNode, assignNode, declarationNode, loNode
        } ;
        
        public Node(){};
        
        public Node(NodeType nodeType)
        {
            
            this.data = new ArrayList<>();
            this. nodeType = nodeType;
            this.children = new ArrayList<>();
        }
        
        public ArrayList<T> data;
        public NodeType nodeType;
        public Node<T> parent;
        public ArrayList<Node<T>> children;
    }
    
    
}