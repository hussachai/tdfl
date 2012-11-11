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
package com.siberhus.tdfl.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.siberhus.tdfl.Employee;
import com.siberhus.tdfl.FieldDataException;
import com.siberhus.tdfl.processor.AbstractDataFileProcessor;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class EmployeeBeanWrapDfp extends AbstractDataFileProcessor<Employee>{

	private Log logger = LogFactory.getLog(EmployeeBeanWrapDfp.class);
	
	@Override
	public void validateItem(Employee item, FieldDataException fde)
			throws Exception {
		if(item.getEmail()==null){
			fde.addError("email", "missing required field");
		}
	}
	
	@Override
	public void processItem(Employee item) throws Exception {
		logger.info("Saving 'employee' data-> "+item);
	}

	@Override
	public void onItemError(Employee item, Exception exception) {
		logger.warn("Item error -> "+item);
	}
	
	
}
