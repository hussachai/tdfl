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

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVWriter;

import com.siberhus.tdfl.AbstractDataFileWriter;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class CSVDataFileWriter extends AbstractDataFileWriter {

	private CSVWriter writer = null;
	
	private boolean append = false;
	
	private String charset = "UTF-8";
	
	private char separator = CSVWriter.DEFAULT_SEPARATOR;
	
	private char quoteChar = CSVWriter.DEFAULT_QUOTE_CHARACTER; 
	
	private char escapeChar = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
	
	private String lineEnd = CSVWriter.DEFAULT_LINE_END;
	
	public CSVDataFileWriter(){}
	
	public CSVDataFileWriter(Resource resource){
		this.resource = resource;
	}
	
	public void setCharset(String charset){
		this.charset = charset;
	}
	
	public void setAppend(boolean append){
		this.append = append;
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

	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

	@Override
	public boolean isOpen() {
		
		return writer!=null;
	}
	
	@Override
	protected void doOpen() throws Exception {
		Writer out = new OutputStreamWriter(
				new FileOutputStream(resource.getFile(), append), charset);
		writer = new CSVWriter(out, separator, quoteChar, escapeChar, lineEnd);
	}
	
	@Override
	protected void doWrite(String[] values) throws Exception {
		if(writer==null){
			throw new IllegalStateException("Writer must be opened before writing");
		}
		writer.writeNext(values);
	}
	
	@Override
	protected void doClose() throws Exception {
		try{
			writer.close();
		}finally{
			writer = null;
		}
	}
	
}
