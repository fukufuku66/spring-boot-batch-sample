package com.example.app;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

public class ColumnRangePartitioner implements Partitioner{
	
    private String table;
    private String column;
    private DataSource dataSource;
    private JdbcOperations jdbcTemplate;
 
    public void setTable(String table) {
        this.table = table;
    }
 
    public void setColumn(String column) {
        this.column = column;
    }
    
    public void setDataSource(DataSource dataSource) {
    	jdbcTemplate = new JdbcTemplate(dataSource);
    }
 
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) 
    {
    	long min = jdbcTemplate.queryForObject("SELECT ISNULL(MIN(" + column + "), 0) FROM " + table, long.class);
 
    	long max = jdbcTemplate.queryForObject("SELECT ISNULL(MAX(" + column + "), 0) FROM " + table, long.class);
 
    	long targetSize = (max - min) / gridSize + 1;
 
        Map<String, ExecutionContext> result = new HashMap<>();
 
        long number = 0;
        long start = min;
        long end = start + targetSize - 1;
         
        while (start <= max) 
        {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);
             
            if(end >= max) {
                end = max;
            }
             
            value.putLong("minValue", start);
            value.putLong("maxValue", end);
 
            start += targetSize;
            end += targetSize;
 
            number++;
        }
        return result;
    }
}
