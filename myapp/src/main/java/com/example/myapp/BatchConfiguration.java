package com.example.myapp;

import com.example.myapp.model.Person;
import com.example.myapp.model.Results;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableTask
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    public BatchConfiguration() throws Exception {
        log.info("TEST env var: {}", System.getenv("TEST"));
        log.info("SPRING_BATCH_JDBC_SCHEMA_LEGACY env var: {}", System.getenv("SPRING_BATCH_JDBC_SCHEMA_LEGACY"));
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
        DelimitedLineAggregator<Results> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // CSV delimiter

        BeanWrapperFieldExtractor<Results> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"firstName", "lastName"}); // Fields to extract in order
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Results>()
                .name("itemWriter")
                .resource(new FileSystemResource("/tmp/output/results.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer1 -> writer1.write("First Name,Last Name"))
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, FlatFileItemReader<Person> reader, DataItemProcessor processor,
                      FlatFileItemWriter<Results> writer, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Results>chunk(3)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

}
