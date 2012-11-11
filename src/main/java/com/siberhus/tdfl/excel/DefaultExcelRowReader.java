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

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DefaultExcelRowReader implements ExcelRowReader {
	
	@Override
	public String[] readRow(Row row) {
		
		return getStringValues(row);
	}
	
	private String[] getStringValues(Row row){
		String values[] = null;
		values = new String[row.getLastCellNum()];
		for(int i=0;i<row.getLastCellNum();i++){
			values[i] = getCellValueAsString(row.getCell(i));
		}
		return values;
	}
	
	private String getCellValueAsString(Cell cell) {
		Object value = getCellValue(cell);
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ObjectUtils.toString(value);
		} else if (value instanceof Date) {			
			return ObjectUtils.toString(ConvertUtils.convert(value, String.class));
		} else {
			return value.toString();
		}
	}
	
	private Object getCellValue(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				RichTextString rts = cell.getRichStringCellValue();
				if (rts != null) {
					return rts.getString();
				}
				return null;
			case Cell.CELL_TYPE_NUMERIC:
				String value = cell.toString();
				/*
				 * In POI we cannot know which cell is date or number because both
				 * cells have numeric type To fix this problem we need to call
				 * toString if it's number cell we can parse it but if it's date
				 * cell we cannot parse the value with number parser
				 */
				try {
					return new BigDecimal(value);
				} catch (Exception e) {
					return cell.getDateCellValue();
				}
			case Cell.CELL_TYPE_BLANK:
				return null;
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_FORMULA:
				return cell.getCellFormula();
		}
		return null;
	}
	
}
