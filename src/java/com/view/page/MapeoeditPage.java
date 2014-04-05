package com.view.page;

import db.pojos.Vector;
import manager.session.Variable;
import org.apache.click.control.Form;
import org.apache.click.control.Select;
import org.apache.click.control.TextField;
import util.UserManager;

/**
 *
 * @author Admin
 */
public class MapeoeditPage extends BorderPage {

    Vector editedInstrument;
    Form form;
    TextField tipoInstrumento;
    TextField emisora;
    TextField serie;
    TextField tasa;
    Select tipoTasa;
    TextField precioSucio;
    TextField precioLimpio;
    Select moodys;
    Select sp;
    Select fitch;
    Select hr;
    TextField duracion;

    @Override
    public void init() {
        form = new Form("form");
        tipoInstrumento = new TextField();
        emisora = new TextField();
        serie = new TextField();
        tasa = new TextField();
        tipoTasa = new Select();
        precioSucio = new TextField();
        precioLimpio = new TextField();
        moodys = new Select();
        sp = new Select();
        fitch = new Select();
        hr = new Select();
        duracion = new TextField();
        editedInstrument = UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("editInstrument").toString())).actualContext).getVariable("data") == null ? null
                : (Vector) UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("editInstrument").toString())).actualContext).getVariable("data").getValue();

        UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).getSessionController(UserManager.getContextManager(Integer.parseInt(getContext().getSessionAttribute("user").toString())).actualContext).addVariable("page", new Variable("page", this.getClass(), Class.class), true);
        tipoInstrumento.setValue(editedInstrument.getIdTipoInstrumento());
        emisora.setValue(editedInstrument.getEmisioraInstrumento());
        serie.setValue(editedInstrument.getSerieInstrumento());
        tasa.setValue(editedInstrument.getTasaInstrumento().toString());
        tipoTasa.setValue(editedInstrument.getTipotasa().getIdTipoTasa().toString());
        precioSucio.setValue(editedInstrument.getPrecioSucio().toString());
        precioLimpio.setValue(editedInstrument.getPrecioLimpio().toString());
        if (editedInstrument.getCalificacionByIdCalificacionMoody() != null) {
            moodys.setValue(editedInstrument.getCalificacionByIdCalificacionMoody().getIdCalificacion().toString());
        }
        if (editedInstrument.getCalificacionByIdCalificacionSp() != null) {
            sp.setValue(editedInstrument.getCalificacionByIdCalificacionSp().getIdCalificacion().toString());
        }
        if (editedInstrument.getCalificacionByIdCalificacionFitch() != null) {
            fitch.setValue(editedInstrument.getCalificacionByIdCalificacionFitch().getIdCalificacion().toString());
        }
        if (editedInstrument.getCalificacionByIdCalificacionHr() != null) {
            hr.setValue(editedInstrument.getCalificacionByIdCalificacionHr().getIdCalificacion().toString());
        }
        duracion.setValue(editedInstrument.getDuracion().toString());
    }

}
