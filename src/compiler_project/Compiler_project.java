/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler_project;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author lenovo
 */
public class Compiler_project
{
    static String numbers = "1234567890";
    static String[][] arr = { {"Pattern", "Class"},     
                   {"DerivedFrom", "Inheretance"},
                   {"TrueFor-Else", "Condition"},
                   {"Ity", "Integer"},
                   {"Sity", "SInteger"},
                   {"Cwq", "Character"},
                   {"CwqSequence", "String"},
                   {"Ifity", "Float"},
                   {"Sifity", "SFloat"},
                   {"Valueless", "Void"},
                   {"Logical", "Boolean"},
                   {"BreakFromThis", "Break"},
                   {"Whatever", "Loop"},
                   {"Respondwith", "Return"},
                   {"Srap", "Struct"},
                   {"Scan-Conditionof", "Switch"},
                   
                   {"@", "Stat Symbol"},
                   {"$", "End Symbo"},
                   
                   {"!=", "Relational operation"},
                   {"<=", "Relational operation"},
                   {">=", "Relationa operation"},
                   {"<", "Relationa operation"},
                   {"==", "Relationa operation"},
                   {">", "Relationa operation"},
                   {"=", "Assignement operation"},
                   {"Require","Inclusion"},
                   
                   {"{", "Braces"},
                   {"}", "Braces"},
                   {"[", "Braces"},
                   {"]", "Braces"},
                   {"'", "Quotation Mark"},
                   {"~", "Logic operators"},
                   {"#", "Token Delimiter"},
                   {"^", "Line Delimiter"},
                   
                   {"+", "Arithmetic Operation"},
                   {"*", "Arithmetic Operation"},
                   {"/", "Arithmetic Operation"},
                   {"-", "Arithmetic Operation"},
                   
                   {"||", "Logic operators"},
                   {"&&", "Logic operators"},
                   {"-/", "Comment"},
                   {"/-", "Comment"}, //actually we dont need to put this in the table, as when the comment begins we print that
                   {"--", "Comment"}, //actually we dont need to put this in the table, as when the comment begins we print that
                   {"->", "Access Operator"}};
    
