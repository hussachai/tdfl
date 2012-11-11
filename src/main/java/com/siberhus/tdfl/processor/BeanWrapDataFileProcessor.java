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

import org.springframework.beans.factory.BeanFactory;

import com.siberhus.tdfl.mapping.BeanWrapLineMapper;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public abstract class BeanWrapDataFileProcessor<T> extends AbstractDataFileProcessor<T>{
	
	private BeanWrapLineMapper<T> lineMapper;
	
	public BeanWrapDataFileProcessor(){
		lineMapper = new BeanWrapLineMapper<T>();
		setLineMapper(lineMapper);
	}
	
	public void setBeanName(String name){
		lineMapper.setPrototypeBeanName(name);
	}
	
	public void setBeanFactory(BeanFactory beanFactory){
		lineMapper.setBeanFactory(beanFactory);
	}
	
	public void setTargetType(Class<T> type){
		lineMapper.setTargetType(type);
	}
	
	public void setStrict(boolean strict){
		lineMapper.setStrict(strict);
	}
	
}
