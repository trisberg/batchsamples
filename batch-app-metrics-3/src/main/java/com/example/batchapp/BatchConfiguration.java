package com.example.batchapp;

import com.example.batchapp.model.Person;
import com.example.batchapp.model.Results;

import io.micrometer.core.instrument.config.MeterFilter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableTask
public class BatchConfiguration {

    @Bean
    MeterFilter dropLegacyBatchJobActive() {
        return MeterFilter.deny(id -> "spring.batch.job.active".equals(id.getName())
                && id.getTag("spring.batch.job.active.name") != null);
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(new ClassPathResource("./data/file-1.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .build();
    }

    @Bean
    public DataItemProcessor processor() {
        return new DataItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Results> writer() {
        FlatFileItemWriter<Results> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("/tmp/output/results.csv")); // Output file path
        writer.setAppendAllowed(false); // Overwrite the file if it exists, set to true to append

        DelimitedLineAggregator<Results> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // CSV delimiter

        BeanWrapperFieldExtractor<Results> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"firstName", "lastName"}); // Fields to extract in order
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);

        // Optional: Add a header to the file
        writer.setHeaderCallback(writer1 -> writer1.write("First Name,Last Name"));

        return writer;
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("jdbcImportUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, FlatFileItemReader<Person> reader, DataItemProcessor processor,
                      FlatFileItemWriter<Results> writer, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Results>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ExecutionContextSerializer executionContextSerializer() {
        return new Jackson2ExecutionContextStringSerializer();
    }

}
