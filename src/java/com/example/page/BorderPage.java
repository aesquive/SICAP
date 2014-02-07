package com.example.page;

import db.pojos.User;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;

public abstract class BorderPage extends Page {

    public String userName;
    public String title;
    public String message;
    private Menu rootMenu;

    public BorderPage() {
        addMenu();
        init();
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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * agrega el Menu de inicio
     */
    private void addMenu() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
        rootMenu.setName("rootMenu");
        rootMenu.setId("rootMenu");
        addControl(rootMenu);
    }
}
