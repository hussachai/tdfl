/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.siberhus.tdfl.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import com.siberhus.tdfl.AbstractDataFileWriter;
import com.siberhus.tdfl.DataFileLoaderException;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class ExcelDataFileWriter extends AbstractDataFileWriter {
	
	private Workbook workbook;
	
	private String sheetName = "Untitled";
	
	private Sheet sheet;
	
	private int currentRowIndex = 0;
	
	private OutputStream outputStream;
	
	private ExcelWorkbookFactory workbookFactory = new DefaultExcelWorkbookFactory();
	
	public ExcelDataFileWriter(){}
	
	public ExcelDataFileWriter(Resource resource){
		this.resource = resource;
	}
	
	public void setSheetName(String sheetName){
		this.sheetName = sheetName;
	}
	
	/**
	 * 
	 * @param workbookFactory the workbookFactory to set
	 */
	public void setWorkbookFactory(ExcelWorkbookFactory workbookFactory) {
		this.workbookFactory = workbookFactory;
	}
	
	@Override
	public boolean isOpen() {
		
		return sheet!=null;
	}
	
	@Override
	protected void doOpen() throws Exception {
		try {
			workbook = workbookFactory.create(resource);
			sheet = workbook.createSheet(sheetName);			
		} catch (IOException e) {
			throw new DataFileLoaderException("Failed to initialize writer", e);
		}
	}
	
	@Override
	protected void doWrite(String[] values) {
		if(sheet==null){
			throw new IllegalStateException("Writer must be opened before writing");
		}
		Row row = createNextRow();
		for(int i=0;i<values.length;i++){
			Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
			cell.setCellValue(values[i]);			
		}
	}
	
	@Override
	protected void doClose() throws Exception{
		if (shouldDeleteIfEmpty && linesWritten == 0) {
			return;
		}
		try {
			File file = resource.getFile();
			outputStream = new FileOutputStream(file.getAbsolutePath(), false);
			workbook.write(outputStream);
		} catch (IOException e) {
			throw new DataFileLoaderException("Failed to write workbook to file", e);
		}finally{
			IOUtils.closeQuietly(outputStream);
			//reset
			currentRowIndex = 0;
			workbook = null;
			sheet = null;
		}
	}
	
	private Row createNextRow(){
		Row row = sheet.createRow(currentRowIndex);
		currentRowIndex++;
		return row;
	}

	
	
}
