package com.example.page;

import db.pojos.User;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;

public abstract class BorderPage extends Page {

    public String userName;
    public String title;
    public String message;
    public ActionLink logOut;
    private Menu rootMenu;

    public BorderPage() {
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
        SessionController.addVariable("title", new Variable("title", title, String.class), true);
    }

    /**
     * agrega el Menu de inicio
     */
    private void addCommonControls() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
        rootMenu.setName("rootMenu");
        rootMenu.setId("rootMenu");
        addControl(rootMenu);
        
        logOut=new ActionLink("logOut", "Salir",this, "onLogOut");
        addControl(logOut);
    }

    /**
     * evento que se lanza cuando se aprieta salir
     * @return 
     */
    public boolean onLogOut(){
        setRedirect(LoginPage.class);
        SessionController.cleanMap();
        return true;
    }
    
    /**
     * Verifica las variables de sesion y trae los respectivos valores
     */
    private void checkSessionVars() {

        if (SessionController.getVariable("user") != null) {
            Variable user = SessionController.getVariable("user");
            if (user != null) {
                userName = ((User) user.getValue()).getUser();
            }
        }
        if (SessionController.getVariable("title") != null) {
            Variable titlevar = SessionController.getVariable("title");
            if (titlevar != null) {
                title = (String) titlevar.getValue();
            }
        }
    }
}
