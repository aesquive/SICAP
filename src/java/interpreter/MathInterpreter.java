package interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 *Es un interprete de operaciones basicas de 2 factores unicamente , es recursivo y sobre cada argumento
 * Soporta
 * + , - ,*,/,^,m (minimo),M (maximo)
 * en caso de querer poner un numero constante va entre ""
 * Ambos argumentos deben ir entre parentesis 
 * 
 * si la variable viene del mapeo debe llevar el caracter =
 * 
 * Recibe un mapeo con nombres de variables por si estas se quieren utilizar durante el calculo  , el mapeo es de <String,Double> , solo evalua expresiones matematicas
 *
 * @author Alberto Emmanuel Esquivel Vega
 */
public class MathInterpreter {
    
    
    /**
     * interpreta cada uno de las expresiones enviadas
     * @param expr
     * @param variableMap
     * @return
     * @throws MathInterpreterException 
     */
    public static String interp(String expr,Map<String,Double> variableMap) throws MathInterpreterException{
        System.out.println("ey"+expr);
        String[] data=getDataExpr(expr);
        return Symbol.interp(data[0],data[1], data[2],variableMap);
    }

    /*
    divide los argumentos de la operacion principal
    */
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
    
    /**
     * Pruebas
     * @param args
     * @throws MathInterpreterException 
     */
    public static void main(String[] args) throws MathInterpreterException {
        String cadOperacionSencilla="((((\"5\")-(\"2\"))+((\"2\")*(\"3\")))+(\"1\"))/(\"2\")";
        System.out.println(cadOperacionSencilla);
        System.out.println(MathInterpreter.interp(cadOperacionSencilla,null));
    
        Map<String,Double> vars=new HashMap<String, Double>();
        vars.put("dAno", 360.0);
        vars.put("dCupon",180.0);
        vars.put("tasa",.08);
        String operacionVars="((=tasa)*(=dCupon))/(=dAno)";
        System.out.println(operacionVars);
        System.out.println(MathInterpreter.interp(operacionVars, vars));
    
        String opM="(=dAno)m(=dCupon)";
        System.out.println(MathInterpreter.interp(opM, vars));
    }
}
