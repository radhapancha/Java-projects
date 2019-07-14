/*/////////////////////////////////////////////////////////
 // Radha Panchap
 // Calculator program
 //
 */ ////////////////////////////////////////////////////////
import java.util.Stack;
import java.util.Scanner;

public class Calculator {
 // all operators -- for now -- as a string
 private static String opsString = "()+-/*";

 // result of applying binary operator op to two operands val1 and val2
 public static double eval(String op, double val1, double val2) {
  if (op.equals("+")) return val1 + val2;
  if (op.equals("-")) return val1 - val2;
  if (op.equals("/")) {
   if (val1 / val2 == Double.POSITIVE_INFINITY || val1 / val2 == Double.NEGATIVE_INFINITY) {
    throw new ArithmeticException("Divide by 0 exception");
   } else
    return val1 / val2;
  }
  if (op.equals("*")) return val1 * val2;
  throw new RuntimeException("Invalid operator");
 }

 // put spaces around operators to simplify tokenizing
 public static String separateOps(String in ) {
  for (int i = 0; i < opsString.length(); i++) {
   char c = opsString.charAt(i); in = in .replace(Character.toString(c), " " + c + " ");
  }
  return in.trim(); // remove leading and trailing spaces
 }

 public static int precedence(String op) {
  // operator precedence: "(" ")" << "+" "-" << "*" "/"
  return opsString.indexOf(op) / 2;
 }

 public static Double evaluate(String[] tokens, BST < String, Double > b) {
  // Edsger Dijkstra's shunting-yard (two-stack) algorithm
  Stack < String > ops = new Stack < String > ();
  Stack < Double > vals = new Stack < Double > ();
  for (String s: tokens) { // I iterate through tokens using a string
   if (!opsString.contains(s)) {
    if (Parser.alphanum(s) == true) { // if isVar true, the value of b is put onto stack
     Double t = b.get(s);
     if (t == null)
      throw new RuntimeException("Undefined Variable Name");
     else
      vals.push(t);
    }
    if (Parser.num(s) == true)
     vals.push(Double.parseDouble(s));
    continue;
   }

   // token is an operator
   while (true) {
    // the last condition ensures that the operator with
    // higher precedence is evaluated first
    if (ops.isEmpty() || s.equals("(") ||
     (precedence(s) > precedence(ops.peek()))) {
     ops.push(s);
     break;
    }
    // evaluate expression
    String op = ops.pop();
    // ignore left parentheses
    if (op.equals("("))
     break;
    else {
     // evaluate operator and two operands;
     // push result to value stack
     double val2 = vals.pop();
     double val1 = vals.pop();
     vals.push(eval(op, val1, val2));
    }
   }
  }

  // evaluate operator and operands remaining on two stacks
  while (!ops.isEmpty()) {
   String op = ops.pop();
   double val2 = vals.pop();
   double val1 = vals.pop();
   vals.push(eval(op, val1, val2));
  }
  // last value on stack is value of expression
  return vals.pop();
 }


 public static void main(String[] args) {

  Scanner input = new Scanner(System.in);

  BST < String, Double > varBST = new BST < String, Double > ();
  BST < String, AbstractSyntax > functionBST = new BST < String, AbstractSyntax > ();

  System.out.print("> ");


  while (input.hasNext()) {

   String ln = input.nextLine();
   String[] tokens = separateOps(ln).split("\\s+");


   try {
    Parser p = new Parser(ln); // parser to parse through user input

   AbstractSyntax a = p.parse();

    if (a.getType().equals("eval")) // checks to see if the type is eval, and if so, it evaluates it
     System.out.println(evaluate(a.getExp(), varBST));
    else if (a.getType().equals("assign")) // checks to see if type is assign
    {
     if (p.alphanum(a.getName()) == true) // method alphanum checks if variable name is valid
     {
      Double t = evaluate(a.getExp(), varBST); // double t is the double that results when the expression of a is evaluated with varBST
      varBST.put(a.getName(), t); // the double is put into varBST along with the string that results from a.getName()
     }
    } else if (a.getType().equals("def")) // if the type that results from a.getType() equals def, then the function and abstract syntax is put in functionBST
    {
     functionBST.put(a.getName(), a);
    } else if (a.getType().equals("call")) { // if the type that results from a.getType() equals call
     BST < String, Double > temp = new BST < String, Double > ();
     AbstractSyntax b = functionBST.get((a.getName()));
     if (b == null) {
      throw new IllegalArgumentException("Function not defined"); // this if statement throws an exception if function is not defined.
     }
     if (a.getParams().length != b.getParams().length) {
      throw new IllegalArgumentException("Parameters of user input do not match parameters of called function"); // this if statement throws an IllegalArgumentException if the parameters of user input do not match those of called function
     }

     if (a.getParams().length == b.getParams().length) { // checks to see that the parameters are of the same length
      String[] parameters = b.getParams(); // variables and numbers;  retrieves parameters of b call
      String[] param2 = a.getParams(); // retrieves parameters of a call
      String[] s = new String[1]; // initializes new array of size 1
      for (int k = 0; k < parameters.length; k++) {
       s[0] = param2[k]; // the a is set to s[0]
       temp.put(parameters[k], evaluate(s, varBST)); // parameters[k], and the double that results from evaluate(s,varBST) is put onto temp stack

      }
      System.out.println(evaluate(b.getExp(), temp));
     }
    }
   } catch (Exception e) { // catches any of the exceptions
    System.out.println(e.getMessage());
   }
  }
 }


}
