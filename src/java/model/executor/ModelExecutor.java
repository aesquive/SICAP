package model.executor;

import db.controller.DAO;
import db.pojos.Cuenta;
import db.pojos.Operacion;
import db.pojos.Regcuenta;
import interpreter.MathInterpreter;
import interpreter.MathInterpreterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Cuenta> todasCuentas;
    public Map<String, Cuenta> cuentas;
    public Map<String, Double> valores;
    public Map<String, Operacion> operaciones;
    public boolean isSimulation;
    public Regcuenta regCuenta;

    /**
     * constructor
     *
     * @param cuentasBase
     * @param forceCalculation
     */
    public ModelExecutor(Map<String, Cuenta> cuentasBase, boolean forceCalculation) {
        this.cuentas = cuentasBase;
        this.todasCuentas = new HashMap<String, Cuenta>();
        this.operaciones = mapOperaciones(DAO.createQuery(Operacion.class, null));
        this.valores = new HashMap<String, Double>();
        this.isSimulation = forceCalculation;
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
        for (Cuenta c : createQuery) {
            if (c.getRegcuenta().getIdRegCuenta() == cuentas.get(cuentas.keySet().iterator().next()).getRegcuenta().getIdRegCuenta()) {
                todasCuentas.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
            }
        }

        for (String s : cuentas.keySet()) {
            valores.put(s, cuentas.get(s).getValor());
        }
        this.regCuenta = cuentas.get(cuentas.keySet().iterator().next()).getRegcuenta();
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
            if (cuentas.get(idRef) == null) {
                System.out.println("solicito " + idRef);
                makeOperacion(operaciones.get(idRef));
            }
            Cuenta ctaRef = cuentas.get(idRef);
            ctasRef = ctasRef.equals("") ? ctaRef.getIdCuenta() + "," : ctasRef + ctaRef.getIdCuenta() + ",";

        }
        //a este momento ya todas las cuentas de referencia deben estar hechas , entonces ahora si realizamos la operacion
        String interp = MathInterpreter.interp(operacion.getValOperacion(), valores);
        interp=interp==null ? "0.0" : interp;
        Cuenta nueva = new Cuenta(null, regCuenta, operacion.getCatalogocuenta(), Double.valueOf(interp), ctasRef, 0);
        if (isSimulation) {
            nueva = todasCuentas.get(operacion.getCatalogocuenta().getIdCatalogoCuenta().toString());
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
    public static void main(String[] args) throws MathInterpreterException {
        Map<String, Cuenta> cuentas = new HashMap<String, Cuenta>();
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
        for (Cuenta c : createQuery) {
            if (c.getRegcuenta().getIdRegCuenta() == 1) {
                if (c.getMoneda() != null && c.getMoneda().getIdMoneda()==14) {
                    cuentas.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
                }
            }
        }
        ModelExecutor m = new ModelExecutor(cuentas, false);
        m.start();
        Cuenta get = cuentas.get("1");
    }

    private void guardarCuenta(Object obj) {
        if (isSimulation) {
            DAO.update(obj);
        }
        DAO.saveOrUpdate(obj);
    }
}
