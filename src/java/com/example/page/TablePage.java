package com.example.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.Table;
import org.apache.click.dataprovider.DataProvider;
import util.Reflector;

/**
 * Clase que se encarga de poner detalles de cuentas pasados 
 * los datos deben de estar en el SessionController con el nombre "data"
 * 
 * @author zorin
 */
public class TablePage extends BorderPage {

    Table table;
    @Resource(name = "data")
    List<Object> data;
    private ActionLink detail;

    /**
     * constructor
     */
    public TablePage() {
        super();
        fillData();
    }

    /**
     * inicializa todo el proceso
     */
    @Override
    public void init() {
        table = new Table("table");
        data = SessionController.getVariable("data") == null ? null : (List) SessionController.getVariable("data").getValue();
        detail = new ActionLink("detail", " Detalle ", this, "onDetailClick");
        addControl(table);
        addControl(detail);
        if (data != null) {
            Object val = Reflector.callMethod(data.get(0), null, "getColumns");
            if (val == null) {
                message = "Ocurrio un error y no encuentro informacion que mostrar";
                return;
            }
            String[] columns = (String[]) val;
            for (String c : columns) {
                table.addColumn(new Column(c));
            }
            Column action = new Column("Acciones");
            action.setDecorator(new Decorator() {
                @Override
                public String render(Object object, Context context) {
                    Cuenta row = (Cuenta) object;
                    if (row.getRef() != null && !row.getRef().equals("")) {
                        detail.setValue(row.getIdCuenta()+"%"+row.getRef());
                        return detail.toString();
                    }
                    detail.setValue(null);
                    return null;
                }
            });
            table.addColumn(action);
        } else {
            message = "Ocurrio un error y no encuentro informacion que mostrar";
        }
    }

    /**
     * llena la informacion de la tabla
     */
    private void fillData() {
        table.setDataProvider(new DataProvider<Object>() {
            public List<Object> getData() {
                return data;
            }
        });
    }

    /**
     * se encarga de cambiar la data que se va a renderizar cuando se quiere ver el detalle
     * @return 
     */
    public boolean onDetailClick() {
        String value = detail.getValue();
        if (value != null) {
            Cuenta clicked=null;
            String[] valueRef=value.split("%");
            List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
            String[] split = valueRef[1].split(",");
            List<Cuenta> newData = new LinkedList<Cuenta>();
            for (String ref : split) {
                for (Cuenta c : createQuery) {
                    if (c.getIdCuenta().toString().equals(ref)) {
                        newData.add(c);
                    }
                }
            }
            for(Cuenta c:createQuery){
                if(c.getIdCuenta().toString().equals(valueRef[0])){
                    clicked=c;
                    break;
                }
            }
            setTitle("Detalle "+clicked.getDescripcion());
            SessionController.addVariable("data", new Variable("data", newData, List.class), true);
            setRedirect(TablePage.class);
            return true;
        }
        return false;
    }

}
