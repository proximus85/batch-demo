package org.home.batchdemo;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class PatientResource {

    private final Job job;
    private final JobLauncher jobLauncher;

}
