package in.niranjana.batch.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadDataFromDB_StoreIntoCsv {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job exportCustomerJob;

    @GetMapping("/export")
    public String export() throws Exception {

        // Ensure directory exists
    	Path dir = Paths.get("C:/Niranjana");
    	Files.createDirectories(dir);   // safe even if folder exists

    	String filePath = "C:/Niranjana/customers_" + System.currentTimeMillis() + ".csv";


        JobParameters params = new JobParametersBuilder()
					                .addString("outputFile", filePath)
					                .addLong("time", System.currentTimeMillis())
					                .toJobParameters();

        jobLauncher.run(exportCustomerJob, params);
        return "Export started successfully. File created at: " + filePath;
    }
}
