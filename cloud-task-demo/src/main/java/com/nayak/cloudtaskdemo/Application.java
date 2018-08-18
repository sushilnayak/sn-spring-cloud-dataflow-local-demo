package com.nayak.cloudtaskdemo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableTask
@EnableBatchProcessing
@SpringBootApplication
public class Application implements ApplicationRunner {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters =
				new JobParametersBuilder()
						.addString("time", LocalDateTime.now().toString())
						.toJobParameters();

		jobLauncher.run(job, jobParameters);
	}
}

@Configuration
class JobConfiguration {
	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Bean
	public Step step1a() {
		return stepBuilderFactory.get("step1a").tasklet(new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution stepContribution,
					ChunkContext chunkContext) throws Exception {
				System.out.println("Helllllllllllo from Cloud Task!!!!!!!!!");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

	@Bean
	public Job job1a() {
		return jobBuilderFactory.get("job1a").start(step1a()).build();
	}
}
