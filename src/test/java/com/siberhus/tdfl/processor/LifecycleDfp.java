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

import java.util.HashMap;
import java.util.Map;

import com.siberhus.tdfl.Employee;
import com.siberhus.tdfl.FieldDataException;
import com.siberhus.tdfl.transform.FieldSet;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class LifecycleDfp extends AbstractDataFileProcessor<Employee>{
	
	public int startCount,finishCount,updateCount,processItemCount,itemSuccessCount,itemErrorCount;
	public Map<Long, String> itemStatusMap = new HashMap<Long, String>();  
	
	public LifecycleDfp(){
		startCount = finishCount = updateCount = processItemCount = itemSuccessCount = itemErrorCount = 0;
		for(long i=1;i<=7;i++){
			itemStatusMap.put(i, "new");
		}
	}
	
	@Override
	public void onStart() {
		startCount++;
		System.out.println("++++++++ onStart ++++++++");
	}
	@Override
	public void onFinish() {
		finishCount++;
		System.out.println("++++++++ onFinish ++++++++");
	}
	
	@Override
	public void update() {
		updateCount++;
	}
	
	@Override
	public Employee mapLine(FieldSet fs, FieldDataException fde)
			throws Exception {
		Employee item = new Employee();
		item.setId(fs.read(Long.class, "Id"));
		itemStatusMap.put(item.getId(), "map");
		return item;
	}
	
	@Override
	public void validateItem(Employee item, FieldDataException fde)
			throws Exception {
		itemStatusMap.put(item.getId(), "validate");
		if(item.getId()==2){
			fde.addError("Id", "severe");
		}
		if(item.getId()==3){
			fde.setForceProcess(true);
			fde.addError("Id", "warning");
		}
	}
	
	@Override
	public void processItem(Employee item) throws Exception {
		itemStatusMap.put(item.getId(), "process");
		processItemCount++;
		if(item.getId()==4){
			throw new RuntimeException("Throw error@processItem");
		}
	}
	
	@Override
	public void onItemSuccess(Employee item) {
		itemStatusMap.put(item.getId(), "success");
		itemSuccessCount++;
	}
	
	@Override
	public void onItemError(Employee item, Exception exception) {
		itemStatusMap.put(item.getId(), "error");
		itemErrorCount++;
	}
}
