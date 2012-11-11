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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;

import com.siberhus.tdfl.UnknownFileTypeException;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DefaultExcelWorkbookFactory implements ExcelWorkbookFactory {
	
	@Override
	public Workbook create(Resource resource) throws IOException {
		String ext = getFileExtension(resource);
		Workbook wb = null;
		if("XLS".equals(ext)){
			wb = new HSSFWorkbook();
		}else if("XLSX".equals(ext)){
			wb = new XSSFWorkbook();
		}else{
			throw new UnknownFileTypeException("Support only *.xls and *.xlsx");
		}
		return wb;
	}
	
	@Override
	public Workbook createAndRead(Resource resource) throws IOException {
		String ext = getFileExtension(resource);
		InputStream in = resource.getInputStream();
		Workbook wb = null;
		if("XLS".equals(ext)){
			wb = new HSSFWorkbook(in);
		}else if("XLSX".equals(ext)){
			wb = new XSSFWorkbook(in);
		}else{
			throw new UnknownFileTypeException("Support only *.xls and *.xlsx");
		}
		IOUtils.closeQuietly(in);
		return wb;
	}
	
	private String getFileExtension(Resource resource){
		String ext = FilenameUtils.getExtension(resource.getFilename());
		ext = ext.toUpperCase();
		return ext;
	}
	

}
