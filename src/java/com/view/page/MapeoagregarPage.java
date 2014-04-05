package com.view.page;

import db.controller.DAO;
import db.pojos.Calificacion;
import db.pojos.Regvector;
import db.pojos.Tipotasa;
import db.pojos.Vector;
import file.uploader.vector.VectorPIPReader;
import file.uploader.vector.VectorReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import util.ContextManager;
import util.Reflector;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class MapeoagregarPage extends BorderPage {

    private Form formActuales;
    private Select selectInstrumentosActuales;
    private List<Vector> instrumentosActuales;

    private Form formVectores;
    private Select selectInstrumentosVector;
    private List<Vector> instrumentosVector;

    private String criterioBusqueda;
    private Vector vectorSeleccionado;

    private TextField searchField;
    private Form searchForm;

    @Override
    public void init() {
        criterioBusqueda = "";
        vectorSeleccionado = null;
        getVariables();
        searchForm();
        if (!criterioBusqueda.equals("")) {
            resultForm();
        }
        forwardForm = null;
        backwardForm = null;
        addControl(searchForm);
    }

    /**
     * se llena el formulario de busqueda
     *
     */
    private void searchForm() {
        searchForm = new Form("searchForm");
        searchField = new TextField("Buscar ");
        searchField.setValue(criterioBusqueda);
        searchForm.add(searchField);
        Submit sub = new Submit("searchClicked");
        searchForm.add(sub);
        sub.setLabel("Buscar");
        sub.setListener(this, "searchClicked");
    }

    /**
     * evento que registra la busqueda de elementos
     *
     * @return
     */
    public boolean searchClicked() {
        System.out.println("entro al evento de search" + searchField.getValue());
        if (searchField.getValue().equals("")) {
            message = "Se debe elegir criterio de busqueda";
            return false;
        } else {
            SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
            ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
            controller.addVariable("searchedInstrument", new Variable("searchedInstrument", searchField.getValue(), String.class), true);
            controller.addVariable("selectedVectorActual", new Variable("selectedVectorActual", null, String.class), true);
            controller.addVariable("selectedVectorVector", new Variable("selectedVectorActual", null, String.class), true);
            userContext.addSessionController(controller);
            setRedirect(MapeoagregarPage.class);
            return true;
        }
    }

    /**
     * nos sirve para obtener las variables que se meten en sesion para la
     * busqueda
     */
    private void getVariables() {
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        if (controller != null) {
            Variable variable = controller.getVariable("searchedInstrument");
            criterioBusqueda = variable == null ? "" : variable.getValue().toString();
        }

    }

    private void resultForm() {
        formActuales = new Form("formActuales");
        formVectores = new Form("formVectores");
        selectInstrumentosActuales = new Select("Instrumentos Actuales");
        instrumentosActuales = DAO.createQuery(Vector.class, null);
        for (Vector v : instrumentosActuales) {
            selectInstrumentosActuales.add(new Option(v.getIdVector(), v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()));
        }
        selectInstrumentosVector = new Select("Intrumentos en Vector");
        try {
            instrumentosVector = getLastVector();
            System.out.println("el size " + instrumentosVector.size());
            for (Vector v : instrumentosVector) {
                System.out.println("agregando al combo");
                System.out.println(v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento());
                selectInstrumentosVector.add(v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento());
            }
        } catch (Exception e) {
        }
        formActuales.add(selectInstrumentosActuales);
        formVectores.add(selectInstrumentosVector);
        formActuales.add(new Submit("okInstrumentosActuales", "Utilizar Información", this, "okInstrumentosActuales"));
        formVectores.add(new Submit("okInstrumentosVector", "Utilizar Información", this, "okInstrumentosVector"));
        addControl(formActuales);
        addControl(formVectores);
    }

    private List<Vector> getLastVector() throws Exception {
        List<Regvector> createQuery = DAO.createQuery(Regvector.class, null);
        Regvector vector = null;
        for (Regvector r : createQuery) {
            if (r.getIdRegVector() >= createQuery.size()) {
                vector = r;
            }
        }
        List<List<String>> filteredData = new LinkedList<List<String>>();
        List<List<String>> data = VectorPIPReader.getData(new File(vector.getRuta()));
        for (List<String> row : data) {
            if ((row.get(1) + row.get(2) + row.get(3)).toUpperCase().contains(criterioBusqueda.toUpperCase())) {
                System.out.println("se agregara " + row.get(1) + row.get(2) + row.get(3));
                filteredData.add(row);
            }
        }
        return parseData(filteredData);
    }

    private List<Vector> parseData(List<List<String>> filteredData) {
        List<Vector> vec = new LinkedList<Vector>();
        Map<String, Calificacion> dataCalif = new HashMap<String, Calificacion>();
        Map<String, Tipotasa> dataTasa = new HashMap<String, Tipotasa>();
        List<Calificacion> calificaciones = DAO.createQuery(Calificacion.class, null);
        List<Tipotasa> tipoTasa = DAO.createQuery(Tipotasa.class, null);
        for (Calificacion c : calificaciones) {
            dataCalif.put(c.getCalificacion(), c);
        }
        for (Tipotasa tts : tipoTasa) {
            dataTasa.put(tts.getDesTipoTasa(), tts);
        }
        String[] cols = VectorReader.PIPCOLUMNS;
        String[] methods = VectorReader.PIPMETHODS;
        for (List<String> row : filteredData) {
            Vector vector = new Vector();
            for (int t = 0; t < cols.length; t++) {
                String dataType = cols[t].substring(cols[t].length() - 1, cols[t].length());
                Object obj = VectorReader.getValue(cols[t].substring(0, cols[t].length() - 1), dataType, row, dataCalif, dataTasa);
                if (obj != null) {
                    Reflector.callMethod(vector, new Object[]{obj}, methods[t]);
                }
            }
            vec.add(vector);
        }
        return vec;
    }

    public boolean okInstrumentosActuales() {
        Vector selected = null;
        for (Vector v : instrumentosActuales) {
            if (Integer.parseInt(selectInstrumentosActuales.getValue()) == v.getIdVector()) {
                selected = v;
                break;
            }
        }
        return true;
    }

    public boolean okInstrumentosVector() {
        Vector selected = null;
        for (Vector v : instrumentosVector) {
            if (selectInstrumentosVector.getValue().toUpperCase().equals((v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()).toUpperCase())) {
                selected = v;
                break;
            }
        }
        //meter el valor seleccionado y la lista de elementos.
        return true;
    }

}
