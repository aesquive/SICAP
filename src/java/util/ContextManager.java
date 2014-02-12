package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import manager.session.SessionController;

/**
 *
 * @author Admin
 */
public class ContextManager {

    /**
     * el mapeo que tendra todos los contextos guardados
     */
    public  Map<Integer, SessionController> variableMap;
    /**
     * el contexto actual
     */
    public  int actualContext = 0;

    public  void cleanMap() {
        if (variableMap == null) {
            variableMap = new HashMap<Integer, SessionController>();
        }
        variableMap.clear();
        actualContext = 0;
    }

    public  void addSessionController(SessionController sessionController) {
        if (variableMap == null) {
            variableMap = new HashMap<Integer, SessionController>();
        }
        actualContext++;
        variableMap.put(actualContext, sessionController);
    }

    public  SessionController getSessionController(int context) {
        return variableMap.get(context);
    }
    
}
