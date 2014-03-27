
package interpreter;

import java.util.Map;

/**
 *
 * @author Admin
 */
public class MathInterpreter {
    
    
    public static String interp(String expr){
        String[] data=getDataExpr(expr);
        return Symbol.interp(data[0],data[1], data[2]);
    }

    private static String[] getDataExpr(String expr) {
        int numeroParentesis=0;
        int parentesisAbren=0;
        int parentesisCierran=0;
        String param1=null;
        String param2=null;
        String symbol=null;
        for(int t=0;t<expr.length();t++){
            if(numeroParentesis>1 && parentesisAbren==parentesisCierran){
                symbol=String.valueOf(expr.charAt(t-1));
                param1=expr.substring(1, t-2);
                param2=expr.substring(t+1, expr.length()-1);
            }
            if(String.valueOf(expr.charAt(t)).equals("(")){
                numeroParentesis++;
                parentesisAbren++;
            }
            if(String.valueOf(expr.charAt(t)).equals(")")){
                numeroParentesis++;
                parentesisCierran++;
            }
        }
        return new String[]{symbol,param1,param2};
    }
    
    public static void main(String[] args) {
        String cad="((((\"5\")-(\"2\"))+((\"2\")*(\"3\")))+(\"1\"))/(\"2\")";
        System.out.println(cad);
        System.out.println(MathInterpreter.interp(cad));
    }
}
