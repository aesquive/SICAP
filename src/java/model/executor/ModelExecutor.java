package model.executor;

import db.controller.DAO;
import db.pojos.Catalogocuenta;
import db.pojos.Cuenta;
import db.pojos.Operacion;
import db.pojos.Regcuenta;
import interpreter.MathInterpreter;
import interpreter.MathInterpreterException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import manager.configuration.Configuration;
import org.hibernate.criterion.Criterion;

/**
 * Ejecuta todas las operaciones contenidas dentro de la base de datos de la
 * tabla operacion Es un sistema inteligente que si utiliza un valor que no ha
 * sido calculado aun va a calcularlo , si no hay forma de calcular el valor
 * entonces lanzara una excepcion.
 *
 * @author Alberto Emmanuel Esquivel Vega
 */
public class ModelExecutor {

    /**
     * son las cuentas con las cuales se va a correr el modelo
     */
    public Map<String, Cuenta> cuentas;
    public Map<String, Double> valores;
    public Map<String, Operacion> operaciones;
    public boolean isSimulation;
    public Regcuenta regCuenta;
    private Criterion[] criterios;

    public ModelExecutor(String baseModelo, Regcuenta regCuenta, boolean isSimulation) throws IOException {
        if(!isSimulation){
            Set<Cuenta> cuentas1 = regCuenta.getCuentas();
            for(Cuenta c:cuentas1){
                if(c.getMoneda()==null){
                    System.out.println("borrando"+c.getCatalogocuenta().getIdCatalogoCuenta());
                    DAO.delete(c);
                }
            }
        }
        this.valores = new HashMap<String, Double>();
        this.cuentas = new HashMap<String, Cuenta>();
        this.operaciones = mapOperaciones(DAO.createQuery(Operacion.class, null));
        this.isSimulation = isSimulation;
        this.regCuenta = regCuenta;
        mapCuentas((List<Cuenta>) DAO.createQuery(Cuenta.class, null));
        //sacamos los datos del modelo de excel
        ExcelInteraction ex = new ExcelInteraction(baseModelo);
        Map<Integer, Double> modelExcelData = ex.getModelExcelData();
        //sacamos los datos del mapeo de catalogos de cuentas
        List<Catalogocuenta> queryCatalogos = DAO.createQuery(Catalogocuenta.class, criterios);
        Map<Integer, Catalogocuenta> mapCatalogosCuentas = new HashMap<Integer, Catalogocuenta>();
        for (Catalogocuenta c : queryCatalogos) {
            mapCatalogosCuentas.put(c.getIdCatalogoCuenta().intValue(), c);
        }
        //generamos las nuevas cuentas
        for (Integer s : modelExcelData.keySet()) {
            System.out.println("intentando " + s);
            Cuenta c;
            if (isSimulation) {
                c = cuentas.get(String.valueOf(s));
            } else {
                c = new Cuenta();
            }
            c.setCatalogocuenta(mapCatalogosCuentas.get(s));
            c.setValor(modelExcelData.get(s));
            c.setRegcuenta(regCuenta);
            c.setRef("");
            guardarCuenta(c);
        }
        
        mapCuentas((List<Cuenta>) DAO.createQuery(Cuenta.class, null));
        for(String s:cuentas.keySet()){
            Cuenta c=cuentas.get(s);
            valores.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(),c.getValor());
        }
    }

    public void start() throws MathInterpreterException {
        if (isSimulation || !operationsCompleted()) {
            while (!operationsCompleted()) {
                startOperations();
            }
        }
    }

    /**
     * mapea las operaciones con el numero de identificacion de la cuenta
     *
     * @param listaOperaciones
     * @return
     */
    private Map<String, Operacion> mapOperaciones(List<Operacion> listaOperaciones) {
        Map<String, Operacion> ops = new HashMap<String, Operacion>();
        for (Operacion op : listaOperaciones) {
            ops.put(op.getCatalogocuenta().getIdCatalogoCuenta().toString(), op);
        }
        return ops;
    }

    /**
     * empieza a realizar las operaciones
     */
    private void startOperations() throws MathInterpreterException {
        for (String s : operaciones.keySet()) {
            //la operacion no se ha realizado por que no esta en las cuentas ya disponibles
            if (cuentas.get(s) == null) {
                makeOperacion(operaciones.get(s));
            }
        }
    }

    /**
     * checa si las operaciones ya fueron todas realizadas
     *
     * @return
     */
    public boolean operationsCompleted() {
        for (String s : operaciones.keySet()) {
            if (cuentas.get(s) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * realiza todas las operaciones del modelo
     *
     * @param operacion
     * @throws MathInterpreterException
     */
    private void makeOperacion(Operacion operacion) throws MathInterpreterException {
        System.out.println("haciendo la operacion de " + operacion.getCatalogocuenta().getIdCatalogoCuenta());
        String valOperacion = operacion.getValOperacion();
        String[] split = valOperacion.split("=");
        String ctasRef = "";
        //checamos que todas las cuentas a las que hace referencia una operacion ya esten hechas
        for (int t = 1; t < split.length; t++) {
            String idRef = "";
            for (int i = 0; i < split[t].length(); i++) {
                if (split[t].charAt(i) == ')') {
                    i = split[t].length();
                } else {
                    idRef = idRef + split[t].charAt(i);
                }
            }
            //checamos si necesitamos calcular alguna otra cosa ante
            if (cuentas.get(idRef) == null) {
                System.out.println("solicito " + idRef);
                if (operaciones.get(idRef) != null) {
                    System.out.println("la operacion " + operaciones.get(idRef).getValOperacion());
                    makeOperacion(operaciones.get(idRef));
                }
            }
            //agregamos 
            Cuenta ctaRef = cuentas.get(idRef);
            if (ctaRef != null) {
                ctasRef = ctasRef.equals("") ? ctaRef.getIdCuenta() + "," : ctasRef + ctaRef.getIdCuenta() + ",";
            }
        }
        //a este momento ya todas las cuentas de referencia deben estar hechas , entonces ahora si realizamos la operacion
        String interp = MathInterpreter.interp(operacion.getValOperacion(), valores);
        interp = interp == null ? "0.0" : interp;
        Cuenta nueva = new Cuenta(null, regCuenta, operacion.getCatalogocuenta(), Double.valueOf(interp), ctasRef, 0);
        if (isSimulation) {
            nueva = cuentas.get(operacion.getCatalogocuenta().getIdCatalogoCuenta().toString());
            nueva.setValor(Double.valueOf(interp));
        }
        guardarCuenta(nueva);
        cuentas.put(operacion.getCatalogocuenta().getIdCatalogoCuenta().toString(), nueva);
        valores.put(operacion.getCatalogocuenta().getIdCatalogoCuenta().toString(), Double.valueOf(interp));
    }

    /**
     * prueba
     *
     * @param args
     * @throws MathInterpreterException
     */
    public static void main(String[] args) throws MathInterpreterException, IOException {
        int regCuenta = 8;
        Map<String, Cuenta> cuentas = new HashMap<String, Cuenta>();
        List<Regcuenta> createQuery = DAO.createQuery(Regcuenta.class, null);
        Regcuenta c = new Regcuenta();
        for (Regcuenta r : createQuery) {
            if (r.getIdRegCuenta() == regCuenta) {
                c = r;
            }
        }
        String value = Configuration.getValue("baseModelo");
        ModelExecutor m = new ModelExecutor(Configuration.getValue("baseModelo"), c, false);
        m.start();
        Cuenta get = cuentas.get("1");
    }

    private void guardarCuenta(Object obj) {
        if (isSimulation) {
            DAO.update(obj);
        }
        DAO.saveOrUpdate(obj);
    }

    private void mapCuentas(List<Cuenta> list) {
        cuentas.clear();
        for (Cuenta c : list) {
            if (c.getRegcuenta().getIdRegCuenta() == regCuenta.getIdRegCuenta()) {
                    cuentas.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
            }
        }
    }
}
