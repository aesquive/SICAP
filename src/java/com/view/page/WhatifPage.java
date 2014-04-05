package com.view.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import db.pojos.Regcuenta;
import db.pojos.Regcuentauser;
import db.pojos.User;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import util.ContextManager;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class WhatifPage extends BorderPage {

    Form formView;

    Form form;
    Select selectProject;
    Select selectView;
    TextField nameSimulation;
    boolean onceClicked;
    User user;

    public WhatifPage() {
        super();
    }

    @Override
    public void init() {
        form = new Form("form");
        formView = new Form("formView");
        onceClicked = true;
        selectProject = new Select("Ejercicio");
        selectView = new Select("Ejercicio");
        nameSimulation = new TextField("Nombre de la Simulación");
        selectProject.setId("selectwhatif");
        nameSimulation.setRequired(true);
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        user = (User) controller.getVariable("user").getValue();
        List<Regcuentauser> createQuery = DAO.createQuery(Regcuentauser.class, null);
        for (Regcuentauser ru : createQuery) {
            if (ru.getUser().getIduser() == user.getIduser()) {
                selectProject.add(new Option(ru.getRegcuenta(), ru.getRegcuenta().getDesRegCuenta()));
                selectView.add(new Option(ru.getRegcuenta(), ru.getRegcuenta().getDesRegCuenta()));
            }
        }
        form.add(selectProject);
        form.add(nameSimulation);
        form.add(new Submit("okWhatIf", "Crear Simulación", this, "okWhatIfClicked"));
        formView.add(selectView);
        formView.add(new Submit("okEditWhatIf", "Editar Simulación", this, "okViewClicked"));
        addControl(formView);
        addControl(form);
    }

    /**
     * evento de inicio de la simulación
     *
     * @return
     */
    public boolean okWhatIfClicked() {
        if (nameSimulation.getValue().equals("")) {
            return false;
        }
        if (onceClicked) {
            copiarProyecto();
        }
        return true;
    }

    public boolean okViewClicked() {
        Regcuenta regCta = null;
        List<Regcuenta> createQuery = DAO.createQuery(Regcuenta.class, null);
        for (Regcuenta rc : createQuery) {
            if (Integer.parseInt(selectView.getValue()) == rc.getIdRegCuenta()) {
                try {
                    regCta = (Regcuenta) rc.clone();
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(WhatifPage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        cambiarPantalla(regCta);
        return true;
    }

    private void copiarProyecto() {
        try {
            Regcuenta regCuenta = new Regcuenta();
            Calendar c = Calendar.getInstance();
            regCuenta.setDesRegCuenta(nameSimulation.getValue());
            regCuenta.setFecha(c.getTime());
            regCuenta.setRegcuentausers(null);
            regCuenta.setCuentas(null);
            DAO.save(regCuenta);
            Regcuentauser regctauser = new Regcuentauser(regCuenta, user);
            DAO.save(regctauser);
            copiarCuentas(regCuenta);
            cambiarPantalla(regCuenta);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(WhatifPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void copiarCuentas(Regcuenta regCuenta) throws CloneNotSupportedException {
        Regcuenta regCta = null;
        List<Regcuenta> createQuery = DAO.createQuery(Regcuenta.class, null);
        for (Regcuenta rc : createQuery) {
            if (Integer.parseInt(selectProject.getValue()) == rc.getIdRegCuenta()) {
                regCta = (Regcuenta) rc.clone();
            }
        }
        Set<Cuenta> cuentas = regCta.getCuentas();
        Map<String, Cuenta> viejasCatalogoCuenta = new HashMap<String, Cuenta>();
        Map<String, Cuenta> viejasIDCuenta = new HashMap<String, Cuenta>();
        Map<String, Cuenta> nuevasCatalogoCuenta = new HashMap<String, Cuenta>();
        for (Cuenta cta : cuentas) {
            Cuenta c = (Cuenta) cta.clone();
            Cuenta nueva = new Cuenta();
            nueva.setMoneda(c.getMoneda());
            nueva.setCatalogocuenta(c.getCatalogocuenta());
            nueva.setRegcuenta(regCuenta);
            nueva.setValor(c.getValor());
            DAO.save(nueva);
            viejasCatalogoCuenta.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
            viejasIDCuenta.put(c.getIdCuenta().toString(), c);
            nuevasCatalogoCuenta.put(nueva.getCatalogocuenta().getIdCatalogoCuenta().toString(), nueva);
        }
        for (String s : nuevasCatalogoCuenta.keySet()) {
            Cuenta nueva = nuevasCatalogoCuenta.get(s);
            Cuenta vieja = viejasCatalogoCuenta.get(s);
            String ref = vieja.getRef();
            String newRef = "";
            if (ref != null) {
                String[] split = ref.split(",");
                for (String sp : split) {
                    if (!sp.equals("")) {
                        Cuenta ctaRef = viejasIDCuenta.get(sp);
                        Cuenta ctaNuevaRef = nuevasCatalogoCuenta.get(ctaRef.getCatalogocuenta().getIdCatalogoCuenta().toString());
                        newRef = newRef + ctaNuevaRef.getIdCuenta().toString() + ",";
                    }
                }
                nueva.setRef(newRef);
                DAO.update(nueva);
            }
        }
    }

    private void cambiarPantalla(Regcuenta regCuenta) {
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        List<Cuenta> data = new LinkedList<Cuenta>();
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
        for (Cuenta c : createQuery) {
            if (c.getCatalogocuenta().getIdCatalogoCuenta() == 1 && c.getRegcuenta().getIdRegCuenta() == regCuenta.getIdRegCuenta()) {
                data.add(c);
                break;
            }
        }
        controller.addVariable("data", new Variable("data", data, List.class), true);
        setTitle("");
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        userContext.cleanMap();
        userContext.addSessionController(controller);
        setRedirect(SimulacionPage.class);
    }


}
