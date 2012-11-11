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
import com.siberhus.tdfl.FieldDataException;
import com.siberhus.tdfl.mapping.LineMapper;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public interface DataFileProcessor<T> extends LineMapper<T>{
	
	public void beforeLoading();
	
	public void afterLoading();
	
	public void onStart() throws Exception;
	
	public void onFinish() throws Exception;
	
	public void validateItem(T item, FieldDataException fde) throws Exception ;
	
	public void processItem(T item) throws Exception ;
	
	public void update();
	/**
	 * Do after mapLine,validateItem or processItem in DataFileProcessor
	 * raise an exception. re-throw this exception to stop the job. 
	 * @param item
	 * @param exception
	 */
	public void onItemError(T item, Exception exception);
	
	public void onItemSuccess(T item);
	
	public void onCancel(CancelException e);
	
	public void onInterrupt(InterruptedException e);
	
	
}
