package com.example.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SampleJobListener extends JobExecutionListenerSupport{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SampleJobListener.class);
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		
		Calendar cTime = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
		
		LOGGER.info("【開始】 " + sdf.format(cTime.getTime()));
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
        
		Calendar cTime = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
		
		LOGGER.info("【終了】 " + sdf.format(cTime.getTime()));
	}

}
