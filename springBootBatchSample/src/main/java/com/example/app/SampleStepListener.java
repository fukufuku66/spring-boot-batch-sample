package com.example.app;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SampleStepListener extends StepExecutionListenerSupport{
	
	@Override
	public void beforeStep(StepExecution stepExecution)	{
		
	}
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution)	{
		
		int readCount = stepExecution.getReadCount();
		int writeCount = stepExecution.getWriteCount();
		
		System.out.println("読み込み件数：" + readCount);
		System.out.println("書き込み件数：" + writeCount);
		
		return ExitStatus.COMPLETED;
		
	}

}
