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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DefaultDataFileHandler implements DataFileHandler {
	
	private DataFileReader reader;
	
	private ResourceCreator successResourceCreator = new DefaultResourceCreator("success");
	
	private DataFileWriter successWriter;
	
	private ResourceCreator errorResourceCreator = new DefaultResourceCreator("error");
	
	private DataFileWriter errorWriter;
	
	private boolean ignoreCase = true;
	
	private Set<String> extensions;
	
	public void setIgnoreCase(boolean ignoreCase){
		this.ignoreCase = ignoreCase;
	}
	
	@Override
	public boolean accept(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		if(ignoreCase){
			return extensions.contains(extension.toUpperCase());
		}else{
			return extensions.contains(extension);
		}
	}
	
//	public void setExtensionsString(String extensions){
//		setExtensions(extensions.split(","));
//	}
	
	public void setExtensions(String[] extensions){
		this.extensions = new HashSet<String>();
		for(String ext : extensions){
			if(ignoreCase){
				this.extensions.add(ext.toUpperCase());
			}else{
				this.extensions.add(ext);
			}
		}
	}

	public DataFileReader getReader() {
		return reader;
	}

	public void setReader(DataFileReader reader) {
		this.reader = reader;
	}
	
	public ResourceCreator getSuccessResourceCreator() {
		return successResourceCreator;
	}

	public void setSuccessResourceCreator(ResourceCreator successResourceCreator) {
		this.successResourceCreator = successResourceCreator;
	}

	public DataFileWriter getSuccessWriter() {
		return successWriter;
	}
	
	public void setSuccessWriter(DataFileWriter successWriter) {
		this.successWriter = successWriter;
	}
	
	public ResourceCreator getErrorResourceCreator() {
		return errorResourceCreator;
	}

	public void setErrorResourceCreator(ResourceCreator errorResourceCreator) {
		this.errorResourceCreator = errorResourceCreator;
	}

	public DataFileWriter getErrorWriter() {
		return errorWriter;
	}

	public void setErrorWriter(DataFileWriter errorWriter) {
		this.errorWriter = errorWriter;
	}
	
}
