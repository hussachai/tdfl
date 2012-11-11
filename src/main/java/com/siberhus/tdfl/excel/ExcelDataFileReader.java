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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.io.Resource;

import com.siberhus.tdfl.AbstractDataFileReader;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class ExcelDataFileReader extends AbstractDataFileReader {
	
	private static final Log logger = LogFactory.getLog(ExcelDataFileReader.class);
	
	private Sheet sheet;
	
	private Iterator<Row> rowIterator;
	
	private ExcelWorkbookFactory workbookFactory = new DefaultExcelWorkbookFactory();
	
	private ExcelRowReader rowReader = new DefaultExcelRowReader();
	
	public ExcelDataFileReader(){}
	
	public ExcelDataFileReader(Resource resource){
		this.resource = resource;
	}
	
	public ExcelDataFileReader(Resource resource, boolean readLabels){
		this.resource = resource;
		this.readLabels = readLabels;
	}
	
	public void setRowReader(ExcelRowReader rowReader){
		this.rowReader = rowReader;
	}
	
	@Override
	public boolean isOpen() {
		
		return sheet!=null;
	}
	
	@Override
	protected void doOpen() throws Exception {
		
		sheet = workbookFactory.createAndRead(resource).getSheetAt(0);
		rowIterator = sheet.rowIterator();		
	}
	
	@Override
	protected String[] doRead() throws Exception {
		
		if (rowIterator == null) {
			throw new IllegalStateException("Read must be opened before reading");
		}		
		Row row = null;		
		try {			
			row = rowIterator.next();
		}catch (NoSuchElementException e) {
			return null;
		}
		
		try{
			return rowReader.readRow(row);
		}catch(Exception ex){
			logger.error("Parsing error at row: " + (linesRead+1) + " in resource=" + 
					resource.getDescription() + ", input=[" + row + "]", ex);
			throw ex;
		}
	}

	@Override
	protected void doClose() {
		sheet = null;
		rowIterator = null;
	}
	
}
