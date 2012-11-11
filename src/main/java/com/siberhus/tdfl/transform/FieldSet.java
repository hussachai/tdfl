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
package com.siberhus.tdfl.transform;

import java.util.Properties;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public interface FieldSet {

	public int getFieldCount();
	
	public String[] getLabels();

	public boolean hasLabels();

	public String[] getValues();
	
	public String readString(int index);

	public String readString(int index, String defaultValue);

	public String readString(String label);

	public String readString(String label, String defaultValue);
	
	public String readRawString(int index);

	public String readRawString(String label);
	
	public <D> D read(Class<D> clazz, int index);
	
	public <D> D read(Class<D> clazz, int index, D defaultValue);

	public <D> D read(Class<D> clazz, String label);

	public <D> D read(Class<D> clazz, String label, D defaultValue);
	
	public <D>D[] readArray(Class<D> clazz, int index, String separator);
	
	public <D>D[] readArray(Class<D> clazz, String label, String separator);
	
	public Properties getProperties();
	
	public int indexOf(String label);
}
