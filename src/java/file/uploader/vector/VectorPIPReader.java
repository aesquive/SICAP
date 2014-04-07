package file.uploader.vector;

import db.controller.DAO;
import db.pojos.Calificacion;
import db.pojos.Tipotasa;
import db.pojos.Vector;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import util.Reflector;

/**
 *
 * @author Alberto Emmanuel Esquivel Vega
 *
 * Carga el vector de PIP a la Base de datos
 */
public class VectorPIPReader extends VectorReader {

    public static boolean saveVector(List<List<String>> data, int PIPVALMER) {
        Map<String, Calificacion> dataCalif = new HashMap<String, Calificacion>();
        Map<String, Tipotasa> dataTasa = new HashMap<String, Tipotasa>();
        List<Calificacion> calificaciones = DAO.createQuery(Calificacion.class, null);
        List<Tipotasa> tipoTasa = DAO.createQuery(Tipotasa.class, null);
        for (Calificacion c : calificaciones) {
            dataCalif.put(c.getCalificacion(), c);
        }
        for (Tipotasa tts : tipoTasa) {
            dataTasa.put(tts.getDesTipoTasa(), tts);
        }
        String[] cols = PIPVALMER == 0 ? PIPCOLUMNS : VALMERCOLUMNS;
        String[] methods = PIPVALMER == 0 ? PIPMETHODS : VALMERMETHODS;
        for (List<String> row : data) {
            System.out.println("recorriendo linea");
            Vector vector = new Vector();
            for (int t = 0; t < cols.length; t++) {
                String dataType = cols[t].substring(cols[t].length() - 1, cols[t].length());
                Object obj = getValue(cols[t].substring(0, cols[t].length() - 1), dataType, row, dataCalif, dataTasa);
                System.out.println(obj+" method = "+methods[t]);
                if (obj != null) {
                    Reflector.callMethod(vector, new Object[]{obj}, methods[t]);
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        VectorPIPReader.saveVector(VectorPIPReader.getData(new File("ejemplo.csv")), 0);
    }

}
