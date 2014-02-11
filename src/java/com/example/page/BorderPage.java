package com.example.page;

import db.pojos.User;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;
import util.ContextManager;

public abstract class BorderPage extends Page {

    public String userName;
    public String title;
    public String message;
    public int numberContext;
    private Menu rootMenu;
    public ActionLink goBack;
    public ActionLink goForward;
    public Form buttonsForm;
    public SessionController sessionController;

    public BorderPage() {
        buttonsForm=new Form("buttonsForm");
        sessionController = ContextManager.getSessionController(ContextManager.actualContext);
        addCommonControls();
        init();
        checkSessionVars();
    }

    /**
     * casa clase que herede debe implementar como iniciar la pantalla
     */
    public abstract void init();

    /**
     * toma el estilo que se usara en comun
     *
     */
    public String getTemplate() {
        return "/border-template.htm";
    }

    /**
     * pone el titulo de la pagina
     */
    public void setTitle(String title) {
        sessionController.addVariable("title", new Variable("title", title, String.class), true);
    }

    /**
     * agrega el Menu de inicio y los botones de atras y adelante
     */
    private void addCommonControls() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
        rootMenu.setName("rootMenu");
        rootMenu.setId("rootMenu");
        addControl(rootMenu);
        addControl(buttonsForm);
        if (ContextManager.getSessionController(ContextManager.actualContext + 1) != null) {
            goForward=new ActionLink("forwardLink", "", this, "forwardClicked");
            goForward.setImageSrc("/img/forward.png");
            buttonsForm.add(goForward);
        }
        if (ContextManager.getSessionController(ContextManager.actualContext - 1) != null && ContextManager.actualContext != 1) {
            goBack=new ActionLink("backLink", "backLink", this, "backClicked");
            goBack.setImageSrc("/img/back.png");
            buttonsForm.add(goBack);
        }
        addControl(buttonsForm);
    }

    /**
     * evento que se lanza cuando el boton de forward es apretado
     *
     * @return
     */
    public boolean forwardClicked() {
        ContextManager.actualContext++;
        setRedirect(TablePage.class);
        return true;
    }

    /**
     * evento que se lanza cuando el boton de back es apretado
     *
     * @return
     */
    public boolean backClicked() {
        ContextManager.actualContext--;
        setRedirect(TablePage.class);
        return true;
    }

    /**
     * evento que se lanza cuando se aprieta salir
     *
     * @return
     */
    public boolean onLogOut() {
        ContextManager.cleanMap();
        return true;
    }

    /**
     * Verifica las variables de sesion y trae los respectivos valores
     */
    private void checkSessionVars() {
        if (sessionController.getVariable("user") != null) {
            Variable user = sessionController.getVariable("user");
            if (user != null) {
                userName = ((User) user.getValue()).getUser();
            }
        }
        if (sessionController.getVariable("title") != null) {
            Variable titlevar = sessionController.getVariable("title");
            if (titlevar != null) {
                title = (String) titlevar.getValue();
            }
        }
        if (sessionController.getVariable("numContext") != null) {
            Variable numContextVar = sessionController.getVariable("numContext");
            if (numContextVar != null) {
                numberContext = Integer.parseInt(numContextVar.getValue().toString());
            }
        }
    }

    public void newContext() {
        SessionController newSessionController = new SessionController();
        sessionController.copySession(newSessionController);
        ContextManager.addSessionController(newSessionController);
        sessionController = ContextManager.getSessionController(ContextManager.actualContext);
    }

}
