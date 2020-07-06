package com.example.app;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class SpringBootBatchSampleConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SampleJobListener sampleJobListener;
	
	@Autowired
	private SampleStepListener sampleStepListener;
	
	
	/**
	 * reader
	 * 
	 * sampleテーブルから全レコードを取得する
	 */
	@StepScope
	@Bean
	public ItemStreamReader<SampleDto> reader( 
			@Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue) throws Exception {

		String selectSql = "SELECT * FROM dbo.sample WHERE ID >= " + minValue + " AND ID <= " + maxValue;
		JpaPagingItemReader<SampleDto> reader = new JpaPagingItemReader<SampleDto>();
		
		JpaNativeQueryProvider<SampleDto> queryProvider = new JpaNativeQueryProvider<SampleDto>();
		queryProvider.setSqlQuery(selectSql);
		queryProvider.setEntityClass(SampleDto.class);
		queryProvider.afterPropertiesSet();
		
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();
		reader.setSaveState(true);
		reader.setPageSize(1000000);
		
		return reader;
		
	}
	
	/**
	 * processor
	 * 
	 * readerで取得したデータの年齢に5を加算する
	 */
	@Bean
	public SampleBootBatchProcessor processor() {
		return new SampleBootBatchProcessor();
	}

	/**
	 * writer
	 * 
	 * processorで編集した値でsampleテーブルをupdateする
	 */
	@Bean
	public ItemWriter<SampleDto> writer(DataSource dataSource){
		String insertSql = "UPDATE dbo.sample SET age = :age WHERE name = :name";
		return new JdbcBatchItemWriterBuilder<SampleDto>()
    		    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
    	    	.sql(insertSql)
	        	.dataSource(dataSource)
	        	.build();
	}
	
	/**
	 * パーティショニング taskexecutor
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}

	/**
	 * stepの設定
	 */
	@Bean
	public Step batchStep() throws Exception {
		return stepBuilderFactory.get("batchStep")
				.<SampleDto, SampleDto> chunk(10000)
				.reader(reader(null, null))
				.processor(processor())
				.writer(writer(dataSource))
				.listener(sampleStepListener)
				.build();
	}
	
	/**
	 * partitioner
	 */
	@Bean
	public Partitioner partitioner() {
		ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
		partitioner.setDataSource(dataSource);
		partitioner.setTable("dbo.sample");
		partitioner.setColumn("ID");
		
		
		return partitioner;
	}
	
	/**
	 * stepのパーティショニング設定
	 * @throws Exception 
	 */
	@Bean
	public Step batchStepManager() throws Exception {
		return stepBuilderFactory.get("batchStep.manager")
				.<String, String> partitioner("batchStep", partitioner())
				.step(batchStep())
				.gridSize(2)
				.taskExecutor(taskExecutor())
				.build();
	}
	
	/**
	 * jobの設定
	 */
	@Bean
	public Job batchJob() throws Exception {
		return jobBuilderFactory.get("batchJob")
				.incrementer(new RunIdIncrementer())
				.start(batchStepManager())
				.listener(sampleJobListener)
				.build();
	}
}
