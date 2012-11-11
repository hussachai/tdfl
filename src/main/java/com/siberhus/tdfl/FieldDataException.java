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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class FieldDataException extends DataFileLoaderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> fieldErrors = new LinkedHashMap<String, String>();
	
	/**
	 * Still call DataFileProcessor.processItem(item) even though there are errors.
	 * Apply only instance that DataFileLoader create not user's instance. 
	 */
	private boolean forceProcess = false;
	
	public FieldDataException(){}
	
	public FieldDataException(String fieldName, String error){
		fieldErrors.put(fieldName, error);
	}
	
	public FieldDataException(String fieldName, Throwable e){
		fieldErrors.put(fieldName, translate(e));
	}
	
	public boolean hasError(String fieldName){
		return fieldErrors.containsKey(fieldName);
	}
	
	public boolean hasErrors(){
		return fieldErrors.size()>0;
	}
	
	public void addError(String fieldName, String error){
		//we can highlight error cell in excel.
		fieldErrors.put(fieldName, error);
	}
	
	public void addError(String fieldName, Throwable e){
		fieldErrors.put(fieldName, translate(e));
	}
	
	public Collection<String> getErrorFields(){
		return fieldErrors.keySet();
	}
	
	public void merge(FieldDataException e){
		if(e.hasErrors()){
			for(String errorKey: e.fieldErrors.keySet()){
				this.fieldErrors.put(errorKey, e.fieldErrors.get(errorKey));
			}
		}
	}
	
	public boolean isForceProcess() {
		return forceProcess;
	}

	public void setForceProcess(boolean forceProcess) {
		this.forceProcess = forceProcess;
	}

	public String toString(){
		return fieldErrors.toString();
	}
	
	protected static String translate(Throwable e){
		Throwable rootCause = ExceptionUtils.getRootCause(e);
		if(rootCause!=null){
			e = rootCause;
		}
		if(!StringUtils.isBlank(e.getMessage())){
			return e.getMessage();
		}
		return e.getClass().getSimpleName();
	}
	
}
