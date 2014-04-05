package com.view.page;

import db.controller.DAO;
import db.pojos.Vector;
import java.util.List;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import util.ContextManager;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class MapeoPage extends BorderPage {

    Form form;
    Select select;
    private List<Vector> instruments;

    @Override
    public void init() {
        form = new Form("form");
        select = new Select("select", "Instrumentos Actuales");
        fillInstrument();
        form.add(select);
        form.add(new Submit("addInstrument", "Agregar Instrumento", this, "addInstrument"));
        form.add(new Submit("editInstrument", "Editar Instrumento", this, "editInstrument"));
        addControl(form);
    }

    private void fillInstrument() {
        instruments = DAO.createQuery(Vector.class, null);
        for (Vector v : instruments) {
            select.add(new Option(v.getIdVector(), v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()));
        }
    }

    public boolean addInstrument() {
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        controller.addVariable("page", new Variable("page", this.getClass(), Class.class), true);
        userContext.cleanMap();
        userContext.addSessionController(controller);
        setRedirect(MapeoagregarPage.class);
        return true;
    }

    public boolean editInstrument() {
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        Vector chosen = null;
        for (Vector v : instruments) {
            if (Integer.parseInt(select.getValue()) == v.getIdVector()) {
                chosen = v;
            }
        }
        if (chosen == null) {
            message = "Se debe elegir un Instrumento a Editar";
            return false;
        }
        controller.addVariable("editInstrument", new Variable("editInstrument", chosen, Vector.class), true);
        setTitle("");
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        controller.addVariable("page", new Variable("page", this.getClass(), Class.class), true);
        userContext.cleanMap();
        userContext.addSessionController(controller);
        setRedirect(MapeoeditPage.class);
        return true;
    }
}
