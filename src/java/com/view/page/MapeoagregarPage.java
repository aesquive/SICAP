package com.view.page;

import db.controller.DAO;
import db.pojos.Calificacion;
import db.pojos.Regvector;
import db.pojos.Tipotasa;
import file.uploader.vector.Vector;
import file.uploader.vector.VectorPIPReader;
import file.uploader.vector.VectorReader;
import java.io.File;
import static java.lang.Double.parseDouble;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import manager.session.SessionController;
import manager.session.Variable;
import org.apache.click.control.Form;
import org.apache.click.control.Label;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.element.JsScript;
import util.ContextManager;
import util.Reflector;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class MapeoagregarPage extends BorderPage {

    private String criterioBusqueda;
    private Vector vectorSeleccionado;
    private Form dataForm;

//    private Form formActuales;
    //  private Select selectInstrumentosActuales;
    //private List<Vector> instrumentosActuales;
    private Form formVectores;
    private Select selectInstrumentosVector;
    private List<Vector> instrumentosVector;

    private TextField searchField;
    private Form searchForm;
    private TextField tipoInstrumento;
    private TextField emisora;
    private TextField serie;
    private TextField tasa;
    private Select tipoTasa;
    private TextField precioSucio;
    private TextField precioLimpio;
    private Select moodys;
    private Select sp;
    private Select fitch;
    private Select hr;
    private TextField duracion;

    @Override
    public void init() {
        criterioBusqueda = "";
        getVariables();
        searchForm();
        if (!criterioBusqueda.equals("") && vectorSeleccionado == null) {
            resultForm();
        }
        if (vectorSeleccionado != null) {
            dataForm();
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
            if (variable != null && variable.getValue() != null) {
                criterioBusqueda = variable == null ? "" : variable.getValue().toString();
            }
            Variable selecVector = controller.getVariable("selectedVectorVector");
            if (selecVector != null && selecVector.getValue() != null) {
                vectorSeleccionado = selecVector == null ? null : (Vector) selecVector.getValue();

            }
        }

    }

    private void resultForm() {
        //  formActuales = new Form("formActuales");
        formVectores = new Form("formVectores");
        //selectInstrumentosActuales = new Select("Instrumentos Actuales");
        //instrumentosActuales = DAO.createQuery(Vector.class, null);
        //for (Vector v : instrumentosActuales) {
        //    selectInstrumentosActuales.add(new Option(v.getIdVector(), v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()));
        //}
        selectInstrumentosVector = new Select("Intrumentos en Vector");
        try {
            instrumentosVector = getLastVector();
            for (Vector v : instrumentosVector) {
                selectInstrumentosVector.add(v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento());
            }
        } catch (Exception e) {
        }
        //formActuales.add(selectInstrumentosActuales);
        formVectores.add(selectInstrumentosVector);
        //formActuales.add(new Submit("okInstrumentosActuales", "Utilizar Información", this, "okInstrumentosActuales"));
        formVectores.add(new Submit("okInstrumentosVector", "Utilizar Información", this, "okInstrumentosVector"));
        //addControl(formActuales);
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
    /*
     public boolean okInstrumentosActuales() {
     Vector selected = null;
     for (Vector v : instrumentosActuales) {
     if (Integer.parseInt(selectInstrumentosActuales.getValue()) == v.getIdVector()) {
     selected = v;
     break;
     }
     }
     SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
     ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
     controller.addVariable("searchedInstrument", new Variable("searchedInstrument", searchField.getValue(), String.class), true);
     controller.addVariable("selectedVectorActual", new Variable("selectedVectorActual", selected, Vector.class), true);
     controller.addVariable("selectedVectorVector", new Variable("selectedVectorVector", null, String.class), true);
     userContext.addSessionController(controller);
     setRedirect(MapeoagregarPage.class);
     return true;
     }
     */

    public boolean okInstrumentosVector() {
        Vector selected = null;
        for (Vector v : instrumentosVector) {
            if (selectInstrumentosVector.getValue().toUpperCase().equals((v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()).toUpperCase())) {
                selected = v;
                break;
            }
        }

        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        controller.addVariable("searchedInstrument", new Variable("searchedInstrument", searchField.getValue(), String.class), true);
        controller.addVariable("selectedVectorActual", new Variable("selectedVectorActual", null, String.class), true);
        controller.addVariable("selectedVectorVector", new Variable("selectedVectorVector", selected, String.class), true);
        userContext.addSessionController(controller);
        setRedirect(MapeoagregarPage.class);
        return true;
    }

    private void dataForm() {
        dataForm = new Form("dataForm");
        tipoInstrumento = new TextField("Tipo de Instrumento");
        emisora = new TextField("Emisora");
        serie = new TextField("Serie");
        tasa = new TextField("Tasa");
        tipoTasa = new Select("Tipo de Tasa");
        precioSucio = new TextField("Precio Sucio");
        precioLimpio = new TextField("Precio Limpio");
        moodys = new Select("Calificación Moody´s");
        sp = new Select("Calificación S&P");
        fitch = new Select("Calificación Fitch");
        hr = new Select("Calificación HR");
        duracion = new TextField("Duración");
        fillCalificaciones(moodys, sp, fitch, hr);
        fillTipoTasa(tipoTasa);
        tipoInstrumento.setValue(vectorSeleccionado.getIdTipoInstrumento());
        emisora.setValue(vectorSeleccionado.getEmisioraInstrumento());
        serie.setValue(vectorSeleccionado.getSerieInstrumento());
        tasa.setValue(vectorSeleccionado.getTasaInstrumento().toString());
        precioSucio.setValue(vectorSeleccionado.getPrecioSucio().toString());
        precioLimpio.setValue(vectorSeleccionado.getPrecioLimpio().toString());
        if (vectorSeleccionado.getTipotasa() != null) {
            tipoTasa.setValue(vectorSeleccionado.getTipotasa().getIdTipoTasa().toString());
        }
        if (vectorSeleccionado.getCalificacionByIdCalificacionMoody() != null) {
            moodys.setValue(vectorSeleccionado.getCalificacionByIdCalificacionMoody().getIdCalificacion().toString());
        }
        if (vectorSeleccionado.getCalificacionByIdCalificacionSp() != null) {
            sp.setValue(vectorSeleccionado.getCalificacionByIdCalificacionSp().getIdCalificacion().toString());
        }
        if (vectorSeleccionado.getCalificacionByIdCalificacionFitch() != null) {
            fitch.setValue(vectorSeleccionado.getCalificacionByIdCalificacionFitch().getIdCalificacion().toString());
        }
        if (vectorSeleccionado.getCalificacionByIdCalificacionHr() != null) {
            hr.setValue(vectorSeleccionado.getCalificacionByIdCalificacionHr().getIdCalificacion().toString());
        }
        if (vectorSeleccionado.getDuracion() != null) {
            duracion.setValue(vectorSeleccionado.getDuracion().toString());
        }
        dataForm.add(new Label("name", "Parametros del Nuevo Instrumento:"));
        dataForm.add(tipoInstrumento);
        dataForm.add(emisora);
        dataForm.add(serie);
        dataForm.add(tasa);
        dataForm.add(tipoTasa);
        dataForm.add(precioSucio);
        dataForm.add(precioLimpio);
        dataForm.add(moodys);
        dataForm.add(sp);
        dataForm.add(fitch);
        dataForm.add(hr);
        dataForm.add(duracion);
        dataForm.add(new Submit("submitAddInstrument", "Agregar Instrumento", this, "submitInstrumento"));
        addControl(dataForm);

    }

    public boolean submitInstrumento() {
        String message = "Se deben llenar todos los campos ";
        boolean continueProcess = true;
        dataForm.add(moodys);
        dataForm.add(sp);
        dataForm.add(fitch);
        dataForm.add(hr);
        dataForm.add(duracion);
        if (tipoInstrumento.getValue().equals("")) {
            message += "Tipo de Instrumento ,";
            continueProcess = false;
        }
        if (emisora.getValue().equals("")) {
            message += "Emisora ,";
            continueProcess = false;
        }
        if (serie.getValue().equals("")) {
            message += "Serie ,";
            continueProcess = false;
        }
        if (tasa.getValue().equals("")) {
            message += "Tasa ,";
            continueProcess = false;
        }
        if (tipoTasa.getValue().equals("")) {
            message += "Tipo de Tasa ,";
            continueProcess = false;
        }
        if (precioSucio.getValue().equals("")) {
            message += "Precio Sucio ,";
            continueProcess = false;
        }
        if (precioLimpio.getValue().equals("")) {
            message += "Precio Limpio ,";
            continueProcess = false;
        }
        if (moodys.getValue().equals("")) {
            message += "Calificación Moody´s ,";
            continueProcess = false;
        }
        if (sp.getValue().equals("")) {
            message += "Calificación S&P ,";
            continueProcess = false;
        }
        if (fitch.getValue().equals("")) {
            message += "Calificación Fitch ,";
            continueProcess = false;
        }

        if (hr.getValue().equals("")) {
            message += "Calificación HR ,";
            continueProcess = false;
        }
        if (duracion.getValue().equals("")) {
            message += "Calificación HR ,";
            continueProcess = false;
        }
        if (vectorRepeated(tipoInstrumento.getValue() + emisora.getValue() + serie.getValue(), DAO.createQuery(Vector.class, null))) {
            continueProcess = false;
            message = "El instrumento ya existe , imposible darlo de Alta";
            super.message=message;
            return false;
        }
        if (continueProcess) {
            Vector vector = new Vector(vectorSeleccionado.getFecha());
            continueProcess = inyectarVector(vector);
            if (continueProcess) {
                DAO.save(vector);
                cleanAll();
                super.message="Instrumento Agregado";
                setRedirect(MapeoPage.class);
                return true;
            }
            message = "Verificar los tipos de datos Numericos";
        }
        super.message = message;
        return false;
    }

    private void fillCalificaciones(Select moodys, Select sp, Select fitch, Select hr) {
        List<Calificacion> createQuery = DAO.createQuery(Calificacion.class, null);
        for (Calificacion c : createQuery) {
            Integer idCalificadora = c.getCalificadora().getIdCalificadora();
            switch (idCalificadora) {
                case 1:
                    moodys.add(new Option(c.getIdCalificacion(), c.getCalificacion()));
                    break;
                case 2:
                    sp.add(new Option(c.getIdCalificacion(), c.getCalificacion()));
                    break;
                case 3:
                    fitch.add(new Option(c.getIdCalificacion(), c.getCalificacion()));
                    break;
                case 4:
                    hr.add(new Option(c.getIdCalificacion(), c.getCalificacion()));
                    break;
                default:
            }
        }
    }

    private void fillTipoTasa(Select tipoTasa) {
        tipoTasa.add(new Option(3, "Tasa Fija"));
        tipoTasa.add(new Option(17, "Tasa Variable"));
    }

    private boolean vectorRepeated(String emision, List<Vector> createQuery) {
        for (Vector v : createQuery) {
            if ((v.getIdTipoInstrumento() + v.getEmisioraInstrumento() + v.getSerieInstrumento()).toUpperCase().equals(emision.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean inyectarVector(Vector vector) {
        Double tasaDou = parseDoubleData(tasa.getValue());
        Double precioSuc = parseDoubleData(precioSucio.getValue());
        Double precioLim = parseDoubleData(precioLimpio.getValue());
        Double dura = parseDoubleData(duracion.getValue());
        if (tasaDou == null || precioSuc == null || precioLim == null || dura == null) {
            return false;
        }
        vector.setIdTipoInstrumento(tipoInstrumento.getValue());
        vector.setEmisioraInstrumento(emisora.getValue());
        vector.setSerieInstrumento(serie.getValue());
        vector.setTasaInstrumento(tasaDou);
        vector.setPrecioSucio(precioSuc);
        vector.setPrecioLimpio(precioLim);
        vector.setDuracion(dura);
        List<Tipotasa> tipoTasas = DAO.createQuery(Tipotasa.class, null);
        for (Tipotasa ta : tipoTasas) {
            if (ta.getIdTipoTasa().toString().equals(tipoTasa.getValue())) {
                vector.setTipotasa(ta);
            }
        }
        List<Calificacion> createQuery = DAO.createQuery(Calificacion.class, null);
        for (Calificacion c : createQuery) {
            if (c.getCalificadora().getIdCalificadora() == 1) {
                List<Option> optionList = moodys.getOptionList();
                for (Option o : optionList) {
                    if (o.getValue().equals(moodys.getValueObject())) {
                        vector.setCalificacionByIdCalificacionMoody(c);
                    }
                }
            }
            if (c.getCalificadora().getIdCalificadora() == 2) {
                List<Option> optionList = sp.getOptionList();
                for (Option o : optionList) {
                    if (o.getValue().equals(sp.getValueObject())) {
                        vector.setCalificacionByIdCalificacionSp(c);
                    }
                }
            }
            if (c.getCalificadora().getIdCalificadora() == 3) {
                List<Option> optionList = fitch.getOptionList();
                for (Option o : optionList) {
                    if (o.getValue().equals(fitch.getValueObject())) {
                        vector.setCalificacionByIdCalificacionFitch(c);
                    }
                }
            }

            if (c.getCalificadora().getIdCalificadora() == 4) {
                List<Option> optionList = hr.getOptionList();
                for (Option o : optionList) {
                    if (o.getValue().equals(hr.getValueObject())) {
                        vector.setCalificacionByIdCalificacionHr(c);
                    }
                }
            }
        }

        return true;
    }

    private Double parseDoubleData(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void cleanAll() {
        SessionController controller = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext);
        ContextManager userContext = UserManager.addUserContext(Integer.parseInt(getContext().getSessionAttribute("user").toString()));
        controller.addVariable("searchedInstrument", new Variable("searchedInstrument", null, String.class), true);
        controller.addVariable("selectedVectorActual", new Variable("selectedVectorActual", null, String.class), true);
        controller.addVariable("selectedVectorVector", new Variable("selectedVectorVector", null, String.class), true);
        userContext.addSessionController(controller);
    }

}
