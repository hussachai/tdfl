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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public abstract class AbstractDataFileReader implements DataFileReader {
	
	private static final Log logger = LogFactory.getLog(AbstractDataFileReader.class);
	
	protected Resource resource;
	
	protected boolean readLabels = true;
	
	protected int linesRead = 0;
	
	protected boolean noInput = false;
	
	protected boolean strict = false;
	
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setReadLabels(boolean readLabels){
		this.readLabels = readLabels;
	}
	
	@Override
	public boolean isReadLabels() {
		return readLabels;
	}
	
	@Override
	public int getLinesRead(){
		return linesRead;
	}
	
	@Override
	public void open(DataContext dataContext) throws Exception {
		
		Assert.notNull(resource, "Input resource must be set");
		noInput = false;
		if (!resource.exists()) {
			if (strict) {
				throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
			}
			noInput = true;
			logger.warn("Input resource does not exist " + resource.getDescription());
			return;
		}

		if (!resource.isReadable()) {
			if (strict) {
				throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): " + resource);
			}
			noInput = true;
			logger.warn("Input resource is not readable " + resource.getDescription());
			return;
		}
		
		doOpen();
	}
	
	@Override
	public void close() throws Exception{
		if(!isOpen()) return;
		try{
			doClose();
		}finally{
			linesRead = 0;
		}
	}
	
	@Override
	public void update(DataContext dataContext) {
		//nothing for now
	}
	
	@Override
	public String[] read() throws Exception {
		if (noInput) {
			return null;
		}
		
		String data[] = doRead();
		linesRead++;
		
		return data;
	}
	
	protected abstract void doOpen() throws Exception ;
	
	protected abstract String[] doRead() throws Exception ;
	
	protected abstract void doClose() throws Exception ;
}
