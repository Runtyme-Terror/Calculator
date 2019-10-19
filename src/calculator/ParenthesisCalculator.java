/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import java.util.Stack;

/**
 * appropriate comment here
 * @author David Gagliardi
 */
public class ParenthesisCalculator extends PrecedenceCalculator {
    
    public ParenthesisCalculator(String title) {
        super(title);
    }
    
    public ParenthesisCalculator() {
        this("Calculator With Operator Precedence and Parentheses");
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
        } else if (getDispenser().tokenIsLeftParen()) {
            syntaxError("operator or right parenthesis");
        } else if (getDispenser().tokenIsRightParen()) {
            reduce();
            setState(State.RIGHT_PAREN);
        } else {
            syntaxError(OP_OR_END);
        }
    }
    
    @Override
    protected void operator() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsOperator() || getDispenser().tokenIsRightParen()) {
            syntaxError(NUM + " or left parenthesis");
        } else if (getDispenser().tokenIsNumber()) {
            setState(State.NUMBER);
        } else {
        setState(State.LEFT_PAREN);
        }
    }
    
    protected void left_paren() {
        shift();
        getDispenser().advance();
        if (!getDispenser().tokenIsNumber()) {
            syntaxError(NUM);
        }
        setState(State.NUMBER);
    }
    
    protected void right_paren() {
        shift();
        getDispenser().advance();
        if (getDispenser().tokenIsEOF()) {
            reduce();
            setState(State.END);
        } else if (getDispenser().tokenIsOperator()) {            
            setState(State.OPERATOR);
        } else if (getDispenser().tokenIsLeftParen()) {
            syntaxError(OP_OR_END);
        } else if (getDispenser().tokenIsRightParen()) {
            syntaxError(OP_OR_END);
        } else {
            syntaxError(OP_OR_END);
        }
    }

    
    @Override
    public double evaluate() {
	setState(State.START);
        while (true) {
            switch (getState()) {
                case START:
                    start();
                    break;
                case NUMBER:
                    number();
                    break;
                case OPERATOR:
                    operator();
                    break;
                case LEFT_PAREN:
                    left_paren();
                    break;
                case RIGHT_PAREN:
                    right_paren();
                    break;
                case END:
                    end();
                    return (Double) getStack().pop();
                default:
                    throw new Error("Something is wrong in ParenthesisCalculator.evaluate"); // shouldn't happen
            }
        }
    }
    
        public boolean numOpNumOnStack(Stack<Object> tempStack) {  // check if NUM OP NUM is on stack
        if (tempStack.size() < 3) {
            return false;
        }
        Object item = tempStack.get(tempStack.size()-2);
        if (item.getClass() != Character.class) {
            return false;
        }
        char ch = (Character)item;
        return ch == '+' ||
               ch == '-' ||
               ch == '*' ||
               ch == '/';
    }
        
    public double reduceNumOpNum(Stack<Object> tempStack) {
        double operand2 = (Double)tempStack.pop();
        char operator = (Character)tempStack.pop();
        double operand1 = (Double)tempStack.pop();
        tempStack.push(operate(operator, operand1, operand2));
        double answer = (Double)tempStack.pop();
        
        return answer;
    }

    @Override
    public void reduce() {
        Stack<Object> tempStack = new Stack<>();
        Stack<Object> tempStack2 = new Stack<>();
        double value;
    
        if (getDispenser().tokenIsRightParen()) {
            getStack().pop();
            int j = getStack().size();
            
            while ((Character)getStack().get(j-1) != '(') {
                tempStack.push(getStack().pop());
            }
            getStack().pop();
            
            for (int k = tempStack.size(); (k-1) >= 0; --k) {
                tempStack2.push(tempStack.pop()); 
            }
            
            if (numOpNumOnStack(tempStack2)) {
                value = reduceNumOpNum(tempStack2);
                getStack().push(value);
            }  
            
        }
        
        if (getDispenser().tokenIsEOF()) {
            
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
}