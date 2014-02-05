
package manager.session;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que maneja todas las variables de sesion guardandolas en un mapa , 
 * este mapa se reinicia en cada ocasion que se entra a la aplicación
 * @author zorin
 */
public class SessionController {

    
    private static Map<String,Variable> map;
    
    
    /**
     * agrega la variable al mapeo de variables.
     * @param name
     * @param var
     * @param force
     * @return 
     */
    public static boolean addVariable(String name,Variable var , boolean force){
        if(map==null){
            map=new HashMap<String, Variable>();
        }
        if(map.get(name)!=null && !force){
            return false;
        }
        map.put(name, var);
        return true;
    }
    
    /**
     * limpia todas las variables
     */
    public static void cleanMap(){
        if(map==null){
            map=new HashMap<String, Variable>();
        }
        map.clear();
    }

    /**
     * obtiene una variable del mapeo
     * @param name
     * @return 
     */
    public  static Variable getVariable(String name) {
        if(map==null){
            map=new HashMap<String, Variable>();
        }
        return map.get(name);
    }
    
    
}
