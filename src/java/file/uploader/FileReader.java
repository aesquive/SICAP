package file.uploader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class FileReader {
    
    public static String[][] readFile(String fileName , String delimiter) throws FileNotFoundException, IOException{
        BufferedReader reader=new BufferedReader(new java.io.FileReader(fileName));
        List<String> rows=new LinkedList<String>();
        String line=reader.readLine();
        while(line!=null && !line.equals("")){
            rows.add(line);
            line=reader.readLine();
        }
        reader.close();
        return splitList(rows,delimiter);
    }

    private static String[][] splitList(List<String> rows,String delimiter) {
        String[][] array=new String[rows.size()][rows.get(0).split(delimiter).length];
        for(int t=0;t<rows.size();t++){
            String[] split = rows.get(t).split(delimiter);
            for(int z=0;z<split.length;z++){
                array[t][z]=split[z];
            }
        }
        return array;
    }
}
