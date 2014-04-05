package com.view.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.util.List;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import util.ContextManager;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class EditsimulationPage extends BorderPage {

    Form formAdd;
    TextField editValue;
    Cuenta cta;

    public EditsimulationPage() {
        super();
    }

    @Override
    public void init() {
        formAdd = new Form("form");
        editValue = new TextField("editValue", "Valor");
        cta = (Cuenta) UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("ctaEdit").getValue();
        editValue.setValue(cta.getValor().toString());
        editValue.setRequired(true);
        formAdd.add(editValue);
        formAdd.add(new Submit("submitedit", "Guardar Cambio", this, "okEditClicked"));
        addControl(formAdd);
    }

    public boolean okEditClicked() {
        if (editValue.equals("")) {
            return false;
        }
        try {
            cta.setValor(Double.parseDouble(editValue.getValue()));
        } catch (NumberFormatException num) {
            System.out.println(num);
            message = "El campo debe ser númerico";
            return false;
        }
        DAO.update(cta);
        //UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        ContextManager contextManager = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        for (int t = 1; t <= contextManager.actualContext; t++) {
            SessionController sessionController = contextManager.getSessionController(t);
            Variable varData = sessionController.getVariable("data");
            if (varData != null) {
                List<Cuenta> value = (List<Cuenta>) varData.getValue();
                for (Cuenta c : value) {
                    if (c.getIdCuenta() == cta.getIdCuenta()) {
                        c = cta;
                    }
                }
            }
        }
        contextManager.removeLastSession();
        setRedirect(SimulacionPage.class);
        return true;
    }

}
