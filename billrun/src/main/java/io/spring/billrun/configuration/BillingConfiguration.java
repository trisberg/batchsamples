package io.spring.billrun.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.billrun.model.Bill;
import io.spring.billrun.model.Usage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableTask
public class BillingConfiguration {

    @Value("${usage.file.name:classpath:usageinfo.json}")
    private Resource usageResource;

    @Bean
    public Job job1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                    ItemReader<Usage> reader, ItemProcessor<Usage, Bill> itemProcessor,
                    ItemWriter<Bill> writer) {
        Step step = new StepBuilder("BillProcessing", jobRepository)
                .<Usage, Bill>chunk(1, transactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();

        return new JobBuilder("BillJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public JsonItemReader<Usage> jsonItemReader() {

        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<Usage> jsonObjectReader =
                new JacksonJsonObjectReader<>(Usage.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Usage>()
                .jsonObjectReader(jsonObjectReader)
                .resource(usageResource)
                .name("UsageJsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
        JdbcBatchItemWriter<Bill> writer = new JdbcBatchItemWriterBuilder<Bill>()
                .beanMapped()
                .dataSource(dataSource)
                .sql("INSERT INTO BILL_STATEMENTS (id, first_name, " +
                        "last_name, minutes, data_usage,bill_amount) VALUES " +
                        "(:id, :firstName, :lastName, :minutes, :dataUsage, " +
                        ":billAmount)")
                .build();
        return writer;
    }

    @Bean
    ItemProcessor<Usage, Bill> billProcessor() {
        return new BillProcessor();
    }
}
