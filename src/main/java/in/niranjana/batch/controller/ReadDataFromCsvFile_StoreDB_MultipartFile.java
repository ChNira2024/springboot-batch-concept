package in.niranjana.batch.controller;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ReadDataFromCsvFile_StoreDB_MultipartFile {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping(value = "/csv", consumes = "multipart/form-data")
    public String loadDataToDB(@RequestParam("file") MultipartFile file) throws Exception {
    	
    	System.out.println("File received: " + file.getOriginalFilename());

        // save uploaded file temporarily
        Path tempFile = Files.createTempFile("customers-", ".csv");
        file.transferTo(tempFile.toFile());

        JobParameters jobParams = new JobParametersBuilder()
					                .addString("filePath", tempFile.toAbsolutePath().toString())
					                .addLong("startAt", System.currentTimeMillis())
					                .toJobParameters();

        jobLauncher.run(job, jobParams);
        return "Batch job started successfully";
    }
}
/*
What is happening?
=====================
=>MultipartFile file → this comes from Postman / UI upload
=>Spring does NOT keep uploaded files permanently
=>Spring Batch cannot directly read MultipartFile
=>So we convert the uploaded file into a real file on disk

Line by line
✅ Files.createTempFile("customers-", ".csv")::-
=>Creates a temporary file in OS temp directory location: Windows → C:\Users\niran\AppData\Local\Temp\
                                                  Linux → /tmp/
=>Example filename:customers-845623781234.csv

✅ file.transferTo(tempFile.toFile())::-
=>Copies uploaded CSV content into that temp file
=>Now Spring Batch can read it like a normal file

✅ Why this is needed in real projects?
========================================
In real-time:
---------------
=>Files come from Postman / UI
=>But Batch needs file system path
=>This is the standard & correct approach ✔

JobParameters jobParams = new JobParametersBuilder()
        .addString("filePath", tempFile.toAbsolutePath().toString())
        .addLong("startAt", System.currentTimeMillis())
        .toJobParameters();
=>Absolute path of uploaded CSV file: C:\Users\Niranjana\AppData\Local\Temp\customers-1234.csv



In real-time applications, users upload files through REST APIs. Since Spring Batch works with file paths, we temporarily store the uploaded MultipartFile in the server filesystem and pass the absolute path as a JobParameter. This ensures batch jobs remain stateless, scalable, and rerunnable with unique parameters.
*/