/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class UserManager {
    
    private static Map<Integer,ContextManager> userMap=new HashMap<Integer, ContextManager>();
    
    public static ContextManager addUserContext(int user){
        ContextManager contextManager=new ContextManager();
        userMap.put(user, contextManager);
        return contextManager;
    }
    
    public static ContextManager getContextManager(int user){
        return userMap.get(user);
    }
    
    public static void clearContext(int user){
        userMap.remove(user);
    }
    
}
