/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.view.page;

import db.controller.DAO;
import db.pojos.Configuracion;
import db.pojos.Cuenta;
import db.pojos.Regcuenta;
import db.pojos.Regreportes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.element.JsScript;
import reports.excelmaker.ExcelMaker;

/**
 *
 * @author Admin
 */
public class ReportesPage extends BorderPage {

    private Select selectProject;
    private Select selectReport;
    private Form form;

    private List<Regreportes> reportes;
    private List<Regcuenta> regCuentas;

    @Override
    public void init() {
        form = new Form("form");
        selectReport = new Select("selectReport", "Reporte:");
        selectProject = new Select("selectProject", "Ejercicio:");
        reportes = DAO.createQuery(Regreportes.class, null);
        for (Regreportes r : reportes) {
            selectReport.add(new Option(r.getIdRegReportes(), r.getDesReportes()));
        }
        regCuentas = DAO.createQuery(Regcuenta.class, null);
        for (Regcuenta r : regCuentas) {
            selectProject.add(new Option(r.getIdRegCuenta(), r.getDesRegCuenta()));
        }
        form.add(selectProject);
        form.add(selectReport);
        Submit sub=new Submit("sub", "Obtener Reporte", this, "okReport");
        javaScriptProcess(sub);
        form.add(sub);
        addControl(form);
    }

    public boolean okReport() {
        Regcuenta selected = null;
        for (Regcuenta r : regCuentas) {
            if (r.getIdRegCuenta().toString().equals(selectProject.getValue())) {
                selected = r;
            }
        }
        Regreportes selectedReport = null;
        for (Regreportes r : reportes) {
            if (r.getIdRegReportes().toString().equals(selectReport.getValue())) {
                selectedReport = r;
            }
        }
        if(selected==null || selectedReport==null){
            message="Se debe seleccionar algun ejercicio";
            return false;
        }
        String path = "";
        List<Configuracion> createQuery = DAO.createQuery(Configuracion.class, null);
        for (Configuracion co : createQuery) {
            if (co.getDesConfiguracion().equals("Ruta Reportes")) {
                path = co.getValor();
            }
        }
        String fileName = path + selected.getIdRegCuenta().toString() + "-" + selectedReport.getIdRegReportes().toString() + ".xlsx";
        Set<Cuenta> cuentas = selected.getCuentas();
        Map<String, Cuenta> map = new HashMap<String, Cuenta>();
        for (Cuenta c : cuentas) {
            map.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
        }

        ExcelMaker excelmaker = new ExcelMaker(fileName, selectedReport.getRuta(), map);
        File makeFile = null;
        try {
            makeFile = excelmaker.makeFile();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(makeFile));
            reader.read();
            reader.close();
        }catch(Exception e){
            System.out.println("waisting time");
        }

        setRedirect("/reportes/" + selected.getIdRegCuenta().toString() + "-" + selectedReport.getIdRegReportes().toString() + ".xlsx");        
        
        return true;
    }
}
