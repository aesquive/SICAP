package com.example.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import manager.session.Variable;
import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Table;
import org.apache.click.extras.control.FormTable;
import util.UserManager;

/**
 * Clase que se encarga de poner detalles de cuentas pasados los datos deben de
 * estar en el SessionController con el nombre "data"
 *
 * @author zorin
 */
public class TablePage extends BorderPage {

    FormTable table;
    Form form;
    @Resource(name = "data")
    List<Cuenta> data;

    /**
     * constructor
     */
    public TablePage() {
        super();
        fillData();
    }

    @Override
    public void init() {
        data = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("data") == null ? null : 
                (List) UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("data").getValue();
        form = new Form("form");
        table = new FormTable("table", form);
        table.setName("dataTable");
        table.setPageNumber(0);
        table.setClass(Table.CLASS_ORANGE2);
        table.setWidth("900px");
        //ponemos las primeras columnas
        Column cID=new Column("IdCuenta");
        cID.setWidth("35%");
        table.addColumn(cID);
        Column dID=new Column("Descripcion");
        dID.setWidth("40%");
        table.addColumn(dID);

        //ponemos los links de los montos para hacerlo recursivo
        for (int t = 0; t < data.size(); t++) {
            ActionLink actionLink = new ActionLink("link" + data.get(t).getIdCuenta(), data.get(t).getValor().toString(), this, "onLinkClick");
            actionLink.setValue(data.get(t).getIdCuenta().toString());
            data.get(t).setActionLink(actionLink);
            addControl(actionLink);
        }
        Column c = new Column("Valor");
        c.setWidth("25%");
        c.setDecorator(new Decorator() {
            @Override
            public String render(Object object, Context context) {
                Cuenta c = (Cuenta) object;
                return c.getActionLink().toString();
            }
        });
        table.addColumn(c);
        //pegamos todo en nuestro fieldset
        FieldSet fs = new FieldSet("Datos");
        form.add(fs);
        fs.add(table);
        //System.out.println("la cantidad de links " + links.length);
        addControl(form);
    }

    /**
     * llena la tabla de informacion;
     */
    private void fillData() {
        table.setRowList(data);
    }

    /**
     * evento que nos ayuda a checar si se da click en alguno de los valores 
     * @return 
     */
    public boolean onLinkClick() {
        for (int t = 0; t < data.size(); t++) {
            if (data.get(t).getActionLink().isClicked()) {
                Cuenta ref = data.get(t);
                List<Cuenta> newData = new LinkedList<Cuenta>();
                List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
                if (ref.getRef() == null || ref.getRef().equals("")) {
                    newData.add(ref);
                } else {
                    String[] split = ref.getRef().split(",");
                    for (String sp : split) {
                        for (Cuenta c : createQuery) {
                            if (c.getIdCuenta().toString().equals(sp)) {
                                newData.add(c);
                            }
                        }
                    }
                }
                newContext();
                setTitle(ref.getDescripcion());
                UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).addVariable("data", new Variable("data", newData, List.class), true);
                setRedirect(TablePage.class);
                return true;
            }
        }
        return true;
    }


}
