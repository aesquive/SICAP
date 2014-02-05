
package util;

import db.pojos.Cuenta;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que se encarga de realizar refleccion
 * @author zorin
 */
public class Reflector {
    
    /**
     * manda llamar a un metodo sobre un objeto
     * @param target
     * @param args
     * @param methodName
     * @return 
     */
    public static Object callMethod(Object target,Object[] args, String methodName) {
        Method findMethod = findMethod(target.getClass(),methodName);
        if(findMethod==null){
            return null;
        }
        return execute(target,args,findMethod);
    }

    /**
     * encuentra un metodo dando el nombre sobre una clase
     * @param classArg
     * @param methodName
     * @return 
     */
    private static Method findMethod(Class classArg, String methodName) {
        for(Method m:classArg.getDeclaredMethods()){
            if(m.getName().toUpperCase().equals(methodName.toUpperCase())){
                return m;
            }
        }
        return null;
    }

    /**
     * ejecuta el metodo mencionado sobre el objeto target pasandole los argumentos
     * @param target
     * @param args
     * @param findMethod
     * @return 
     */
    private static Object execute(Object target, Object[] args, Method findMethod) {
        try {
            return findMethod.invoke(target, args);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Reflector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;   
        
    }

}
