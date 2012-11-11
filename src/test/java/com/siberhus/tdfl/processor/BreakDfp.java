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

import com.siberhus.tdfl.CancelException;
import com.siberhus.tdfl.Employee;
import com.siberhus.tdfl.FieldDataException;
import com.siberhus.tdfl.transform.FieldSet;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class BreakDfp extends AbstractDataFileProcessor<Employee> {
	
	public int processItemCount = 0;
	public boolean onFinishExecuted = false;
	
	@Override
	public Employee mapLine(FieldSet fs, FieldDataException fde)
			throws Exception {
		return new Employee();
	}
	@Override
	public void validateItem(Employee item, FieldDataException fde)
			throws Exception {
		if(processItemCount==5){
			throw new CancelException("Trivial exception");
		}
	}
	@Override
	public void processItem(Employee item) throws Exception {
		processItemCount++;
	}
	@Override
	public void onFinish() {
		onFinishExecuted = true;
	}
	
	
}
