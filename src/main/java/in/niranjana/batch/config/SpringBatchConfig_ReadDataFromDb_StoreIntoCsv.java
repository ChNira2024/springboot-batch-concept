package in.niranjana.batch.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import in.niranjana.batch.entity.Customer;

@Configuration
public class SpringBatchConfig_ReadDataFromDb_StoreIntoCsv {

    /* =======================
       READER : DB → OBJECT
       ======================= */
    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> customerReader(DataSource dataSource) {

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(10);
        reader.setRowMapper(new BeanPropertyRowMapper<>(Customer.class));

        PostgresPagingQueryProvider queryProvider =
                new PostgresPagingQueryProvider();

        // ✅ MATCH DB COLUMN NAMES
        queryProvider.setSelectClause(
                "CUSTOMER_ID, FIRST_NAME, LAST_NAME, EMAIL, GENDER, CONTACT, COUNTRY, DOB");

        queryProvider.setFromClause("from CUSTOMERS_INFO");

        queryProvider.setSortKeys(
                Map.of("CUSTOMER_ID", Order.ASCENDING));

        reader.setQueryProvider(queryProvider);
        return reader;
    }

    /* =======================
       WRITER : OBJECT → CSV
       ======================= */
    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> customerWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile) {

        System.out.println("CSV Output File: " + outputFile);

        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(outputFile));
        writer.setAppendAllowed(false);

        DelimitedLineAggregator<Customer> aggregator =
                new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<Customer> extractor =
                new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{
                "id", "firstName", "lastName",
                "email", "gender", "contactNo",
                "country", "dob"
        });

        aggregator.setFieldExtractor(extractor);
        writer.setLineAggregator(aggregator);

        writer.setHeaderCallback(
                w -> w.write("ID,FIRST_NAME,LAST_NAME,EMAIL,GENDER,CONTACT_NO,COUNTRY,DOB"));

        return writer;
    }

    /* =======================
       STEP
       ======================= */
    @Bean
    public Step exportStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcPagingItemReader<Customer> customerReader,
                           FlatFileItemWriter<Customer> customerWriter) {

        return new StepBuilder("exportStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customerReader)
                .writer(customerWriter)
                .build();
    }

    /* =======================
       JOB
       ======================= */
    @Bean
    public Job exportCustomerJob(JobRepository jobRepository,
                                 Step exportStep) {

        return new JobBuilder("exportCustomerJob", jobRepository)
                .start(exportStep)
                .build();
    }
}
