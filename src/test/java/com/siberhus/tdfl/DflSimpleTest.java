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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.siberhus.tdfl.processor.BreakDfp;
import com.siberhus.tdfl.processor.EmployeeBeanWrapDfp;
import com.siberhus.tdfl.processor.EmployeeDfp;
import com.siberhus.tdfl.processor.LifecycleDfp;
import com.siberhus.tdfl.processor.LongRunningDfp;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class DflSimpleTest extends DflBaseTest{
	
	
	@Autowired
	private EmployeeDfp employeeDfp;
	
	@Autowired
	private EmployeeBeanWrapDfp employeeBeanWrapDfp;
	
	@Autowired
	private LongRunningDfp longRunningDfp;
	
	@Autowired
	private DataFileLoader<Employee> employeeDfl;
	
	@Autowired
	private Collection<DataFileHandler> handlers;
	
	@Test
	public void testLoadXlsFileToBeanManually() throws DataFileLoaderException{
		
		employeeDfl.setDataFileProcessor(employeeDfp);
		employeeDfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
//		employeeDfl.setDestination(new FileSystemResource(XLS_FILE_OUT_NAME));
		employeeDfl.load();
		
	}
	
	@Test
	public void testLoadXlsFileToBeanAutomatically() throws DataFileLoaderException{
		
		employeeDfl.setDataFileProcessor(employeeBeanWrapDfp);
		employeeDfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
//		employeeDfl.setDestination(new FileSystemResource(XLS_FILE_OUT_NAME));
		employeeDfl.load();
		
//		File processedFile = new File(XLS_FILE_OUT_NAME);
//		Assert.assertTrue(processedFile.exists());
//		FileUtils.deleteQuietly(processedFile);	
	}
	
	
	@Test
	public void testStopLoadingFile() throws DataFileLoaderException{
		Runnable dfl = new Runnable(){
			public void run(){
				employeeDfl.setDataFileProcessor(longRunningDfp);
				employeeDfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
				try{
					employeeDfl.load();
					Assert.fail();
				}catch(DataFileLoaderException e){
					Assert.assertEquals(2, longRunningDfp.getSuccessCounter());
					Assert.assertEquals("Interrupt exception", "STOP", e.getMessage());
				}
			}
		};
		Thread thread = new Thread(dfl);
		thread.start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		thread.interrupt();
	}
	
	
	@Test
	public void testDfpLifeCycle() throws DataFileLoaderException {
		LifecycleDfp dfp = new LifecycleDfp();
		employeeDfl.setDataFileProcessor(dfp);
		employeeDfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
		employeeDfl.load();
		System.out.println("++++++++ Summary ++++++++");
		System.out.println("Start count = "+dfp.startCount);
		System.out.println("Finish count = "+dfp.finishCount);
		System.out.println("Update count = "+dfp.updateCount);
		System.out.println("Process item count = "+dfp.processItemCount);
		System.out.println("Item success count = "+dfp.itemSuccessCount);
		System.out.println("Item error count = "+dfp.itemErrorCount);
		System.out.println(dfp.itemStatusMap);
	}
	
	
	@Test
	public void testStopLoader() throws DataFileLoaderException{
		BreakDfp dfp = new BreakDfp();
		employeeDfl.setDataFileProcessor(dfp);
		employeeDfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
		employeeDfl.load();
		Assert.assertEquals(5, dfp.processItemCount);
		Assert.assertTrue(dfp.onFinishExecuted);
	}
	
	@Test
	public void testOverrideLoader(){
		DataFileLoader<Employee> dfl = new DataFileLoader<Employee>(){
			@Override
			protected void doReadProcessWrite(DataContext dataContext,
					DataFileReader reader, DataFileWriter successWriter,
					DataFileWriter errorWriter) throws Exception {
				super.doReadProcessWrite(dataContext, reader, successWriter, errorWriter);
			}
		};
		dfl.setDataFileHandlers(handlers);
		dfl.setDataFileProcessor(employeeDfp);
		dfl.setResource(new FileSystemResource(XLS_FILE_IN_NAME));
		dfl.load();
		
	}
}
