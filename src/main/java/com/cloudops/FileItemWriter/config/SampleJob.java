package com.cloudops.FileItemWriter.config;

import com.cloudops.FileItemWriter.model.StudentJdbc;
import com.cloudops.FileItemWriter.processor.FirstItemProcessor;
import com.cloudops.FileItemWriter.reader.FirstItemReader;
import com.cloudops.FileItemWriter.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

@Configuration
public class SampleJob {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    FirstItemReader firstItemReader;

    @Autowired
    FirstItemProcessor firstItemProcessor;

    @Autowired
    FirstItemWriter firstItemWriter;

    @Autowired
    DataSource dataSource;

    @Bean
    public Job chunkJob(){
        return new JobBuilder("Chunk Job",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .build();
    }

    private Step firstChunkStep() {
        return new StepBuilder("First Chunk Step",jobRepository)
                .<StudentJdbc, StudentJdbc>chunk(3,platformTransactionManager)
                .reader(jdbcJdbcCursorItemReader())
                //.processor(firstItemProcessor)
                .writer(flatFileItemWriter())
                .build();
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcJdbcCursorItemReader(){
        JdbcCursorItemReader<StudentJdbc> jdbcItemReader = new JdbcCursorItemReader<>();
        jdbcItemReader.setDataSource(dataSource);
        jdbcItemReader.setSql("SELECT id, first_name, last_name, email FROM student");
        BeanPropertyRowMapper<StudentJdbc> beanPropertyRowMapper = new BeanPropertyRowMapper<>();
        beanPropertyRowMapper.setMappedClass(StudentJdbc.class);
        jdbcItemReader.setRowMapper(beanPropertyRowMapper);
        jdbcItemReader.setMaxItemCount(9);
        jdbcItemReader.setCurrentItemCount(2);
        return jdbcItemReader;
    }

    public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(){
        FlatFileItemWriter<StudentJdbc> fileItemWriter = new FlatFileItemWriter<>();
        fileItemWriter.setResource(new FileSystemResource(new File("D:\\Spring Projects\\Spring Batch Projects\\FileItemWriter\\src\\main\\java\\input\\student.csv")));
        fileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id,First Name,Last Name,Email");
            }
        });

        fileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentJdbc>(){{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<StudentJdbc>(){{
                setNames(new String[]{"id","first_name","last_name","email"});
            }});
        }});

        fileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created @"+new Date());
            }
        });
        return fileItemWriter;
    }
}
