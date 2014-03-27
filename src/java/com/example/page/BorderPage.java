package com.example.page;

import db.pojos.User;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Form;
import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;
import util.ContextManager;
import util.UserManager;

public abstract class BorderPage extends Page {

    public String title;
    public String message;
    private Menu rootMenu;
    public ActionLink goBack;
    public ActionLink goForward;
    public Form forwardForm;
    public Form backwardForm;
    public ContextManager context;
    
    public BorderPage() {
        System.out.println("la sesion que lo pidio "+getContext().getSessionAttribute("user").toString());
        addCommonControls();
        checkSessionVars();
        init();
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
        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).addVariable("title", new Variable("title", title, String.class), true);
    }
    
    public String getTitle(){
        return UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("title").toString();
        
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
        if (UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext+1) != null) {
            forwardForm=new Form("forwardForm");
            addControl(forwardForm);
            goForward = new ActionLink("forwardLink", "", this, "forwardClicked");
            goForward.setName("forwardLink");
            goForward.setId("forwardLink");
            goForward.setImageSrc("/img/forward.png");
            forwardForm.add(goForward);
        }
        if (UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext-1) != null 
                && UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext != 1) {
            backwardForm=new Form("backwardForm");
            addControl(backwardForm);
            goBack = new ActionLink("backLink", "", this, "backClicked");
            goBack.setImageSrc("/img/back.png");
            goBack.setName("backLink");
            goBack.setId("backLink");
            backwardForm.add(goBack);
        }
    }

    /**
     * evento que se lanza cuando el boton de forward es apretado
     *
     * @return
     */
    public boolean forwardClicked() {
        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext++;
        setRedirect(TablePage.class);
        return true;
    }

    /**
     * evento que se lanza cuando el boton de back es apretado
     *
     * @return
     */
    public boolean backClicked() {
        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext--;
        setRedirect(TablePage.class);
        return true;
    }

    /**
     * evento que se lanza cuando se aprieta salir
     *
     * @return
     */
    public boolean onLogOut() {
        UserManager.clearContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        if(context!=null){
            context.cleanMap();
        }
        getContext().removeSessionAttribute("user");
        return true;
    }

    /**
     * Verifica las variables de sesion y trae los respectivos valores
     */
    private void checkSessionVars() {
        if (UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("title") != null) {
            Variable titlevar = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("title");
            if (titlevar != null) {
                title = (String) titlevar.getValue();
                System.out.println("el titulo es "+title);
            }
        }
        
    }

    public void newContext() { 
        SessionController newSessionController = new SessionController();
        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).copySession(newSessionController);
        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).addSessionController(newSessionController);
    }

}
