package com.example.page;

import db.pojos.User;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Button;
import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;
import util.ContextManager;

public abstract class BorderPage extends Page {

    public String userName;
    public String title;
    public String message;
    public String numberContext;
    private Menu rootMenu;
    public SessionController sessionController;
    
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
        sessionController.addVariable("title", new Variable("title", title, String.class), true);
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
        
    }

    /**
     * evento que se lanza cuando se aprieta salir
     * @return 
     */
    public boolean onLogOut(){
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
        if (sessionController.getVariable("numContext")!=null){
           Variable numContextVar=sessionController.getVariable("numContext");
           if(numContextVar!=null){
               numberContext=(String) numContextVar.getValue();
           }
        }
    }
}
