package com.example.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import db.pojos.User;
import java.util.List;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.PasswordField;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import util.ContextManager;

/**
 * Clase que maneja toda la seccion del Login
 *
 * @author zorin
 */
public class LoginPage extends Page {

    private Form form = new Form("form");
    private PasswordField passwordField;
    private TextField userField;
    public String title;
    public String message;

    /**
     * obtienes la parte del estilo de la pagina
     */
    @Override
    public String getTemplate() {
        return "/border-template.htm";
    }

    /**
     *genera el constructor del login
     * */
    public LoginPage() {
        init();
        //como es la pagina de login limpiamos toda la sesion pasada
        
        //agregamos el form a la pantalla
        addControl(form);
        //le damos el setup al form
        form.add(userField);
        form.add(passwordField);
        form.add(new Submit("okSubmit", " OK ", this, "okClicked"));
    }

    /**
     * metodo inicial
     */
    public void init() {
        form = new Form("form");
        passwordField = new PasswordField("password", " Password ", true);
        userField = new TextField("usuario", " Usuario ", true);
        passwordField.setMaxLength(10);
        userField.setMaxLength(10);
        passwordField.setMinLength(2);
    }

    /**
     * evento al dar click en OK para loggear
     * @return 
     */
    public boolean okClicked() {
        if (!form.isValid()) {
            message = "Favor de completar los campos";
            return false;
        }
        User user = verifyUser(userField.getValue(), passwordField.getValue());
        if (user == null) {
            message = "Usuario y/o password incorrecto";
            return false;
        }
        SessionController sessionController=new SessionController();
        sessionController.addVariable("user", new Variable("user", user, String.class), true);
        ContextManager.addSessionController(sessionController);
        redireccionar();
        return true;
    }
    /**
     * revisa que elk usuario loggeado este dado de alta
     * @param user
     * @param pass
     * @return 
     */
    private User verifyUser(String user, String pass) {
        List<User> createQuery = DAO.createQuery(User.class, new Criterion[]{Restrictions.and(
            Restrictions.eq("user", user), Restrictions.eq("password", pass))});
        if (createQuery.isEmpty()) {
            return null;
        }
        return createQuery.get(0);
    }

    /**
     * este METODO DEBE MODIFICARSE PARA ENVIAR AL MENU PRINCIPAL PRIMERO
     */
    private void redireccionar() {
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class,new Criterion[]{Restrictions.like("idCuenta", 3)});
        SessionController sessionController = ContextManager.getSessionController(ContextManager.actualContext);
        sessionController.addVariable("data",new Variable("data", createQuery, List.class),true);
        setRedirect(TablePage.class);
    }

}
