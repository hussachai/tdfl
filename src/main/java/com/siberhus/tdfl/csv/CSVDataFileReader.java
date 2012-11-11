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
package com.siberhus.tdfl.csv;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import com.siberhus.tdfl.AbstractDataFileReader;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class CSVDataFileReader extends AbstractDataFileReader {
	
	private CSVReader reader = null;
	
	private String charset = "UTF-8";
	
	private char separator = CSVParser.DEFAULT_SEPARATOR;
	
	private char quoteChar = CSVParser.DEFAULT_QUOTE_CHARACTER; 
	
	private char escapeChar = CSVParser.DEFAULT_ESCAPE_CHARACTER;
	
	private int skipLine = 0;
	
	private boolean strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
	
	public CSVDataFileReader(){}
	
	public CSVDataFileReader(Resource resource){
		super.resource = resource;
	}
	
	public CSVDataFileReader(Resource resource, boolean readLabels){
		super.resource = resource;
		super.readLabels = readLabels;
	}
	
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	public void setReader(CSVReader reader) {
		this.reader = reader;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public void setQuoteChar(char quoteChar) {
		this.quoteChar = quoteChar;
	}

	public void setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
	}

	public void setSkipLine(int skipLine) {
		this.skipLine = skipLine;
	}

	public void setStrictQuotes(boolean strictQuotes) {
		this.strictQuotes = strictQuotes;
	}
	
	@Override
	public boolean isOpen() {
		return reader!=null;
	}
	
	@Override
	protected void doOpen() throws Exception {
		InputStreamReader in = new InputStreamReader(
				new FileInputStream(resource.getFile()) , charset);
		reader = new CSVReader(in, separator, quoteChar, 
				escapeChar, skipLine, strictQuotes);
	}
	
	@Override
	protected String[] doRead() throws Exception {
		if(reader==null){
			throw new IllegalStateException("Read must be opened before reading");
		}
		return reader.readNext();
	}
	
	@Override
	protected void doClose() throws Exception {
		try{
			if(reader!=null) reader.close();
		}catch(Exception e){
			throw e;
		}finally{
			reader = null;
		}
	}
	
}
