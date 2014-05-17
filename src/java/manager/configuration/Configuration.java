package manager.configuration;

import db.controller.DAO;
import db.pojos.Configuracion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class Configuration {

    private static Map<String,String> configuration;
    
    private  static void readConfigurationTable(){
        List<Configuracion> createQuery = DAO.createQuery(Configuracion.class, null);
        init();
        for(Configuracion conf:createQuery){
            configuration.put(conf.getDesConfiguracion(),conf.getValor());
        }
    }

    public static String getValue(String clave){
        if(configuration==null){
            readConfigurationTable();
        }
        return configuration.get(clave);
    }
    
    public static void reset(){
        configuration=null;
        readConfigurationTable();
    }
    
    private static void init() {
        if(configuration==null){
            configuration=new HashMap<String, String>();
        }
    }
    
}
