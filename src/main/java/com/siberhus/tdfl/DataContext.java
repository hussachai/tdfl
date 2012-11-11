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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.core.io.Resource;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DataContext {
	
	protected int resourceNum;
	
	protected Resource resource;
	
	/**
	 * Lines read. Don't count label line.
	 */
	protected int linesRead = 0;
	
	/**
	 * Error lines written. Don't count label line.
	 */
	protected int errorLinesWritten = 0;
	
	/**
	 * Success lines written. Don't count label line.
	 */
	protected int successLinesWritten = 0;
	
	protected int itemErrorCount = 0;
	
	protected int itemSuccessCount = 0;
	
	protected Date startTime;
	
	protected Date finishTime;
	
	protected ExitStatus exitStatus = ExitStatus.EXECUTING;
	
	protected Map<String, Object> attributes;
	
	public DataContext(){
		this(null);
	}
	
	public DataContext(Map<String,Object> attributes){
		if(attributes!=null){
			this.attributes = attributes;
		}else{
			this.attributes = new HashMap<String, Object>();
		}
	}
	
	public int getResourceNum(){
		return resourceNum;
	}
	
	public void setAttribute(String name, Object value){
		attributes.put(name, value);
	}
	
	public Object getAttribute(String name){
		return attributes.get(name);
	}
	
	public Iterator<String> getAttributeNames(){
		return attributes.keySet().iterator();
	}
	
	public void removeAttribute(String name){
		attributes.remove(name);
	}
	
	public Resource getResource() {
		return resource;
	}

	public int getLinesRead() {
		return linesRead;
	}
	
	public int getErrorLinesWritten() {
		return errorLinesWritten;
	}

	public int getSuccessLinesWritten() {
		return successLinesWritten;
	}
	
	public int getItemErrorCount() {
		return itemErrorCount;
	}

	public int getItemSuccessCount() {
		return itemSuccessCount;
	}

	public Date getStartTime() {
		return startTime;
	}
	
	public Date getFinishTime() {
		return finishTime;
	}
	
	public ExitStatus getExitStatus() {
		return exitStatus;
	}
	
}
