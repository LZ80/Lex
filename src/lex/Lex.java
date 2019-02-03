/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lex.Tree.Node;

/**
 *
 * @author Arkai Ariza
 */
public class Lex {
    
    static Tree<Token> sTree;
    static ArrayList<Token> myTokens;
    static Iterator<Token> iToken;
    static Token currentToken;
    
    
    public static void main(String[] args) throws IOException {
        String file = readFile("Test.java");
        myTokens = lex(file);
        
        System.out.println(file);
        /*for(char x : file.toCharArray())
        {
            System.out.println((int)(x));
        }*/
        for (Token tok : myTokens) {
            System.out.println(tok);
        }
        
        makeTree();
        printTree(sTree.root, 0, 1);
    }
    
    public static ArrayList<Token> lex(String input) {
        int line = 1;
        ArrayList<Token> tokens = new ArrayList<Token>();

        StringBuffer tokenPatternsBuffer = new StringBuffer();
        for (Token.TokenType tokenType : Token.TokenType.values()) {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));

        }
        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find()) {
            for (Token.TokenType tk : Token.TokenType.values()) {
                if (matcher.group(Token.TokenType.SKIP.toString()) != null) {
                    //tokens.add(new Token(tk, matcher.group(tk.name()), line));
                    continue;
                }
                else if(matcher.group(Token.TokenType.LSKIP.toString()) != null)
                {
                    if(tk.name() == Token.TokenType.LSKIP.toString())
                    {
                        //tokens.add(new Token(tk, matcher.group(tk.name()), line));
                        line++;
                    }
                    
                    continue;
                }
                else if (matcher.group(tk.name()) != null) {
                    tokens.add(new Token(tk, matcher.group(tk.name()), line));
                    continue;
                }
            }
        }
        return tokens;
    }

    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
    
    public static void makeTree()
    {
        iToken = myTokens.iterator();
        
        sTree = new Tree(program());
        
        System.out.println(currentToken);
        
        if(currentToken.type == Token.TokenType.EOF)
        {
            System.out.println("Parse OK");
        }
        else
        {
            System.exit(1);
        }
    }
    
    public static Node<Token> program()
    {
        Node<Token> node = getNode(Node.NodeType.programNode);
        
        currentToken = iToken.next();
        //System.out.println(currentToken);
        if(currentToken.type == Token.TokenType.BEGIN)
        {
            currentToken = iToken.next();
          //  System.out.println(currentToken);
        }
        else
        {
            System.out.printf("ERROR. Expected 'Begin Token' in line %d\n", currentToken.line);
            System.exit(1);
        }
        
        node.children.add(block());
        
        if(currentToken.type == Token.TokenType.RETURN)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            return node;
        }
        else
        {
            System.out.printf("ERROR. Expected 'Return Token' in line %d\n", currentToken.line);            
            System.exit(1);
            return null;
        }
    }
    
    public static Node<Token> block()
    {
        Node<Token> node = getNode(Node.NodeType.blockNode);
        
        if(currentToken.type == Token.TokenType.LBRA)
        {   
            currentToken = iToken.next();
            //System.out.println(currentToken);
            node.children.add(stats());
            //System.out.println(currentToken + "block");
            
            if(currentToken.type == Token.TokenType.RBRA)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
                return node;
            }
            else
            {
                System.out.printf("ERROR. Expected '} Token' in line %d\n", currentToken.line);
                System.exit(1);
                return null;
            }
        }
        else
        {
            System.out.printf("ERROR. Expected '{ Token' in line %d\n", currentToken.line);
            System.exit(1);
        }
        return null;
    }
    
    public static Node<Token> stats()
    {
        Node<Token> node = getNode(Node.NodeType.statsNode);
        
        node.children.add(stat());
        
        node.children.add(mStat());
        
        return node;
    }
    
    public static Node<Token> stat()
    {
        Node<Token> node = getNode(Node.NodeType.statNode);
        
        //System.out.println(currentToken);
        
        switch (currentToken.type) {
            case LBRA:
                node.children.add(block());
                break;
            case IF:
                node.children.add(ifF());
                break;
            case WHILE:
                node.children.add(whileF());
                break;
            case ID:
                node.children.add(assign());
                break;
            case INTEGER:
                node.children.add(declaration());
                break;
            default:
                break;
        }
        
        return node;
    }
    
    public static Node<Token> mStat()
    {
        Node<Token> node = getNode(Node.NodeType.mStatNode);
        
        node.children.add(stat());
        if(node.children.get(0).children.isEmpty() && node.children.get(0).data.isEmpty())
        {
            return node;
        }
        node.children.add(mStat());
        return node;
    }
    
    public static Node<Token> ifF()
    {
        
        //if 	(	<expr> 	<LO> 	<expr>	) 	<block>
        
        Node node = getNode(Node.NodeType.ifNode);
        
        if(currentToken.type == Token.TokenType.IF)
        {            
            currentToken = iToken.next();
            //System.out.println(currentToken);
            if(currentToken.type == Token.TokenType.LPAREN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected '( token' at line %d", currentToken.line);
                System.exit(1);
            }

                node.children.add(expr());
                node.children.add(lOperator());
                node.children.add(expr());
                
            if(currentToken.type == Token.TokenType.RPAREN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected ') token' at line %d", currentToken.line);
                System.exit(1);
            }
            node.children.add(block());
            return node;
        }
        return null;
    }
    
    public static Node<Token> whileF()
    {
        //while 	( 	<expr> 	<LO> 	<expr>	 )	 <block>
        
        Node node = getNode(Node.NodeType.whileNode);
        
        if(currentToken.type == Token.TokenType.WHILE)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            
            if(currentToken.type == Token.TokenType.LPAREN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected '( token' at line %d", currentToken.line);
                System.exit(1);
            }

            node.children.add(expr());
            node.children.add(lOperator());
            node.children.add(expr());
            
            if(currentToken.type == Token.TokenType.RPAREN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected ') token' at line %d", currentToken.line);
                System.exit(1);
            }
            node.children.add(block());
            return node;
        }
        return null;
    }
    
    public static Node<Token> assign()
    {
        //ID		=	<expr> ;
        Node<Token> node = getNode(Node.NodeType.assignNode);
        
        if(currentToken.type == Token.TokenType.ID)
        {
            node.data.add(currentToken);
            currentToken = iToken.next();
            //System.out.println(currentToken);
            
            if(currentToken.type == Token.TokenType.ASSIGN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected '= Token' at line %d", currentToken.line);
                System.exit(1);
            }
            
            node.children.add(expr());
            
            if(currentToken.type == Token.TokenType.SEMICOLON)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected '; Token' at line %d", currentToken.line);
                System.exit(1);
            }
            return node;
        }
        else
        {
            System.out.printf("ERROR. Expected 'ID Token' at line %d", currentToken.line);
            System.exit(1);
        }
        return null;
    }
    
    public static Node<Token> declaration()
    {
        Node<Token> node = getNode(Node.NodeType.assignNode);
        
        if(currentToken.type == Token.TokenType.INTEGER)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            
            if(currentToken.type == Token.TokenType.ID)
            {
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected 'ID Token' at line %d", currentToken.line);
                System.exit(1);
            }
                        
            if(currentToken.type == Token.TokenType.SEMICOLON)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected '; Token' at line %d", currentToken.line);
                System.exit(1);
            }
            return node;
        }
        else
        {
            System.out.printf("ERROR. Expected 'INTEGER Token' at line %d", currentToken.line);
            System.exit(1);
        }
        return null;
    } 
    
    public static Node<Token> expr(){
        //<expr>    --> 	<T> * <expr> | <T> / <expr> | <T>
        
        Node<Token> node = getNode(Node.NodeType.exprNode);
        
        node.children.add(t());
        
        if(currentToken.type == Token.TokenType.MULT)
        {
            currentToken = iToken.next();
            node.children.add(expr());
            //System.out.println(currentToken);
        }
        else if(currentToken.type == Token.TokenType.DIV)
        {
            currentToken = iToken.next();
            node.children.add(expr());
            //System.out.println(currentToken);
        }
        
        return node;
    }
    
    public static Node<Token> lOperator(){
        
        Node<Token> node = getNode(Node.NodeType.loNode);
        
        switch(currentToken.type)
        {
            case EQ:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
            case DIFF:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
            case GEQT:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
            case GT:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
            case LEQT:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
            case LT:
                node.data.add(currentToken);
                currentToken = iToken.next();
                //System.out.println(currentToken);
                break;
        }
        return node;
    }
    
    public static Node<Token> t(){
        //<F> 	+	 <T>	 |	 <F>	 -	 <T>	 |	 <F>
        
        Node<Token> node = getNode(Node.NodeType.tNode);
        
        node.children.add(f());
        
        if(currentToken.type == Token.TokenType.ADD)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            node.children.add(t());
            
        }
        else if(currentToken.type == Token.TokenType.SUB)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            node.children.add(t());
        }
        
        return node;
    }
    
    public static Node<Token> f(){
        //( <expr> ) 	|	 ID	 |	 NUMBER
        
        Node<Token> node = getNode(Node.NodeType.fNode);
        
        if(currentToken.type == Token.TokenType.LPAREN)
        {
            currentToken = iToken.next();
            //System.out.println(currentToken);
            
            node.children.add(expr());
            
            if(currentToken.type == Token.TokenType.RPAREN)
            {
                currentToken = iToken.next();
                //System.out.println(currentToken);
            }
            else
            {
                System.out.printf("ERROR. Expected ') Token' at line %d", currentToken.line);
                System.exit(1);
            }
        }
        else if(currentToken.type == Token.TokenType.ID)
        {
            node.data.add(currentToken);
            currentToken = iToken.next();
            //System.out.println(currentToken);
        }
        else if(currentToken.type == Token.TokenType.NUMBER)
        {
            node.data.add(currentToken);
            currentToken = iToken.next();
            //System.out.println(currentToken);
        }
        
        return node;
    }
    
    public static Node<Token> getNode(Node.NodeType nodeType)
    {
        //System.out.println(nodeType);
        return new Node<Token>(nodeType);
    }
    
    public static void printTree(Node<Token> node, int t, int s){
        
        for(int i=0; i<=t; i++){
            System.out.print("\t");
        }
        
        System.out.println(node.nodeType);
        
        t+=s;
        
        if(!node.data.isEmpty())
        {
           for(Token tk : node.data)
           {
               for(int i=0; i<=t; i++)
               {
                   System.out.print("\t");
               }
               System.out.println(tk);
           }
        }
        
        if(!node.children.isEmpty())
        {
            for(Node<Token> nd : node.children)
           {
               printTree(nd, t,1);
           }
        }
        for(int i=0; i<t; i++)
        {
            System.out.print("\t");
        }
        System.out.println(node.nodeType);
    }
}