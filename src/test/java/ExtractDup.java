import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class ExtractDup {
	
	static String[] extractRow(Row row){
		String[] values = new String[10];
		for(int i=0;i<10;i++){
			Cell cell = row.getCell(i);
			if(cell==null){
				values[i] = null;
				continue;
			}
			if(cell.getCellType()==Cell.CELL_TYPE_STRING){
				values[i] = cell.getStringCellValue();
			}else{
				values[i] = cell.toString();
			}
		}
		return values;
	}
	
	public static void main(String[] args)throws Exception {
		List<String[]> dupValues = new ArrayList<String[]>();
		Set<String> sentLeads = new HashSet<String>();
		
		String files[] = new String[]{"D:/tmp/kbank/a.xls","D:/tmp/kbank/b.xls"};
		for(String file: files){
			Workbook wbIn = new HSSFWorkbook(new FileInputStream(file));
			Sheet shIn = wbIn.getSheetAt(0);
			Row row = null;
			int i = 1;
			while( (row = shIn.getRow(i)) !=null){
				String name = row.getCell(1).getStringCellValue();
				
				if(sentLeads.contains(name)){
					dupValues.add(extractRow(row));
//					System.out.println(name);
				}else{
					sentLeads.add(name);
				}
				i++;
			}
		}
		
		Workbook wbOut = new HSSFWorkbook();
		Sheet shOut = wbOut.createSheet();
		for(int i=0;i<dupValues.size();i++){
			String values[] = dupValues.get(i);
			Row row = shOut.createRow(i);
			for(int j=0;j<10;j++){
				String value = values[j];
				Cell cell = row.createCell(j);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(value);
			}
		}
		OutputStream outStream = new FileOutputStream("D:/tmp/kbank/out.xls");
		wbOut.write(outStream);
		IOUtils.closeQuietly(outStream);
	}
}
