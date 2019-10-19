/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;



/**
 * appropriate comment here
 * @author David Gagliardi
 */
public class PrecedenceCalculator extends NoPrecedenceCalculator {
    
    public PrecedenceCalculator(String title) {
        super(title);
    }
    
    public PrecedenceCalculator() {
        this("Calculator With Operator Precedence");
    }
    
    @Override
    protected void number() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsEOF()) {
            reduce();
            setState(State.END);
        } else if (getDispenser().tokenIsOperator()) {
            setState(State.OPERATOR);
        } else {
            syntaxError(OP_OR_END);
        }
    }
    
    @Override
    public void reduce() {
        char op1, op2;        
        int i = getStack().size();
        
        boolean reducible = true;
    
        while (numOpNumOnStack()) {             
            while (reducible) {
                if (getStack().size() > 3) {
                    op1 = (Character)getStack().get(i-2);
                    op2 = (Character)getStack().get(i-4);
                    
                    if (((op1 == '+' || op1 == '-') && (op2 == '*' || op2 == '/'))
                            || (op1 == '+' && op2 == '-')
                            || (op1 == '*' && op2 == '/')) {
                                double oldOperand = (Double)getStack().pop();
                                char oldOperator = (Character)getStack().pop();
                                
                                if (numOpNumOnStack()) {
                            reduceNumOpNum();
                        }
                                getStack().push(oldOperator);
                                getStack().push(oldOperand);
                    } else {
                        
                        if (numOpNumOnStack()) {
                            reduceNumOpNum();
                        }
                      }
                    } else if (getStack().size() == 3) {
                                        
                    if (numOpNumOnStack()) {
                       reduceNumOpNum();
                    }
                
                    reducible = false;                    
                } else {
                    syntaxError(OP_OR_END);
                }
                
                i = getStack().size();
            }
        
        }
    }
        
        
        
        
        
        
        
        
        
        
        
        
        
    }