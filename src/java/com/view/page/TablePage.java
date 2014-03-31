package com.view.page;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import manager.session.Variable;
import net.sf.click.extras.graph.JSLineChart;
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
    JSLineChart chart = new JSLineChart("chart");

    /**
     * constructor
     */
    public TablePage() {
        super();
        fillData();
    }

    @Override
    public void init() {
        data = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("data") == null ? null
                : (List) UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).getVariable("data").getValue();
        form = new Form("form");
        table = new FormTable("table", form);
        table.setName("dataTable");
        table.setPageNumber(0);
        table.setClass(Table.CLASS_ORANGE2);
        String subtit = data.size() > 1 ? data.get(0).getEjercicio().equals(data.get(1).getEjercicio()) ? data.get(0).getEjercicio() : "Datos" : data.get(0).getEjercicio();
//ponemos los links de los montos para hacerlo recursivo
        for (int t = 0; t < data.size(); t++) {
            ActionLink actionLink = new ActionLink("link" + data.get(t).getIdCuenta(), data.get(t).getResultado(), this, "onLinkClick");
            actionLink.setValue(data.get(t).getIdCuenta().toString());
            data.get(t).setActionLink(actionLink);
            addControl(actionLink);
        }
//ponemos las primeras columnas
        String[] columnsTmp = Cuenta.getColumns();
        String[] columns = null;
        if (subtit.equals("Datos")) {
            columns = columnsTmp;
        } else {
            String[] subArray = new String[columnsTmp.length - 1];
            for (int t = 0; t < subArray.length; t++) {
                subArray[t] = columnsTmp[t + 1];
            }
            columns = subArray;
        }

        if (subtit.equals("Datos")) {
            makeGraph();
        }

        for (String c : columns) {
            Column col = new Column(c);
            col.setWidth("900 px");
            //col.setWidth(String.valueOf(100 / columns.length) + "%");
            if (c.substring(c.length() - 1, c.length()).equals("*")) {
                col.setName(c.substring(0, c.length() - 1));
                col.setDecorator(new Decorator() {
                    @Override
                    public String render(Object object, Context context) {
                        Cuenta c = (Cuenta) object;
                        return c.getActionLink().toString();
                    }
                });

            }

            table.addColumn(col);
        }
        //pegamos todo en nuestro fieldset
        FieldSet fs = new FieldSet(subtit);
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
     *
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

    private void makeGraph() {
        chart = new JSLineChart("chart");
        chart.setChartHeight(350);
        chart.setChartWidth(350);
        Map<Date, Cuenta> cuentas = new HashMap<Date, Cuenta>();
        List<Date> dates = new LinkedList<Date>();
        for (Cuenta c : data) {
            Date fecha = c.getRegcuenta().getFecha();
            cuentas.put(fecha, c);
            dates.add(fecha);
        }
        Collections.sort(dates);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        //for (Date d : dates) {
        chart.addPoint("1", 2);

//     chart.addPoint("1", cuentas.get(d).getValor().intValue());
        //}
    }

}
