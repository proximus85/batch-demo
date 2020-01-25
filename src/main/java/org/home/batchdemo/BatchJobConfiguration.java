package org.home.batchdemo;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManager;
import java.nio.file.Paths;

@Configuration
@AllArgsConstructor
public class BatchJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private EntityManager entityManager;

    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

    @Bean
    public Job job(Step step) {
        return this.jobBuilderFactory
                .get(Constants.JOB_NAME)
                .validator(validator())
                .start(step)
                .build();
    }

    @Bean
    public Step getStep(ItemReader<PatientRecord> itemReader,
                        ItemProcessor<PatientRecord, Patient> itemProcessor,
                        ItemWriter<Patient> itemWriter) {
        return this.stepBuilderFactory
                .get(Constants.STEP_NAME)
                .<PatientRecord, Patient>chunk(2)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public ItemReader<PatientRecord> getItemReader(LineMapper<PatientRecord> lineMapper) {
        return new FlatFileItemReaderBuilder<PatientRecord>()
                .name(Constants.LINE_MAPPER)
                .resource(new FileSystemResource(Paths.get("C:/dev/projects/batch-demo-final/src/main/resources/files/patientsSetOne")))
                .lineMapper(lineMapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public LineMapper<PatientRecord> getLineMapper() {
        DefaultLineMapper<PatientRecord> patientMapper = new DefaultLineMapper<>();
        patientMapper.setFieldSetMapper((fieldSet) -> new PatientRecord(fieldSet.readRawString(0)));
        patientMapper.setLineTokenizer(new DelimitedLineTokenizer());
        return patientMapper;
    }

    @Bean
    public ItemProcessor<PatientRecord, Patient> getItemProcessor() {
        return new FunctionItemProcessor<>(
                patientRecord -> new Patient(patientRecord.getFirstName())
        );
    }

    @Bean
    public ItemWriter<Patient> getItemWriter() {
        JpaItemWriter itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        return itemWriter;
    }

    @Bean
    public JobParametersValidator validator() {
        return parameters -> System.out.println("im validating some parameters");
    }
}
