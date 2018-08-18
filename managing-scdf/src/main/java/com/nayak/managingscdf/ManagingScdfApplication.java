package com.nayak.managingscdf;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.rest.client.DataFlowTemplate;
import org.springframework.cloud.dataflow.rest.client.TaskOperations;
import org.springframework.cloud.dataflow.rest.client.VndErrorResponseErrorHandler;
import org.springframework.cloud.dataflow.rest.client.support.ExecutionContextJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.ExitStatusJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.JobExecutionJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.JobInstanceJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.JobParameterJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.JobParametersJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.StepExecutionHistoryJacksonMixIn;
import org.springframework.cloud.dataflow.rest.client.support.StepExecutionJacksonMixIn;
import org.springframework.cloud.dataflow.rest.job.StepExecutionHistory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ManagingScdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagingScdfApplication.class, args);
	}
}

@Slf4j
@RestController
class JobController{


	@Autowired
	RestTemplate restTemplate;


	@PostMapping(value = "/task")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity runTask(@RequestParam("task") String task){
		URI dataFlowUri = URI.create("http://localhost:9393/");

		String name = task + "-" + System.currentTimeMillis();

		DataFlowTemplate dataFlowTemplate = new DataFlowTemplate(dataFlowUri, restTemplate);
		TaskOperations taskOperations = dataFlowTemplate.taskOperations();

		taskOperations.create(name, task );

		Map<String, String> properties = Collections.EMPTY_MAP;
		List<String> arguments = Arrays.asList("input=in", "output=out", "sushil=nayak");

		taskOperations.launch(name, properties, arguments);

		log.info(String.format("Task %s submitted as composed task %s", task, name));


		return ResponseEntity.ok().build();
	}
}

@Configuration
class MainConfig{
	@Bean
	public static RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new VndErrorResponseErrorHandler(restTemplate.getMessageConverters()));
		for(HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				final MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;
				jacksonConverter.getObjectMapper()
						.registerModule(new Jackson2HalModule())
						.addMixIn(JobExecution.class, JobExecutionJacksonMixIn.class)
						.addMixIn(JobParameters.class, JobParametersJacksonMixIn.class)
						.addMixIn(JobParameter.class, JobParameterJacksonMixIn.class)
						.addMixIn(JobInstance.class, JobInstanceJacksonMixIn.class)
						.addMixIn(ExitStatus.class, ExitStatusJacksonMixIn.class)
						.addMixIn(StepExecution.class, StepExecutionJacksonMixIn.class)
						.addMixIn(ExecutionContext.class, ExecutionContextJacksonMixIn.class)
						.addMixIn(StepExecutionHistory.class, StepExecutionHistoryJacksonMixIn.class);
			}
		}
		return restTemplate;
	}
}