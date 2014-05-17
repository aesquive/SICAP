package model.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase que se encarga de procesar las formulas de una hoja de calculo en su hoja 0 en toda la columna A solamente
 * 
 * @author Admin
 */
public class ExcelInteraction {

    private String fileName;

    public ExcelInteraction(String fileName) {
        this.fileName = fileName;
        System.out.println(fileName);
    }

    /**
     * obtiene la informacion del archivo de excel base para los calculos del
     * modelo, siempre la hoja 0 es la que debera contener toda la informacion a
     * tomar en la columna A empezando de la fila 0
     *
     * @return
     * @throws IOException
     */
    public Map<String, Double> getModelExcelData() throws IOException {
        Map<String, Double> map = new HashMap<String, Double>();
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        while (cell != null && row != null) {
            cell = row.getCell(0);
            int cellType = cell.getCellType();
            switch (cellType) {
                case Cell.CELL_TYPE_FORMULA:
                    CellValue evaluate = evaluator.evaluate(cell);
                    map.put(row.getCell(1).getStringCellValue(), evaluate.getNumberValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    map.put(String.valueOf(row.getCell(1).getNumericCellValue()), cell.getNumericCellValue());
                    break;
                default:break;
            }
            int numRow = cell.getRow().getRowNum() + 1;
            row = sheet.getRow(numRow);
        }
        return map;
    }

}
