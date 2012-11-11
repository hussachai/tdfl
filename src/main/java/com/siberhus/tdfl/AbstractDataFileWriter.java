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

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public abstract class AbstractDataFileWriter implements DataFileWriter {
	
	protected Resource resource;
	
	protected int linesWritten = 0;
	
	protected boolean shouldDeleteIfEmpty = true;
	
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setShouldDeleteIfEmpty(boolean shouldDeleteIfEmpty) {
		this.shouldDeleteIfEmpty = shouldDeleteIfEmpty;
	}
	
	@Override
	public int getLinesWritten(){
		return linesWritten;
	}
	
	@Override
	public void open(DataContext dataContext) throws Exception {
		Assert.notNull(resource, "The resource must be set");
		doOpen();
	}
	
	@Override
	public void close() throws Exception {
		
		if(!isOpen()) return;
		try{
			doClose();
			
			if (shouldDeleteIfEmpty && linesWritten == 0) {
				if(resource.exists()){
					resource.getFile().delete();
				}
			}
		}finally{
			//reset
			linesWritten = 0;
		}
	}
	
	@Override
	public void update(DataContext dataContext) {
		//nothing for now
	}
	
	@Override
	public void writeLabels(String[] labels) throws Exception {
		doWrite(labels);
	}
	
	@Override
	public void write(String[] values) throws Exception {

		doWrite(values);
		linesWritten++;
	}
	
	protected abstract void doOpen() throws Exception ;
	
	protected abstract void doWrite(String[] values) throws Exception ;
	
	protected abstract void doClose() throws Exception ;
	
}
