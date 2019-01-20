/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

/**
 *
 * @author Arkai Ariza
 */
public class Token {
    public enum TokenType {
        EQ("=="), LEQT("<="), GEQT(">="), DIFF("<>"), LT("<"), GT(">"),  
        MULT("[*]"), DIV("[/]"), ADD("[+]"), SUB("[-]"), 
        LPAREN("\\("), RPAREN("\\)"), LBRA("\\{"), RBRA("\\}"), LSBRA("\\["), RSBRA("\\]"),
        COMMA(","), PEROID("\\."), ASSIGN("="), SEMICOLON(";"), 
        WHILE("while"), IF("if"), ELSE("else"), COMMENT("//"), 
        INTEGER("int"),
        PUBLIC("public"), PRIVATE("private"), PACKAGE("package"), IMPORT("import"), ENUM("enum"), 
        NUMBER("\\d+"), ID("\\w+"), SKIP("[\\s+\\t]*"),
        INVALID(".*");

        public final String pattern;

        private TokenType(String pattern) {
            this.pattern = pattern;
        }
    }

    public TokenType type;
    public String data;

    public Token(TokenType type, String data) {
      this.type = type;
      this.data = data;
    }

    @Override
    public String toString() {
      return String.format("[ %s, %s ]", type.name(), this.data);
    }
}