    static int pointer = 0; //this to pass over each character
    static String lexeme = "";
    static int state = 0;
    static int NoLine = 1;
    static int LexemeNo = 1;
    static int _flag = 0; // this is a flag to check if the word has (-) symbol in it or not
    static char c;
    static int NoErrors = 0;
    static boolean FromFile = true;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException
    {   
        int x; 
        //list to collect all the characters in it, ignoring white spaces and \n
        List<Character> characters = new ArrayList<>(); 
        FileReader inputStream = null;
        try
        {
            if(FromFile)
            {
                System.out.println("Reading from file");
                inputStream  = new FileReader("SourceCode.txt");
                //This loop to add each character to the list
                while((x = inputStream.read()) != -1)
                {
                    if((char)x != ' ' && (char)x != '\r' && (char)x != '\t')
                        characters.add((char)x); 
                }
            }
            else
            {
                System.out.println("Reading from text");
                //if we want to read from a text field
                //add to the (characters) list
            }
            
            //The main loop 
            while(pointer < characters.size())
            {
                c = characters.get(pointer); 
                if(c == '\n')
                    NoLine++;
                
                switch(state)
                {
                    case(0): // in this case we check for all possible beginning character
                    {
                        lexeme = lexeme + c;
                        
                        if ((Is_char(c)) || c == '_')// means that the character is (letter) or ( _ )
                            state = 1;
                        
                        else if(Is_num(c))
                            state = 5;
                        
                        else if(c=='[' || c==']' || c=='{'||c=='}' || c=='~' || c=='#' || c=='^' || c=='+' || c=='*' || c=='$' || c=='@' || c=='"')
                            state = 6;
                        
                        else if(c == '<' || c == '>' || c == '=' || c == '!')
                            state = 7;
                        
                        else if(c == '|')
                            state = 16;
                        
                        else if(c == '&')
                            state = 17;

                        else if(c == '/')
                            state = 9;
                        
                        else if(c == '-')
                            state = 12;
                        
                        else
                        {
                            if(c != '\n')
                            {
                                System.out.println(c + ": is an Invalid input in line " + NoLine+ " ,LexemeNo: " + LexemeNo);
                                NoErrors++;
                            }
                            lexeme = "";
                            LexemeNo++;
                        }
                    }
                    break;

                    case(1): //check for an identifier or a keyword
                    {
                        if((Is_char(c)) || c == '_') 
                            lexeme = lexeme + c; //and keep the state = 1 to enter here again
                            // here i do not need to use the advance method because it is a loop until i get different char
                        else if(Is_num(c))
                        {
                            lexeme = lexeme + c;
                            state = 4;
                        }
                        else if(c == '-')
                        {
                            _flag = 1;
                            lexeme = lexeme + c;
                            state = 2;
                        }
                        else
                            done(); // search for the keyword and make the state = 0 again
                    }
                    break;
                    
                    case(2): // the lexeme has (letters and -), and checks for the rest of letters after (-)
                    {
                        if(Is_char(c))
                            lexeme = lexeme + c;
                        
                        else
                            done();
                    }
                    break;
                    
                    case(4): // the lexeme has letters and digit, and checks for any other letters and digits
                    {
                        if(Is_char(c) || Is_num(c))
                            lexeme = lexeme + c;
                        
                        else
                            done();
                    }
                    break;
                    
                    case(5): //check for any combination of numbers
                    {
                        if(Is_num(c))
                            lexeme = lexeme + c; // we do not need the advance method here because it is a loop
                        
                        else
                            done();
                    }
                    break;
                    
                    case(6): //check for any single symbols
                    {
                        done(); // here we go to check directly because this state has only one character
                    }
                    break;
                    
                    case(7): // the lexeme has (< or > or =), and checks for any relational operators
                    {
                        if(c == '=')
                            Advance_input(c);

                        done(); 
                    }
                    break;

                    case(9): // the lexeme has (/)
                    {
                        if(c == '-')
                        {
                            lexeme = lexeme + c;
                            state = 10;
                            // printing here because in CheckForToken() method the state resets again, and we dont want that now
                            System.out.println(lexeme + ": is Comment," + " ,NoLine: " + NoLine+ " ,LexemeNo: "+LexemeNo);
                            LexemeNo ++;
                        }
                        else
                            done();
                    }
                    break;
                    
                    case(10): //the lexeme has (/-) as a starting point to comment some lines
                    {
                        lexeme = "";
                        if(c == '-')
                        {
                            lexeme = lexeme + c;
                            state = 11;
                        }
                    }
                    break;
                    
                    case(11): //the lexeme has (-) and waiting for (/) to end the comment
                    {
                        if(c == '/')
                        {
                            Advance_input(c); //this method increases the pointer by 1 after ((lexeme+=c) statement
                            done();
                        }
                        else
                            state = 10;
                    }
                    break;
                    
                    case(12): // lexeme has (-)
                    {
                        if(c == '-')
                        {
                            lexeme = lexeme + c;
                            state = 14;
                            // printing here because in CheckForToken() method the state resets again, and we dont want that now
                            System.out.println(lexeme + ": is Comment," + " ,NoLine: " + NoLine+ " ,LexemeNo: " + LexemeNo);
                            LexemeNo ++;
                        }
                        else
                        {
                            if(c == '>')
                                Advance_input(c);
                            
                            done();
                        }
                    }
                    break;

                    case(14):// lexeme has (--) and waiting for (\n) to end the comment
                    {
                        lexeme = "";
                        if(c == '\n')
                        {
                            state = 0;
                            lexeme = "";
                        }
                    }
                    break;
                    
                    case(16): // the lexeme has (|), and checks for logic operator (||)
                    {
                        if(c == '|')
                            Advance_input(c);

                        done();
                    }
                    break;
                    
                    case(17): // the lexeme has (&), and checks for logic operator (&&)
                    {
                        if(c == '&')
                            Advance_input(c); 
                        
                        done();
                    }
                    break;
                }
                if(c == '\n')
                    LexemeNo = 1; // reset the lexeme number with each new line
                
                pointer++; // the iteration of each loop to get the next character
            }
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }

        //this is to handle tha last lexeme
        if(lexeme.length() > 0)
        {
            checkForToken(lexeme);
        }
        System.out.println("Number of errors is: " + NoErrors);
}   
    
    //method to search for the keywords & symbols
    static void checkForToken(String lexeme)
    {
        if(c == '\n')
           NoLine--; 
            
        pointer--; // To move backward one character to avoid missing the next character
        state = 0; // To back again to start state
        for(int i = 0; i < arr.length; i++)
        {
            if(lexeme.equals(arr[i][0]))
            {
                System.out.println(lexeme + ": " + arr[i][1] + " ,NoLine " + NoLine + " ,LexemeNo: " + LexemeNo);
                LexemeNo++;
                if(_flag == 1) //this to reset the (-)flag again
                    _flag = 0;
                return;
            }
        }
        if((Is_char(lexeme.charAt(0)) || lexeme.charAt(0) == '_') && _flag != 1)
        {
            System.out.println(lexeme + ": is an IDENTIFIER," + " NoLine: " + NoLine + ", LexemeNo: " + LexemeNo);
        }
        else if(Is_num(lexeme.charAt(0)))
            System.out.println(lexeme + ": is Constant," + " NoLine: " + NoLine + ", LexemeNo: " + LexemeNo);
        
        else
        {
            System.out.println(lexeme + ": is INVALID," + " NoLine: " + NoLine + ", LexemeNo: " + LexemeNo);
            NoErrors++;
        }
 
        if(_flag == 1) //this to reset the (-)flag again
            _flag = 0;
        
        LexemeNo++;
    }
    
    //method to check if the character is an alpha or not
    static boolean Is_char(char c)
    {
        if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            return true;
        else 
            return false;
    }
    
    static boolean Is_num(char c)
    {
        for(int i = 0; i < 10; i++)
        {
            if(c == numbers.charAt(i))
                return true;
        }
        return false;
    }
    
    // this method is used when we guarantee that we in an accept state, and there is no loop. so, we increase the pointer by 1
    static void Advance_input(char c) 
    {
       lexeme = lexeme + c;
       pointer++;
    }
    
    static void done()
    {
       checkForToken(lexeme);
       lexeme = "";
    }
}
