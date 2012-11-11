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
package com.siberhus.tdfl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.TemporalTypeConverter;


/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DflBaseTest {
	
	static final String XLS_FILE_OUT_NAME = "data/gen/processed/demo.out.xls";
	static final String XLS_FILE_IN_NAME = "data/gen/demo.xls";
	
	@BeforeClass
	public static void init(){
		
		Locale locale = Locale.US;
		
		TemporalTypeConverter utilDateConverter = new TemporalTypeConverter(Date.class);
		utilDateConverter.setLocale(locale);
		utilDateConverter.setPattern("dd/MM/yyyy");
		ConvertUtils.register(utilDateConverter, Date.class);
		
		TemporalTypeConverter sqlDateConverter = new TemporalTypeConverter(java.sql.Date.class);
		sqlDateConverter.setLocale(locale);
		sqlDateConverter.setPattern("dd/MM/yyyy");
		ConvertUtils.register(sqlDateConverter, java.sql.Date.class);
		
		TemporalTypeConverter sqlTimeConverter = new TemporalTypeConverter(java.sql.Time.class);
		sqlTimeConverter.setLocale(locale);
		sqlTimeConverter.setPattern("HH:mm:ss");
		ConvertUtils.register(sqlTimeConverter, java.sql.Time.class);
		
		TemporalTypeConverter sqlTimestampConverter = new TemporalTypeConverter(java.sql.Timestamp.class);
		sqlTimestampConverter.setLocale(locale);
		sqlTimestampConverter.setPattern("dd/MM/yyyy HH:mm:ss");
		ConvertUtils.register(sqlTimestampConverter, java.sql.Timestamp.class);
	}
	
	@AfterClass
	public static void deleteGeneratedFiles(){
		FileUtils.deleteQuietly(new File(XLS_FILE_IN_NAME));
	}
	
	@Before
	public void createXLSFile() throws Exception{
		if(new File(XLS_FILE_IN_NAME).exists()){
			return;
		}
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("Untitled");
		String dataArray[][] = getTestData();
		for(int i=0;i<dataArray.length;i++){
			String data[] = dataArray[i];
			Row row = sheet.createRow(i);
			for(int j=0;j<data.length;j++){
				row.createCell(j).setCellValue(data[j]);
			}
		}
		OutputStream out = new FileOutputStream(XLS_FILE_IN_NAME);
		workbook.write(out);
		IOUtils.closeQuietly(out);
	}
	
	public String[][] getTestData(){
		return new String[][]{
			new String[]{"Id","Fullname","Email","Birthdate","Salary"}, //labels
			new String[]{"1","John Smith","john.smith@demo.com","19/11/1985","4000"}, //perfect one
			new String[]{"2","Jason Melon","","",""},//blank fields
			new String[]{"3","Jeff Denvor","jeff.denvor@demo.com","invalid date","2000"},// invalid date field
			new String[]{"4","Micheal Jackson","micheal.j@demo.com","12/03/1843","invalid number"}, //invalid number field
			new String[]{"5","Lisa Baron","lisa.baron@demo.com"},//missing(null) fields 
			new String[]{"6","Elizabeth Graham","eliz.g@demo.com","19/02/1922","2333","trivial data",""},//extra field
			new String[]{"7","Gizmo Gomez","giz.gom@demo.com","22/02/1922","2222",null},//extra null field
		};
	}
	
	@Test
	public void testTemporalTypeConverter() throws Exception{
		java.sql.Date expectedDate = new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse("11/11/2011").getTime());
		java.sql.Date actualDate = (java.sql.Date)ConvertUtils.convert("11/11/2011", java.sql.Date.class);
		Assert.assertEquals(expectedDate, actualDate);
		try{
			ConvertUtils.convert("invalid",java.sql.Date.class);
		}catch(Exception e){
			Assert.assertEquals("org.apache.commons.beanutils.ConversionException", e.getClass().getName());
		}
	}
}
