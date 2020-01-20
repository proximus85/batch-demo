package org.home.batchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.nio.file.Paths;
import java.util.function.Function;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public Job getJob() {
        return jobBuilderFactory.get("someJob")
                .start(getSomeStep())
                .build();
    }

    @Bean
    public Step getSomeStep() {
        return this.stepBuilderFactory.get("someStep")
                .<PatientRecord, Patient>chunk(2)
                .reader(patientReader())
                .processor(patientProcessor())
                .writer(patientJpaItemWriter())
                .build();
    }

    @Bean
    public Function<PatientRecord, Patient> patientProcessor() {
        return (patientRecord) -> new Patient(patientRecord.getFirstName());
    }

    @Bean
    public FlatFileItemReader<PatientRecord> patientReader() {
        return new FlatFileItemReaderBuilder<PatientRecord>()
                .name("patient flat file item reader")
                .resource(new FileSystemResource(Paths.get("C:/dev/projects/batch-demo/src/main/resources/patients/patientsSetOne")))
                .lineMapper(getLineMapper())
                .linesToSkip(1)
                .build();
    }

    @Bean
    public LineMapper<PatientRecord> getLineMapper() {
        DefaultLineMapper<PatientRecord> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSet -> new PatientRecord(
                fieldSet.readString(0)
        ));
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        return lineMapper;
    }

    @Bean
    public JpaItemWriter<Patient> patientJpaItemWriter() {
        return new JpaItemWriterBuilder<Patient>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
