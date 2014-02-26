package file.uploader.rc;

import db.controller.DAO;
import db.pojos.Catalogocuenta;
import db.pojos.Cuenta;
import db.pojos.Moneda;
import db.pojos.Regcuenta;
import file.uploader.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Admin
 */
public class RC0Reader {

    public static String DELIMITER = ",";
    public static String[] SAVING_ORDER = new String[]{"CatalogoCuenta", "", "Moneda", "Valor"};
    private String message;
    private Map<String, Catalogocuenta> cuentas;
    private Map<String, Moneda> monedas;

    public RC0Reader() {
        message = "";
        this.cuentas = createMap(DAO.createQuery(Catalogocuenta.class, null));
        this.monedas = createMapMon(DAO.createQuery(Moneda.class, null));

    }

    /**
     * los resultados que da son 0 - nos dice si se realizo correctamente la
     * lectura y escritura (0 error , 1 ok) 1 - nos dice cuantos registros se
     * leyeron 2 - nos dice cuantos registros se escribieron
     *
     * @param fileName
     * @return
     */
    public int[] loadFile(String fileName) {
        int[] results = new int[3];
        try {
            message = "";
            String[][] readFile = FileReader.readFile(fileName, DELIMITER);
            results[1] = readFile.length;
            results[2] = saveValues(readFile);
            results[0] = 1;
        } catch (IOException ex) {
            results[0] = 0;
        }
        return results;
    }

    /**
     * guarda los valores dentro de la base de datos
     *
     * @param readFile
     * @return
     */
    private int saveValues(String[][] values) {
        int complete = 0;
        for (int t = 0; t < values.length; t++) {
            try {
                Catalogocuenta cat = cuentas.get(values[t][0]);
                Moneda mon = monedas.get(values[t][2]);
                Double value = Double.parseDouble(values[t][3]);
                Regcuenta reg = (Regcuenta) DAO.createQuery(Regcuenta.class, new Criterion[]{Restrictions.like("idregCuenta", 1)}).get(0);
                Cuenta cuenta = new Cuenta(mon, reg, cat, value, "", 0);
                DAO.save(cuenta);
                complete++;
            } catch (Exception e) {
                message = message.equals("") ? "Error reg:" + values[t][0] : message + "," + values[t][0];
            }
        }
        return complete;
    }

    private Map<String, Catalogocuenta> createMap(List<Catalogocuenta> createQuery) {
        Map<String, Catalogocuenta> cuentas = new HashMap<String, Catalogocuenta>();
        for (Catalogocuenta c : createQuery) {
            cuentas.put(c.getIdCatalogoCuenta().toString(), c);
        }
        return cuentas;
    }

    private Map<String, Moneda> createMapMon(List<Moneda> createQuery) {
        Map<String, Moneda> cuentas = new HashMap<String, Moneda>();
        for (Moneda c : createQuery) {
            cuentas.put(c.getIdMoneda().toString(), c);
        }
        return cuentas;
    }
    
    public static void main(String[] args) {
        RC0Reader rc=new RC0Reader();
        int[] loadFile = rc.loadFile("r01/R01.txt");
        for(int t:loadFile){
            System.out.println(t);
        }
    }
    
}
