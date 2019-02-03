/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Arkai Ariza
 */
public class Lex {
    
    public static void main(String[] args) throws IOException {
        String file = readFile("Test.java");
        ArrayList<Token> myTokens = lex(file);
        
        System.out.println(file);
        /*for(char x : file.toCharArray())
        {
            System.out.println((int)(x));
        }*/
        for (Token tok : myTokens) {
            System.out.println(tok);
        }
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
}