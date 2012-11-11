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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.siberhus.tdfl.processor.DataFileProcessor;
import com.siberhus.tdfl.transform.DefaultFieldSet;
import com.siberhus.tdfl.transform.FieldSet;

/**
 * 
 * @author Hussachai Puripunpinyo (http://www.siberhus.com)
 * 
 */
public class DataFileLoader<T> {
	
	private static final Log logger = LogFactory.getLog(DataFileLoader.class);
	
	private FilenameFilter directoryListingFilter;
	
	private Resource resource;
	
	//if this value is not null, the resource will be moved here
	private Resource destination;
	
	private boolean shouldDeleteIfFinish = false;
	
	//StopOnError must be true,if the sequence of file,to be processing, is important.
	private boolean stopOnError = false;
	
	//Call update method on DataFileStream every success read/process/write time interval
	private int updateInterval = 1;
	
	private Collection<DataFileHandler> dataFileHandlers;
	
	private DataFileProcessor<T> dataFileProcessor;
	
	private Map<String, Object> attributes;
	
	private Comparator<Resource> comparator = new Comparator<Resource>() {
		
		/**
		 * Compares resource filenames.
		 */
		public int compare(Resource r1, Resource r2) {
			return r1.getFilename().compareTo(r2.getFilename());
		}
		
	};
	
	public void setResource(Resource resource){
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}

	public void setDirectoryListingFilter(FilenameFilter directoryListingFilter){
		this.directoryListingFilter = directoryListingFilter;
	}
	public void setDestination(Resource destination) {
		this.destination = destination;
	}
	
	public Resource getDestination() {
		return destination;
	}

	public void setShouldDeleteIfFinish(boolean shouldDeleteIfFinish) {
		this.shouldDeleteIfFinish = shouldDeleteIfFinish;
	}
	
	public boolean isShouldDeleteIfFinish() {
		return shouldDeleteIfFinish;
	}

	public void setStopOnError(boolean stopOnError){
		this.stopOnError = stopOnError;
	}
	
	public boolean isStopOnError() {
		return stopOnError;
	}

	public void setUpdateInterval(int updateInterval){
		this.updateInterval = updateInterval;
	}
	
	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setDataFileHandlers(Collection<DataFileHandler> dataFileHandlers){
		this.dataFileHandlers = dataFileHandlers;
	}
	
	public Collection<DataFileHandler> getDataFileHandlers() {
		return dataFileHandlers;
	}

	public void setDataFileProcessor(DataFileProcessor<T> dataFileProcessor){
		this.dataFileProcessor = dataFileProcessor;
	}
	
