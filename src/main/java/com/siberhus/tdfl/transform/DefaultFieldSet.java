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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DefaultFieldSet implements FieldSet{
	
	private String values[];
	
	private List<String> labels;
	
	public DefaultFieldSet(){
		
	}
	public DefaultFieldSet(String values[]){
		this.values = values == null ? null : (String[]) values.clone();
	}
	
	public DefaultFieldSet(String values[], String labels[]){
		Assert.notNull(values);
		Assert.notNull(labels);
		
		this.values = (String[]) values.clone();
		
		if( values.length < labels.length){
			int addSize = labels.length - values.length;
			values = (String[])ArrayUtils.addAll(values, new String[addSize]);
		}
//		if (values.length != labels.length) {
//			throw new IllegalArgumentException("Field labels must be same length as values: labels="
//					+ Arrays.asList(labels) + ", values=" + Arrays.asList(values));
//		}
		
		this.labels = Arrays.asList(labels);
	}
	
	@Override
	public String[] getLabels() {
		if (labels == null) {
			throw new IllegalStateException("Field labels are not known");
		}
		return labels.toArray(new String[labels.size()]);
	}
	
	@Override
	public boolean hasLabels() {
		return labels != null;
	}
	
	@Override
	public String[] getValues() {
		return values.clone();
	}
	
	@Override
	public int getFieldCount() {
		return values.length;
	}
	
	@Override
	public String readString(int index) {
		return readAndTrim(index);
	}
	
	@Override
	public String readString(int index, String defaultValue){
		String value = readAndTrim(index);
		if(value==null){
			return defaultValue;
		}
		return value;
	}
	
	@Override
	public String readString(String name) {
		return readString(indexOf(name));
	}
	
	@Override
	public String readString(String name, String defaultValue) {
		return readString(indexOf(name), defaultValue);
	}
	
	@Override
	public String readRawString(int index) {
		return values[index];
	}
	
	@Override
	public String readRawString(String name) {
		return readRawString(indexOf(name));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <D>D read(Class<D> clazz, int index){
		String value = readString(index);
		if(value==null){
			return null;
		}
		return (D)ConvertUtils.convert(value, clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <D>D read(Class<D> clazz, int index, D defaultValue){
		String value = readString(index);
		if(value==null){
			return defaultValue;
		}
		return (D)ConvertUtils.convert(value, clazz);
	}
	
	@Override
	public <D>D read(Class<D> clazz, String name){
		return read(clazz, indexOf(name));
	}
	
	@Override
	public <D>D read(Class<D> clazz, String name, D defaultValue){
		return read(clazz, indexOf(name), defaultValue);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <D>D[] readArray(Class<D> clazz, int index, String separator){
		String subValues[] = StringUtils.split(readString(index), separator);
		if(subValues==null){
			return null;
		}
		return (D[])ConvertUtils.convert(subValues, clazz);
	}
	
	@Override
	public <D>D[] readArray(Class<D> clazz, String name, String separator){
		return readArray(clazz, indexOf(name), separator);
	}
	
	@Override
	public Properties getProperties() {
		if (labels == null) {
			throw new IllegalStateException("Cannot create properties without labels");
		}
		Properties props = new Properties();
		for (int i = 0; i < values.length; i++) {
			String value = readAndTrim(i);
			if (value != null) {
				props.setProperty((String) labels.get(i), value);
			}
		}
		return props;
	}
	
	@Override
	public int indexOf(String label) {
		if (labels == null) {
			throw new IllegalArgumentException("Cannot access columns by name without meta data");
		}
		int index = labels.indexOf(label);
		if (index >= 0) {
			return index;
		}
//		throw new IllegalArgumentException("Cannot access column [" + label + "] from " + labels);
		return -1;
	}
	
	protected String readAndTrim(int index) {
		if(index==-1) return null;
		if(index<values.length)
			return StringUtils.trimToNull(values[index]);
		else
			return null;
	}
	
	
}
