package interpreter;

/**
 *
 * @author Admin
 */
public class Symbol {

    public static String interp(String sym , String param1 , String param2){
        Double value1=0.0;
        Double value2=0.0;
        System.out.println(sym);
        System.out.println(param1);
        System.out.println(param2);
        if(String.valueOf(param1.charAt(0)).equals("(")){
            value1=Double.parseDouble(MathInterpreter.interp(param1));
        }
        if(String.valueOf(param2.charAt(0)).equals("(")){
            value2=Double.parseDouble(MathInterpreter.interp(param2));
        }
        if(String.valueOf(param1.charAt(0)).equals("\"")){
            value1=Double.parseDouble(param1.replaceAll("\"", ""));
        }
        if(String.valueOf(param2.charAt(0)).equals("\"")){
            value2=Double.parseDouble(param2.replaceAll("\"", ""));
        }
        
        if(sym.equals("+")){
            return String.valueOf(value1+value2);
        }
        if(sym.equals("-")){
            
            return String.valueOf(value1-value2);
        }
        if(sym.equals("*")){
            
            return String.valueOf(value1*value2);
            
        }
        if(sym.equals("^")){
            
            return String.valueOf(Math.pow(value1,value2));
        }
        if(sym.equals("/")){
            
            return String.valueOf(value1/value2);
        }
        return null;
    }
    
}
