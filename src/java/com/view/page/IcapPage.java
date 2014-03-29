package com.view.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.util.LinkedList;
import java.util.List;
import manager.session.SessionController;
import manager.session.Variable;
import util.ContextManager;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class IcapPage extends BorderPage {

    public IcapPage() {
     super();
    }

    @Override
    public void init() {
        SessionController controller= UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
        List<Cuenta> data = new LinkedList<Cuenta>();
        for (Cuenta c : createQuery) {
            if (c.getCatalogocuenta().getIdCatalogoCuenta() == 1) {
                data.add(c);
            }
        }
        controller.addVariable("data", new Variable("data", data, List.class), true);
        setTitle("");
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        userContext.cleanMap();
        userContext.addSessionController(controller);
        setRedirect(TablePage.class);
    }
}
