package util;

import java.util.HashMap;
import java.util.Map;
import manager.session.SessionController;

/**
 *
 * @author Admin
 */
public class ContextManager {

    /**
     * el mapeo que tendra todos los contextos guardados
     */
    public static Map<Integer,SessionController> variableMap;
    /**
     * el contexto actual
     */
    public static int actualContext=0;
    
    public static void cleanMap(){
        if(variableMap==null){
            variableMap=new HashMap<Integer, SessionController>();
        }
        variableMap.clear();
        actualContext=0;
    }

    public static void addSessionController(SessionController sessionController) {
        if(variableMap==null){
            variableMap=new HashMap<Integer, SessionController>();
        }
        actualContext++;
        variableMap.put(actualContext, sessionController);
    }
    
    public static SessionController getSessionController(int context){
        return variableMap.get(context);
    }
}
