package com.example.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Context;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.extras.control.FormTable;
import org.apache.click.extras.control.LinkDecorator;
import util.Reflector;

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
        data = SessionController.getVariable("data") == null ? null : (List) SessionController.getVariable("data").getValue();
        form = new Form("form");
        table = new FormTable("formTable", form);

        table.addColumn(new Column("IdCuenta"));
        table.addColumn(new Column("Descripcion"));

        for (int t = 0; t < data.size(); t++) {
            ActionLink actionLink = new ActionLink("link" + data.get(t).getIdCuenta(), data.get(t).getValor().toString(), this, "onLinkClick");
            actionLink.setValue(data.get(t).getIdCuenta().toString());
            data.get(t).setActionLink(actionLink);
            addControl(actionLink);
        }
        Column c = new Column("Valor");
        c.setDecorator(new Decorator() {
            @Override
            public String render(Object object, Context context) {
                Cuenta c = (Cuenta) object;
                return c.getActionLink().toString();
            }
        });

        table.addColumn(c);
        FieldSet fs = new FieldSet("info");
        form.add(fs);
        fs.add(table);
        //System.out.println("la cantidad de links " + links.length);
        addControl(form);
    }

    private void fillData() {
        table.setRowList(data);
    }

    public boolean onLinkClick() {
        System.out.println("entra al linkclick");
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
                setTitle("Detalle " + ref.getDescripcion());
                SessionController.addVariable("data", new Variable("data", newData, List.class), true);
                setRedirect(TablePage.class);
                return true;
            }
        }
        return true;
    }

}
