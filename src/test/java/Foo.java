import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class Foo {
	
	public static void main(String[] args)throws Exception {
		Set<String> sentLeads = new HashSet<String>();
		Workbook wbIn = new HSSFWorkbook(new FileInputStream("D:/tmp/lead/sent.xls"));
		Sheet shIn = wbIn.getSheetAt(0);
		int i = 1;
		Row row = null;
		while( (row = shIn.getRow(i)) !=null){
			String name = row.getCell(2).getStringCellValue()+"|"+row.getCell(3).getStringCellValue();
//			String name = row.getCell(1).getStringCellValue();
			sentLeads.add(name);
			i++;
		}
		System.out.println(sentLeads.size());
		wbIn = new HSSFWorkbook(new FileInputStream("D:/tmp/lead/lead.xls"));
		shIn = wbIn.getSheetAt(0);
		Workbook wbOut = new HSSFWorkbook();
		Sheet shOut = wbOut.createSheet();
		i = 0;
		int dupCount = 0;
		int outRow  = 0;
		while( (row = shIn.getRow(i)) !=null){
			String name = row.getCell(1).getStringCellValue()+"|"+row.getCell(2).getStringCellValue();
//			String name = row.getCell(1).getStringCellValue();
			i++;
			if(sentLeads.contains(name)){
				System.out.println("Duplicated name: "+name);
				dupCount++;
				continue;
			}
			Row rowOut = shOut.createRow(outRow++);
			for(int j=0;j<=7;j++){
				if(row.getCell(j).getCellType()==Cell.CELL_TYPE_STRING){
					rowOut.createCell(j).setCellValue(row.getCell(j).getStringCellValue());
				}else{
					rowOut.createCell(j).setCellValue(row.getCell(j).toString());
				}
			}
		}
		OutputStream outStream = new FileOutputStream("D:/tmp/lead/out.xls");
		wbOut.write(outStream);
		IOUtils.closeQuietly(outStream);
		System.out.println("Dup Count = "+dupCount);
	}
}
