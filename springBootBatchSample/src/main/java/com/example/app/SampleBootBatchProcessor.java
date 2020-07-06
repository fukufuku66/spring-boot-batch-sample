package com.example.app;

import org.springframework.batch.item.ItemProcessor;

public class SampleBootBatchProcessor implements ItemProcessor<SampleDto, SampleDto> {
	
	@Override
	public SampleDto process(SampleDto item) throws Exception {
		
		// 5年の月日がたつ
		item.setAge(item.getAge() + 5);
		
		return item;
	}

}
