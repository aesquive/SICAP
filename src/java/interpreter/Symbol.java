package interpreter;

import java.util.Map;

/**
 *
 * @author Admin
 */
public class Symbol {

    public static String interp(String sym, String param1, String param2, Map<String, Double> variableMap) throws MathInterpreterException {
        Double value1 = 0.0;
        Double value2 = 0.0;
        if (String.valueOf(param1.charAt(0)).equals("(")) {
            value1 = Double.parseDouble(MathInterpreter.interp(param1, variableMap));
        }
        if (String.valueOf(param2.charAt(0)).equals("(")) {
            value2 = Double.parseDouble(MathInterpreter.interp(param2, variableMap));
        }
        if (String.valueOf(param1.charAt(0)).equals("=")) {
            Double value = variableMap.get(param1.replace("=", ""));
            if (value == null) {
                System.out.println("Not existing variable " + param1.replace("=", ""));
                value1 = 0.0;
            } else {

                value1 = value;
            }
        }
        if (String.valueOf(param2.charAt(0)).equals("=")) {
            Double value = variableMap.get(param2.replace("=", ""));
            if (value == null) {
                System.out.println("Not existing variable " + param1.replace("=", ""));
                value2 = 0.0;
            } else {
                value2 = value;
            }
        }
        if (String.valueOf(param1.charAt(0)).equals("\"")) {
            value1 = Double.parseDouble(param1.replaceAll("\"", ""));
        }
        if (String.valueOf(param2.charAt(0)).equals("\"")) {
            value2 = Double.parseDouble(param2.replaceAll("\"", ""));
        }

        if (sym.equals("+")) {
            return String.valueOf(value1 + value2);
        }
        if (sym.equals("-")) {

            return String.valueOf(value1 - value2);
        }
        if (sym.equals("*")) {

            return String.valueOf(value1 * value2);

        }
        if (sym.equals("^")) {

            return String.valueOf(Math.pow(value1, value2));
        }
        if (sym.equals("/")) {

            return String.valueOf(value1 / value2);
        }
        //minimo
        if (sym.equals("m")) {

            return String.valueOf(Math.min(value1, value2));
        }
        //maximo
        if (sym.equals("M")) {

            return String.valueOf(Math.max(value1, value2));
        }
        //minimo bool
        if (sym.equals("B")) {

            if (value1 < value2) {
                return String.valueOf(Double.valueOf("1.0"));
            } else {
                return String.valueOf(Double.valueOf("0.0"));
            }
        }
        //maximo bool
        if (sym.equals("A")) {

            if (value1 > value2) {
                return String.valueOf(Double.valueOf("1"));
            } else {
                return String.valueOf(Double.valueOf("0"));
            }
        }
        //round
        if (sym.equals("R")) {
            return String.valueOf(Math.round(value1 * Math.pow(10, value2)) / Math.pow(10, value2));
        }
        if(sym.equals("I")) {
            
            if(value1 > value2)
              return String.valueOf(Double.valueOf(value1));
            else
              return String.valueOf(Double.valueOf("0"));
        }
        return null;
    }

}
