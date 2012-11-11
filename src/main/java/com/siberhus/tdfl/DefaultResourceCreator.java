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
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DefaultResourceCreator implements ResourceCreator, InitializingBean {
	
	private boolean createDirectory = true;
	private String prefix;
	private String suffix;	
	private String subdirectory;
	private String extension;
	
	public DefaultResourceCreator(){}
	
	public DefaultResourceCreator(String subdirectory){
		setSubdirectory(subdirectory);
	}
	
	@Override
	public Resource create(Resource example) throws IOException{
		
		String parent = example.getFile().getParent();
		String targetDirPath = parent+File.separator+subdirectory;
		File targetDir = new File(targetDirPath);
		if(!targetDir.exists()){
			if(createDirectory){
				targetDir.mkdir();
			}
		}
		String filename = example.getFilename();
		if(extension==null)
			extension = FilenameUtils.getExtension(filename);
		filename = FilenameUtils.getBaseName(filename);		
		if(prefix!=null){
			filename = prefix + filename;
		}
		if(suffix!=null){
			filename = filename + suffix;
		}
		filename = filename + "."+ extension;
		return new FileSystemResource(targetDirPath +
				File.separator + filename);
	}
	
	public void setCreateDirectory(boolean createDirectory){
		this.createDirectory = createDirectory;
	}
	
	public void setSubdirectory(String subdirectory) {
		this.subdirectory = subdirectory;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Set this property to override default extension 
	 * that is extracted from input resource.
	 * 
	 * @param extension
	 */
	public void setExtension(String extension){
		this.extension = extension;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(subdirectory, "subdirectory is a required field.");
		if(subdirectory.startsWith(File.separator)){
			subdirectory = subdirectory.substring(1);
		}
		if(subdirectory.endsWith(File.separator)){
			subdirectory = subdirectory.substring(subdirectory.length());
		}
	}
	
}
