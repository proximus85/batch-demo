package org.home.batchdemo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/patient")
@Slf4j
public class PatientController {

    private final JobLauncher jobLauncher;
    private final Job job;

    @PostMapping
    public ResponseEntity savePatients() {
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("dupa", new JobParameter("wafel"));
        try {
            jobLauncher.run(this.job, new JobParameters(parameterMap));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        return ResponseEntity.ok()
                .build();
    }
}
