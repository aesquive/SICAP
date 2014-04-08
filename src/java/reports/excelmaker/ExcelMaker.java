package reports.excelmaker;

import db.controller.DAO;
import db.pojos.Cuenta;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Admin
 */
public class ExcelMaker {

    private String fileName;
    private String baseName;
    private Map<String, Cuenta> datos;

    public ExcelMaker(String fileName, String baseName, Map<String, Cuenta> datos) {
        this.fileName = fileName;
        this.baseName = baseName;
        this.datos = datos;
    }

    public File makeFile() throws IOException, InvalidFormatException {
        File file = new File(baseName);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);
        int maxRows = 100;
        int maxColumns = 70;
        for (int t = 0; t < maxRows; t++) {
            Row row = sheet.getRow(t);
            for (int c = 0; c < maxColumns; c++) {
                Cell cell = null;
                if (row != null) {
                    cell = row.getCell(c);
                }
                if (cell != null) {
                    boolean isFormula = isFormula(cell);
                    if (!isFormula) {
                        String cellContents = getValue(cell);
                        if (!cellContents.equals("") && cellContents.charAt(0) == '?') {
                            cell.setCellValue(mapGetValue(cellContents.substring(1, cellContents.length())));
                        }
                    }else{
                        cell.setCellFormula(cell.getCellFormula());
                    }
                }
            }
        }
        File fileOutput=new File(fileName);
        FileOutputStream fileOut = new FileOutputStream(fileOutput);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return fileOutput;
    }

    public static void main(String[] args) throws IOException, InvalidFormatException, InvalidFormatException {
        List<Cuenta> createQuery = DAO.createQuery(Cuenta.class, null);
        Map<String, Cuenta> map = new HashMap<String, Cuenta>();
        for (Cuenta c : createQuery) {
            map.put(c.getCatalogocuenta().getIdCatalogoCuenta().toString(), c);
        }
        ExcelMaker ex = new ExcelMaker("archivo.xlsx", "baseRC01.xlsx", map);
//        String makeFile = ex.makeFile();
    }

    private String getValue(Cell cell) {

        try {
            return cell.getStringCellValue();
        } catch (IllegalStateException ex) {
            return String.valueOf(cell.getNumericCellValue());
        }

    }

    private double mapGetValue(String substring) {
        Cuenta get = datos.get(substring);
        if (get != null) {
            return get.getValor();
        }
        return -1.0;
    }

    private boolean isFormula(Cell cell) {
        try{
            cell.getCellFormula();
            cell.setCellType(Cell.CELL_TYPE_FORMULA);
            return true;
        }catch(IllegalStateException is){
            return false;
        }
    }

}