	public DataFileProcessor<T> getDataFileProcessor() {
		return dataFileProcessor;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param comparator used to order the injected resources, by default
	 * compares {@link Resource#getFilename()} values.
	 */
	public void setComparator(Comparator<Resource> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * 
	 */
	public void load() {	
		
		dataFileProcessor.beforeLoading();
		try{
			_load();
		}finally{
			dataFileProcessor.afterLoading();
		}
	}
	
	/**
	 * 
	 */
	private void _load(){
		Resource sources[] = getSources();
		if(sources==null){
			return;
		}
		
		DataFileReader reader = null;
		DataFileWriter successWriter = null, errorWriter = null;
		
		for(int i=0;i<sources.length;i++){
			Resource source = sources[i];
			try{
				String filename = source.getFilename();
				DataFileHandler fileHandler = null;
				for(DataFileHandler fh : dataFileHandlers){
					if(fh.accept(filename)){
						fileHandler = fh;
						break;
					}
				}
				if(fileHandler==null){
					throw new DataFileLoaderException("No handler found for filename: "+filename);
				}
				
				reader = fileHandler.getReader();
				reader.setResource(source);
				successWriter = fileHandler.getSuccessWriter();
				if(successWriter!=null){
					Resource successResource = fileHandler.getSuccessResourceCreator().create(source);
					successWriter.setResource(successResource);
				}
				errorWriter = fileHandler.getErrorWriter();
				if(errorWriter!=null){
					Resource errorResource = fileHandler.getErrorResourceCreator().create(source);
					errorWriter.setResource(errorResource);
				}
				
				DataContext dataContext = new DataContext(this.attributes);
				dataContext.resource = source;
				dataContext.resourceNum = i;
				dataContext.startTime = new Date();
				if(dataFileProcessor instanceof DataContextAware){
					((DataContextAware)dataFileProcessor).setDataContext(dataContext);
				}
				try{
					doReadProcessWrite(dataContext, reader, successWriter, errorWriter);
					dataContext.exitStatus = ExitStatus.COMPLETED;
				}catch(Exception e){
					dataContext.exitStatus = ExitStatus.FAILED;
					throw e;
				}finally{
					dataContext.finishTime = new Date();
					//may persist dataContext here
					
					if(reader!=null) try{ reader.close();}catch(Exception e){}
					if(successWriter!=null) try{ successWriter.close();}catch(Exception e){}
					if(errorWriter!=null) try{ errorWriter.close();}catch(Exception e){}
				}
				
				if(shouldDeleteIfFinish){
					logger.info("Deleting file "+source);
					source.getFile().delete();
				}else if(destination!=null){
					File destFile = destination.getFile();
					logger.debug("Moving file: "+source.getFile().getCanonicalPath()+
							" to file: "+destFile.getCanonicalPath());
					if(resource.getFile().isDirectory()){
						FileUtils.moveFileToDirectory(source.getFile(), destFile, true);
					}else{
						FileUtils.moveFile(source.getFile(), destFile);
					}
				}
				
			}catch(Exception e){
				if(stopOnError){
					if(e instanceof DataFileLoaderException){
						throw (DataFileLoaderException)e;
					}else{
						throw new DataFileLoaderException(e.getMessage(), e);
					}
				}else{
					logger.error(e.getMessage(), e);
				}
			}
		}//for each resource
	}
	
	/**
	 * Override this method if you run it in container-managed transaction
	 * In some framework such as Spring, Grails (service), EJB (session bean), etc.
	 * do a rollback when found that the system exception, which is extended 
	 * from RuntimeException, is thrown. 
	 * 
	 * onFinish method of DataFileProcessor may be used to detect the errors at the end
	 * of process. If errors are found, you may decide to throw the system exception 
	 * to rollback all items in file.
	 * 
	 * @param dataContext
	 * @param reader
	 * @param successWriter
	 * @param errorWriter
	 * @throws Exception
	 */
	protected void doReadProcessWrite(DataContext dataContext, DataFileReader reader, 
			DataFileWriter successWriter, DataFileWriter errorWriter) throws Exception{
		try{
			dataFileProcessor.onStart();
			_doReadProcessWrite(dataContext, reader, successWriter, errorWriter);
		}finally{
			dataFileProcessor.onFinish();
		}
	}
	
	/**
	 * 
	 * @param dataContext
	 * @param reader
	 * @param successWriter
	 * @param errorWriter
	 * @throws Exception
	 */
	private void _doReadProcessWrite(DataContext dataContext, DataFileReader reader, 
			DataFileWriter successWriter, DataFileWriter errorWriter) throws Exception{
		
		boolean successLabeled = false, errorLabeled = false;
		
		reader.open(dataContext);
		
		String labels[] = null, values[] = null;
		if(reader.isReadLabels()){
			labels = reader.read();					
		}
		
		int successCount = 0, lineCount = 0;
		while( (values = reader.read())!=null){
			lineCount++;
			dataContext.linesRead += reader.getLinesRead();
			FieldSet fieldSet = null;
			if(labels!=null){
				if(values.length> labels.length){
					List<String> vList = new ArrayList<String>();
					for(int i=0;i<labels.length;i++){
						vList.add(values[i]);
					}
					values = vList.toArray(new String[0]);
				}
				fieldSet = new DefaultFieldSet(values, labels);
			}else{
				fieldSet = new DefaultFieldSet(values);
			}
			
			FieldDataException fde = new FieldDataException();
			T item = null;
			try{
				if(Thread.currentThread().isInterrupted()){
					throw new InterruptedException("Current thread is interrupted");
				}
				item = dataFileProcessor.mapLine(fieldSet, fde);
				if(item==null){
					//skip
					continue;
				}
				dataFileProcessor.validateItem(item, fde);
				if(fde.hasErrors()){
					if(fde.isForceProcess()){
						dataFileProcessor.processItem(item);
					}
					throw fde;
				}
				dataFileProcessor.processItem(item);
				successCount++;
				if(successCount==updateInterval){
					successCount=0;//reset
					dataFileProcessor.update();
					reader.update(dataContext);
					if(successWriter!=null) successWriter.update(dataContext);
					if(errorWriter!=null) errorWriter.update(dataContext);
					//TODO: save dataContext here
				}
				dataContext.itemSuccessCount++;
				dataFileProcessor.onItemSuccess(item);
				if(!successLabeled && labels!=null){
					lazyOpenWriterThenWriteLabels(dataContext, successWriter, labels);
					successLabeled = true;
				}
				lazyOpenWriterThenWrite(dataContext, successWriter, values);
				if(successWriter!=null) dataContext.successLinesWritten+=successWriter.getLinesWritten();
			}catch(InterruptedException e){
				logger.info("Data processing is interrupted on file "
					+dataContext.getResource().getFilename());
				dataFileProcessor.onInterrupt(e);
				throw new DataFileLoaderException(e);
			}catch(Exception e){
				if( !(e instanceof FieldDataException) ){
					if(e instanceof CancelException){
						logger.info("Data processing is cancelled on file "
							+dataContext.getResource().getFilename());
						dataFileProcessor.onCancel((CancelException)e);
//						break;
						throw e;
					}
					logger.error(e.getMessage(), e);
				}
				dataContext.itemErrorCount++;
				dataFileProcessor.onItemError(item, e);
				
				if(!errorLabeled && labels!=null){
					lazyOpenWriterThenWriteLabels(dataContext, errorWriter, labels);
					errorLabeled = true;
				}
				String valuesWithErr[] = (String[])ArrayUtils.add(values, e.toString());				
				lazyOpenWriterThenWrite(dataContext, errorWriter, valuesWithErr);
				if(errorWriter!=null) dataContext.errorLinesWritten+=errorWriter.getLinesWritten();
			}finally{
				if(lineCount>0){
					//do again in case there are no success item.
					dataFileProcessor.update();
					reader.update(dataContext);
					if(successWriter!=null) successWriter.update(dataContext);
					if(errorWriter!=null) errorWriter.update(dataContext);
					//TODO: save dataContext here
				}
			}
		}//end while
	}
	
	/**
	 * 
	 * @param dataContext
	 * @param writer
	 * @param values
	 * @throws Exception
	 */
	private void lazyOpenWriterThenWrite(DataContext dataContext,
			DataFileWriter writer, String[] values) throws Exception{
		if(writer==null) return;
		if(!writer.isOpen()){
			writer.open(dataContext);
		}
		writer.write(values);		
	}
	
	/**
	 * 
	 * @param dataContext
	 * @param writer
	 * @param labels
	 * @throws Exception
	 */
	private void lazyOpenWriterThenWriteLabels(DataContext dataContext,
			DataFileWriter writer, String[] labels) throws Exception{
		if(writer==null) return;
		if(!writer.isOpen()){
			writer.open(dataContext);
		}
		writer.writeLabels(labels);
	}
	
	/**
	 * 
	 * @return
	 * @throws DataFileLoaderException
	 */
	private Resource[] getSources() throws DataFileLoaderException {
		
		Assert.notNull(resource, "resource is a required property");
		
		Resource sources[] = null;
		
		File resourceFile = null;
		
		try {
			
			resourceFile = resource.getFile();
			
			if(resourceFile.exists()){
				if(resourceFile.isDirectory()){
					if(destination!=null){
						if(!destination.getFile().exists()){
							if(!destination.getFile().mkdir()){
								throw new IOException("Failed to create directory: "+destination);
							}
						}
						if(!destination.getFile().isDirectory()){
							throw new IllegalArgumentException("resource and destination must be the same file type [file|directory].");
						}
					}
				}
			}else{
				throw new FileNotFoundException("Resource: "+resource+" not found.");
			}
			
			if(resourceFile.isDirectory()){
				File files[] = resourceFile.listFiles(directoryListingFilter);
				List<Resource> sourceList = new ArrayList<Resource>();
				for(int i=0;i<files.length;i++){
					if(files[i].isDirectory()) continue;
					//for now we support only FileSystemResource
					sourceList.add(new FileSystemResource(files[i].getCanonicalPath()));
				}
				sources = sourceList.toArray(new Resource[0]);
			}else{
				sources = new Resource[]{resource};
			}
			
		} catch (Exception e) {
			throw new DataFileLoaderException(e.getMessage(), e);
		}
		
		if (sources.length == 0) {
			logger.warn("No resources to read");
			return null;
		}
		
		Arrays.sort(sources, comparator);
		return sources;
	}
	
}
