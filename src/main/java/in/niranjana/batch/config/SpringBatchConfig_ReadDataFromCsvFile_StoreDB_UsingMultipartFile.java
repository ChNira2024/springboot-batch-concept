//package in.niranjana.batch.config;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.data.RepositoryItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.LineMapper;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import in.niranjana.batch.entity.Customer;
//import in.niranjana.batch.repo.CustomerRepository;
//import lombok.AllArgsConstructor;
//
//@Configuration
////@EnableBatchProcessing  //Spring Boot 3.x + Spring Batch 5 @EnableBatchProcessing ❌ should NOT be used
//@AllArgsConstructor
//public class SpringBatchConfig_ReadDataFromCsvFile_StoreDB_UsingMultipartFile {
//
//
//	private CustomerRepository customerRepository;
//
//	//Reader 
//	@Bean
//	@StepScope
//	public FlatFileItemReader<Customer> customerReader(
//	        @Value("#{jobParameters['filePath']}") String filePath) { //Spring Batch reader uses this path to read CSV.
//
//	    FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//	    itemReader.setResource(new FileSystemResource(filePath));
//	    itemReader.setName("csv-reader");
//	    itemReader.setLinesToSkip(1);
//	    itemReader.setLineMapper(lineMapper());
//	    return itemReader;
//	}
//
//	private LineMapper<Customer> lineMapper() {
//
//		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
//
//		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); //DelimitedLineTokenizer class helps to that logic for retrieve
//		lineTokenizer.setDelimiter(",");		//retrieve data by comma separater
//		lineTokenizer.setStrict(false);			//it tells..in csv file any column has no data..means empty then it consider as null
//		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");	//here mention header of csv file
//
//		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//		fieldSetMapper.setTargetType(Customer.class); //it convert to that java object
//		
//		
//		lineMapper.setLineTokenizer(lineTokenizer);
//		lineMapper.setFieldSetMapper(fieldSetMapper);
//
//		return lineMapper;
//	}
//
//	//Processor
//	@Bean
//	public CustomerProcessor customerProcessor() {
//		return new CustomerProcessor();//here after getting the details from csv, u can do some operation logic like based on gender male get detail..
//	}
//
//	//Writter
//	@Bean
//	public RepositoryItemWriter<Customer> customerWriter() {
//
//		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
//		writer.setRepository(customerRepository);  //then to store in db, i am suing JPA So here providing class and its save method
//		writer.setMethodName("save");
//
//		return writer;
//	}
//	
//	//Step
//	@Bean
//	public Step step1(JobRepository jobRepository,
//	                  PlatformTransactionManager transactionManager) {
//
//	    return new StepBuilder("step1", jobRepository)
//	            .<Customer, Customer>chunk(10, transactionManager)
//	            .reader(customerReader(null))
//	            .processor(customerProcessor())
//	            .writer(customerWriter())
//	            .build();
//	}
//
//	
//	//Job
//	@Bean
//    public Job importUserJob(JobRepository jobRepository, Step step1) {
//        return new JobBuilder("importUserJob", jobRepository) //JobBuilder helps to strat step
//                .start(step1) //mention the step name
//                .build();
//    }
//	
//	
//	
//	
//}
//
//
//
//
//
//
//
