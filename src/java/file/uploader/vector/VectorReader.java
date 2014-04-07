/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file.uploader.vector;

import db.pojos.Calificacion;
import db.pojos.Tipotasa;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public  class VectorReader {

    /**
     *
     */
    public static String[] PIPCOLUMNS = new String[]{"1s", "2s", "3s", "58n", "24t", "5n", "4n", "36c", "37c", "53c", "59c", "47n", "0d"};

    public static String[] PIPMETHODS = new String[]{"setIdTipoInstrumento", "SetEmisioraInstrumento", "setSerieInstrumento", "setTasaInstrumento",
        "setTipoTasa", "setPrecioSucio", "setPrecioLimpio", "setCalificacionByIdCalificacionMoody", "setCalificacionByIdCalificacionSP",
        "setCalificacionByIdCalificacionFitch", "setCalificacionByIdCalificacionHR", "setDuracion", "setFecha"};

    private static final String SEPARATOR = ";";
    public static String[] VALMERCOLUMNS;
    public static String[] VALMERMETHODS;

    public static List<List<String>> getData(File file) throws FileNotFoundException, IOException {
        List<List<String>> alldata = new LinkedList<List<String>>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null && !line.equals("")) {
            LinkedList<String> dataColumn = new LinkedList<String>();
            String[] split = line.split(SEPARATOR);
            for (String s : split) {
                dataColumn.add(s);
            }
            alldata.add(dataColumn);
            line=reader.readLine();
        }
        reader.close();
        return alldata;
    }

    
    public static Object getValue(String numCol, String dataType, List<String> data, Map<String, Calificacion> dataCalif, Map<String, Tipotasa> dataTasa) {
       String value = data.get(Integer.parseInt(numCol));       
       if(dataType.equals("s")){
            return value;
        }
        if(dataType.equals("n")){
            try{
                return Double.parseDouble(value);
            }catch(NumberFormatException num){
                System.out.println(num);
            }
        }
        if(dataType.equals("t")){
           Tipotasa get = dataTasa.get(value);
           return get;
        }
        if(dataType.equals("c")){
           Calificacion get = dataCalif.get(value);
           return get;
        }
        if(dataType.equals("d")){
           try {
               SimpleDateFormat sp=new SimpleDateFormat("yyyyMMdd");
               return sp.parse(value);
           } catch (ParseException ex) {
               return null;
           }
        }
        return null;
    }

}
